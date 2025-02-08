package asia.virtualmc.vArchaeology.storage;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.tasks.TaskManager;
import asia.virtualmc.vLibrary.VLibrary;
import asia.virtualmc.vLibrary.storage.OtherDataLib;
import asia.virtualmc.vLibrary.storage.PlayerDataLib;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class StorageManager {
    private final Main plugin;
    private final PlayerData playerData;
    private final Statistics statistics;
    private final VLibrary vlib;
    private final PlayerDataLib playerDataLib;
    private final OtherDataLib otherDataLib;
    private final TaskManager taskManager;

    public StorageManager(@NotNull Main plugin, @NotNull VLibrary vlib, TaskManager taskManager) {
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

    public OtherDataLib getOtherDataLib() { return otherDataLib; }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public void loadPlayerAllData(@NotNull UUID uuid, String name) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                playerData.loadPlayerData(uuid);
                statistics.loadPlayerData(uuid);
            } catch (Exception e) {
                plugin.getLogger().severe(Main.prefix + "Error loading data for " + name + ": " + e.getMessage());
            }
        });
    }

    public void unloadPlayerAllData(@NotNull UUID uuid, String name) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                playerData.unloadData(uuid);
                statistics.unloadData(uuid);
            } catch (Exception e) {
                plugin.getLogger().severe(Main.prefix + "Error unloading data for " + name + ": " + e.getMessage());
            }
        });
    }

    public void saveAllPluginData() {
        try {
            playerData.updateAllData();
            statistics.updateAllData();
        } catch (Exception e) {
            plugin.getLogger().severe(Main.prefix + "An error occurred while updating data to database.");
        }
    }
}
