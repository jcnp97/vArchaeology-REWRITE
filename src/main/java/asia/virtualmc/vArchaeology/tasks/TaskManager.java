package asia.virtualmc.vArchaeology.tasks;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.events.EventManager;
import asia.virtualmc.vArchaeology.global.GlobalManager;
import asia.virtualmc.vArchaeology.storage.StorageManager;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class TaskManager {
    private final Main plugin;
    private final BossBarUpdater bossBarUpdater;
    private final EventManager eventManager;
    private final StorageManager storageManager;

    public TaskManager(@NotNull Main plugin) {
        this.plugin = plugin;
        this.bossBarUpdater = new BossBarUpdater(this);
        this.eventManager = plugin.getEventManager();
        this.storageManager = plugin.getStorageManager();

        startTasks();
    }

    private void startTasks() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, bossBarUpdater::sendBossBarToAll, 0L, 300L);
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::repeatingTask1, 0L, 12000L);
    }

    public Main getMain() {
        return plugin;
    }

    public BossBarUpdater getBossBarUpdater() {
        return bossBarUpdater;
    }

    // All tasks
    private void repeatingTask1() {
        try {
            eventManager.getArtefactDiscoveryProgress().cleanupExpired();
        } catch (Exception e) {
            plugin.getLogger().severe(GlobalManager.prefix + "An error occurred while unloading ADP map: " + e.getMessage());
        }
        storageManager.saveAllPluginData();
    }
}
