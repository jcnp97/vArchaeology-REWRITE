package asia.virtualmc.vArchaeology.storage;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.tasks.TaskManager;
import asia.virtualmc.vLibrary.VLibrary;
import asia.virtualmc.vLibrary.storage.OtherDataLib;
import asia.virtualmc.vLibrary.storage.PlayerDataLib;

public class StorageManager {
    private final Main plugin;
    private final PlayerData playerData;
    private final Statistics statistics;
    private final VLibrary vlib;
    private final PlayerDataLib playerDataLib;
    private final OtherDataLib otherDataLib;
    private final TaskManager taskManager;

    public StorageManager(Main plugin, VLibrary vlib, TaskManager taskManager) {
        this.plugin = plugin;
        this.vlib = vlib;
        this.playerDataLib = vlib.getPlayerDataLib();
        this.otherDataLib = vlib.getOtherDataLib();
        this.taskManager = taskManager;
        this.playerData = new PlayerData(this);
        this.statistics = new Statistics(this);
    }

    public Main getMain() {
        return plugin;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public PlayerDataLib getPlayerDataLib() {
        return playerDataLib;
    }

    public OtherDataLib getOtherDataLib() {
        return otherDataLib;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }
}
