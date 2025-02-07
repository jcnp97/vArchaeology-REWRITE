package asia.virtualmc.vArchaeology.storage;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.tasks.TaskManager;
import asia.virtualmc.vLibrary.VLibrary;
import asia.virtualmc.vLibrary.storage.PlayerDataLib;

public class StorageManager {
    private final Main plugin;
    private final PlayerData playerData;
    private final VLibrary vlib;
    private final PlayerDataLib playerDataLib;
    private final TaskManager taskManager;

    public StorageManager(Main plugin, VLibrary vlib, TaskManager taskManager) {
        this.plugin = plugin;
        this.vlib = vlib;
        this.playerDataLib = vlib.getPlayerDataLib();
        this.taskManager = taskManager;
        this.playerData = new PlayerData(this);
    }

    public Main getMain() {
        return plugin;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public PlayerDataLib getPlayerDataLib() {
        return playerDataLib;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }
}
