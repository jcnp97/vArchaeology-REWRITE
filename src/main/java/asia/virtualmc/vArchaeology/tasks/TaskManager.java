package asia.virtualmc.vArchaeology.tasks;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.tasks.BossBarUpdater;
import asia.virtualmc.vLibrary.VLibrary;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class TaskManager {
    private final Main plugin;
    private final BossBarUpdater bossBarUpdater;


    public TaskManager(@NotNull Main plugin) {
        this.plugin = plugin;
        this.bossBarUpdater = new BossBarUpdater(this);
        startTasks();
    }

    private void startTasks() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, bossBarUpdater::sendBossBarToAll, 0L, 300L);
    }

    public Main getMain() {
        return plugin;
    }

    public BossBarUpdater getBossBarUpdater() {
        return bossBarUpdater;
    }
}
