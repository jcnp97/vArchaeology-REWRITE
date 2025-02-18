package asia.virtualmc.vArchaeology.core;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.scheduler.JavaScheduler;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityStatus;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class FishingGame implements Listener {
    private final Main plugin;
    private final Map<Player, MinigameState> activeMinigames = new ConcurrentHashMap<>();
    private final Map<Player, Long> fishingCooldown = new ConcurrentHashMap<>();
    private static final int DISPLAY_MAX = 30;

    public FishingGame(@NotNull CoreManager coreManager) {
        this.plugin = coreManager.getMain();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        PlayerFishEvent.State state = event.getState();

        if (state == PlayerFishEvent.State.FISHING) {
            return;
        }

        if (state == PlayerFishEvent.State.BITE) {
            if (activeMinigames.containsKey(player)) {
                return;
            }

            Long cd = fishingCooldown.get(player);
            if (cd != null && System.currentTimeMillis() < cd) {
                event.setCancelled(true);
                player.sendMessage("§eYou must wait a few seconds before fishing again!");
                return;
            }

            startFishingMinigame(player, 7, 2, 20, 10);
            return;
        }

        if (activeMinigames.containsKey(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        MinigameState game = activeMinigames.get(player);

        if (game == null) {
            return;
        }

        if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            event.setCancelled(true);
            return;
        }

        // Schedule the click handling on the main thread
        new BukkitRunnable() {
            @Override
            public void run() {
                game.onPlayerClick();
            }
        }.runTask(plugin);

        event.setCancelled(true);
    }

    public void startFishingMinigame(Player player, int greenBarWidth, int arrowSpeed, int progressGain, int difficulty) {
        if (greenBarWidth < 1 || greenBarWidth > DISPLAY_MAX) {
            throw new IllegalArgumentException("Green bar width must be between 1 and " + DISPLAY_MAX);
        }
        if (arrowSpeed < 1) {
            throw new IllegalArgumentException("Arrow speed must be positive");
        }
        if (progressGain < 1 || progressGain > 100) {
            throw new IllegalArgumentException("Progress gain must be between 1 and 100");
        }

        MinigameState existing = activeMinigames.get(player);
        if (existing != null) {
            existing.endGame();
        }

        MinigameState game = new MinigameState(player, greenBarWidth, arrowSpeed, progressGain, difficulty);
        activeMinigames.put(player, game);
        game.start();
    }

    private class MinigameState {
        private final Player player;
        private final AtomicInteger arrowPosition = new AtomicInteger(0);
        private final AtomicInteger progress = new AtomicInteger(0);
        private final AtomicInteger greenBarStart = new AtomicInteger(0);
        private final AtomicInteger greenBarEnd = new AtomicInteger(0);
        private final int greenBarWidth;
        private final int arrowSpeed;
        private final int progressGain;
        private final int difficulty;
        private final AtomicBoolean running = new AtomicBoolean(false);
        private final AtomicBoolean movingRight = new AtomicBoolean(true);
        private final Random random = new Random();
        private JavaScheduler.SchedulerTask task;

        private static final int MIN_DIFFICULTY = 1;
        private static final int MAX_DIFFICULTY = 10;
        private static final double minSpeed = 1.0;
        private static final double maxSpeed = 10.0;

        public MinigameState(Player player, int greenBarWidth, int arrowSpeed, int progressGain, int difficulty) {
            this.player = player;
            this.greenBarWidth = greenBarWidth;
            this.arrowSpeed = arrowSpeed;
            this.progressGain = progressGain;
            this.difficulty = difficulty;
        }

        public void start() {
            running.set(true);
            arrowPosition.set(0);
            progress.set(0);
            movingRight.set(true);
            randomizeGreenBar();
            displayGame();
            arrangeTask();
        }

        public void onPlayerClick() {
            if (!running.get()) {
                return;
            }

            int currentPosition = arrowPosition.get();
            int currentStart = greenBarStart.get();
            int currentEnd = greenBarEnd.get();

            if (currentPosition >= currentStart && currentPosition <= currentEnd) {
                int newProgress = Math.min(100, progress.addAndGet(progressGain));
                if (newProgress >= 100) {
                    endGame();
                    return;
                }
                randomizeGreenBar();
                // Ensure UI updates happen on the main thread
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        displayGame();
                    }
                }.runTask(plugin);
            }
        }

        private void arrangeTask() {
            long period = mapValueToIntervalMilliseconds(difficulty);
            task = plugin.getScheduler().asyncRepeating(() -> {
                if (!running.get()) {
                    return;
                }
                // Calculate new position asynchronously
                updateArrowPosition();
                // Update UI on the main thread
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        displayGame();
                    }
                }.runTask(plugin);
            }, period, period, TimeUnit.MILLISECONDS);
        }

        private void updateArrowPosition() {
            if (movingRight.get()) {
                int newPos = arrowPosition.addAndGet(arrowSpeed);
                if (newPos >= DISPLAY_MAX) {
                    arrowPosition.set(DISPLAY_MAX);
                    movingRight.set(false);
                }
            } else {
                int newPos = arrowPosition.addAndGet(-arrowSpeed);
                if (newPos <= 0) {
                    arrowPosition.set(0);
                    movingRight.set(true);
                }
            }
        }

        private long mapValueToIntervalMilliseconds(int value) {
            double frequency = minSpeed + ((double) (value - MIN_DIFFICULTY) / (MAX_DIFFICULTY - MIN_DIFFICULTY)) * (maxSpeed - minSpeed);
            return (long) (1_000 / frequency);
        }

        private void displayGame() {
            if (!running.get()) return;

            StringBuilder bar = new StringBuilder();
            int currentPosition = arrowPosition.get();
            int currentStart = greenBarStart.get();
            int currentEnd = greenBarEnd.get();
            boolean isMovingRight = movingRight.get();

            for (int i = 0; i <= DISPLAY_MAX; i++) {
                if (i == currentPosition) {
                    bar.append(isMovingRight ? "§f►" : "§f◄");
                } else if (i >= currentStart && i <= currentEnd) {
                    bar.append("§a❙");
                } else {
                    bar.append("§7❙");
                }
            }

            int progressSegments = progress.get() / 10;
            StringBuilder progressBar = new StringBuilder("Progress: [");
            progressBar.append("§a").append("❙".repeat(progressSegments));
            progressBar.append("§7").append("❙".repeat(10 - progressSegments));
            progressBar.append("§f]");

            Component titleComponent = Component.text(bar.toString());
            Component subtitleComponent = Component.text(progressBar.toString());
            Title.Times times = Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(1), Duration.ofMillis(0));
            Title title = Title.title(titleComponent, subtitleComponent, times);
            player.showTitle(title);
        }

        private void randomizeGreenBar() {
            int maxStart = DISPLAY_MAX - greenBarWidth;
            int start = random.nextInt(maxStart + 1);
            greenBarStart.set(start);
            greenBarEnd.set(start + greenBarWidth - 1);
        }

        public void endGame() {
            if (!running.compareAndSet(true, false)) {
                return; // Already ended
            }

            activeMinigames.remove(player);
            fishingCooldown.put(player, System.currentTimeMillis() + 5000);

            if (task != null) {
                task.cancel();
            }

            // Ensure cleanup happens on the main thread
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.getFishHook() != null) {
                        player.getFishHook().remove();
                    }

                    WrapperPlayServerEntityStatus fishingPacket = new WrapperPlayServerEntityStatus(
                            player.getEntityId(),
                            (byte) 0x0E
                    );
                    PacketEvents.getAPI().getPlayerManager().sendPacket(player, fishingPacket);

                    player.swingMainHand();
                    player.playSound(player.getLocation(), Sound.ENTITY_FISHING_BOBBER_RETRIEVE, 1f, 1f);

                    Component titleComponent = Component.text("§aFishing Complete!");
                    Component subtitleComponent = Component.text("§eYou caught something!");
                    Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(2), Duration.ofMillis(500));
                    Title title = Title.title(titleComponent, subtitleComponent, times);
                    player.showTitle(title);

                    player.sendMessage("§aFishing successful!");
                }
            }.runTask(plugin);
        }
    }
}