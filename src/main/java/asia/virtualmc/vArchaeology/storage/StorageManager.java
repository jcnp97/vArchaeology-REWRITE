package asia.virtualmc.vArchaeology.storage;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.global.GlobalManager;
import asia.virtualmc.vLibrary.VLibrary;
import asia.virtualmc.vLibrary.storage.OtherDataLib;
import asia.virtualmc.vLibrary.storage.PlayerDataLib;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class StorageManager {
    private final Main plugin;
    private final PlayerData playerData;
    private final Statistics statistics;
    private final CollectionLog collectionLog;
    private final TalentTree talentTree;
    private final VLibrary vlib;
    private final PlayerDataLib playerDataLib;
    private final OtherDataLib otherDataLib;

    public StorageManager(@NotNull Main plugin) {
        this.plugin = plugin;
        this.vlib = plugin.getVLibrary();
        this.playerDataLib = vlib.getPlayerDataLib();
        this.otherDataLib = vlib.getOtherDataLib();
        this.playerData = new PlayerData(this);
        this.statistics = new Statistics(this);
        this.collectionLog = new CollectionLog(this);
        this.talentTree = new TalentTree(this);
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

    public TalentTree getTalentTree() {
        return talentTree;
    }

    public CollectionLog getCollectionLog() {
        return collectionLog;
    }

    public PlayerDataLib getPlayerDataLib() {
        return playerDataLib;
    }

    public OtherDataLib getOtherDataLib() { return otherDataLib; }

    public void loadPlayerAllData(@NotNull UUID uuid, String name) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                playerData.loadPlayerData(uuid);
                statistics.loadPlayerData(uuid);
                collectionLog.loadPlayerData(uuid);
                talentTree.loadPlayerData(uuid);
            } catch (Exception e) {
                plugin.getLogger().severe(GlobalManager.prefix + "Error loading data for " + name + ": " + e.getMessage());
            }
        });
    }

    public void unloadPlayerAllData(@NotNull UUID uuid, String name) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                playerData.unloadData(uuid);
                statistics.unloadData(uuid);
                collectionLog.unloadData(uuid);
                talentTree.unloadData(uuid);
            } catch (Exception e) {
                plugin.getLogger().severe(GlobalManager.prefix + "Error unloading data for " + name + ": " + e.getMessage());
            }
        });
    }

    public void saveAllPluginData() {
        try {
            playerData.updateAllData();
            statistics.updateAllData();
            collectionLog.updateAllData();
            talentTree.updateAllData();
        } catch (Exception e) {
            plugin.getLogger().severe(GlobalManager.prefix + "An error occurred while updating data to database.");
        }
    }
}
