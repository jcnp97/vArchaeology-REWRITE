package asia.virtualmc.vArchaeology.tasks;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.events.EventManager;
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
        this.storageManager = plugin.getStorageManager();
        this.eventManager = plugin.getEventManager();
        this.bossBarUpdater = new BossBarUpdater(this);

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
        eventManager.maintenanceTasks();
        storageManager.saveAllPluginData();
    }
}
