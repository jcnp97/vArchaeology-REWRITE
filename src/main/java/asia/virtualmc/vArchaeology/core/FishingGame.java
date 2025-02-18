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
import org.bukkit.scheduler.BukkitScheduler;

import java.time.Duration;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class FishingGame implements Listener {
    private final Main plugin;
    private final Map<Player, MinigameState> activeMinigames = new ConcurrentHashMap<>();
    private final Map<Player, Long> fishingCooldown = new ConcurrentHashMap<>();
    private static final int DISPLAY_MAX = 30;
    private final ForkJoinPool forkJoinPool = new ForkJoinPool();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public FishingGame(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();

        if (event.getState() == PlayerFishEvent.State.BITE) {
            if (activeMinigames.containsKey(player)) {
                return;
            }

            Long cd = fishingCooldown.get(player);
            if (cd != null && System.currentTimeMillis() < cd) {
                event.setCancelled(true);
                player.sendMessage("§eWait a moment before fishing again!");
                return;
            }

            startFishingMinigame(player, 3, 1, 100, 20);
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

        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            forkJoinPool.execute(game::onPlayerClick);
            //event.setCancelled(true);
        }
    }

    public void startFishingMinigame(Player player, int greenBarWidth, int arrowSpeed, int progressGain, int difficulty) {
        if (activeMinigames.containsKey(player)) {
            activeMinigames.get(player).endGame();
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

        public MinigameState(Player player, int greenBarWidth, int arrowSpeed, int progressGain, int difficulty) {
            this.player = player;
            this.greenBarWidth = greenBarWidth;
            this.arrowSpeed = arrowSpeed;
            this.progressGain = progressGain;
            this.difficulty = difficulty;
        }

        public void start() {
            running.set(true);
            randomizeGreenBar();
            scheduler.scheduleAtFixedRate(this::gameTick, 0, mapValueToIntervalMilliseconds(difficulty), TimeUnit.MILLISECONDS);
        }

        public void onPlayerClick() {
            if (!running.get()) return;

            int pos = arrowPosition.get();
            if (pos >= greenBarStart.get() && pos <= greenBarEnd.get()) {
                int newProgress = Math.min(100, progress.addAndGet(progressGain));

                if (newProgress >= 100) {
                    endGame();
                    return;
                }

                randomizeGreenBar();
                plugin.getServer().getScheduler().runTask(plugin, this::displayGame);
            }
        }

        private void gameTick() {
            if (!running.get()) return;

            updateArrowPosition();
            plugin.getServer().getScheduler().runTask(plugin, this::displayGame);
        }

        private void updateArrowPosition() {
            int current = arrowPosition.get();
            if (movingRight.get()) {
                if (current + arrowSpeed > DISPLAY_MAX) {
                    arrowPosition.set(DISPLAY_MAX);
                    movingRight.set(false);
                } else {
                    arrowPosition.addAndGet(arrowSpeed);
                }
            } else {
                if (current - arrowSpeed < 0) {
                    arrowPosition.set(0);
                    movingRight.set(true);
                } else {
                    arrowPosition.addAndGet(-arrowSpeed);
                }
            }
        }

        private long mapValueToIntervalMilliseconds(int value) {
            int baseSpeed = 1000;
            return Math.max(100, baseSpeed - (value * 80));
        }

        private void displayGame() {
            if (!running.get()) return;

            StringBuilder bar = new StringBuilder();
            int pos = arrowPosition.get();

            for (int i = 0; i <= DISPLAY_MAX; i++) {
                if (i == pos) {
                    bar.append(movingRight.get() ? "§f►" : "§f◄");
                } else if (i >= greenBarStart.get() && i <= greenBarEnd.get()) {
                    bar.append("§a❙");
                } else {
                    bar.append("§7❙");
                }
            }

            int progressSegments = progress.get() / 10;
            String progressBar = "Progress: [" + "§a" + "❙".repeat(progressSegments) +
                    "§7" + "❙".repeat(10 - progressSegments) +
                    "]";

            Component titleComponent = Component.text(bar.toString());
            Component subtitleComponent = Component.text(progressBar);
            Title.Times times = Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(1), Duration.ofMillis(0));
            Title title = Title.title(titleComponent, subtitleComponent, times);

            player.showTitle(title);
        }

        private void randomizeGreenBar() {
            int start = random.nextInt(DISPLAY_MAX - greenBarWidth + 1);
            greenBarStart.set(start);
            greenBarEnd.set(start + greenBarWidth - 1);
        }

        public void endGame() {
            running.set(false);
            activeMinigames.remove(player);
            fishingCooldown.put(player, System.currentTimeMillis() + 5000);

            plugin.getServer().getScheduler().runTask(plugin, () -> {
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
            });
        }
    }
}