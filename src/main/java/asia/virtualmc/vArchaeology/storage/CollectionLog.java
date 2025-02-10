package asia.virtualmc.vArchaeology.storage;

import asia.virtualmc.vArchaeology.Main;

import asia.virtualmc.vArchaeology.global.GlobalManager;
import asia.virtualmc.vLibrary.configs.CollectionLogConfig;
import asia.virtualmc.vLibrary.interfaces.OtherDataHandlingLib;
import asia.virtualmc.vLibrary.storage.OtherDataLib;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CollectionLog implements OtherDataHandlingLib {
    private final Main plugin;
    private final OtherDataLib otherDataLib;
    private final String tableName = "varch_collection";
    private final ConcurrentHashMap<UUID, ConcurrentHashMap<Integer, Integer>> collectionsMap;

    public CollectionLog(@NotNull StorageManager storageManager) {
        this.plugin = storageManager.getMain();
        this.otherDataLib = storageManager.getOtherDataLib();
        this.collectionsMap = new ConcurrentHashMap<>();
        createTable();
    }

    private void createTable() {
        List<String> statList = CollectionLogConfig.loadCollections(plugin);
        otherDataLib.createTable(statList, tableName, GlobalManager.prefix);
    }

    @Override
    public void loadPlayerData(@NotNull UUID uuid) {
        String name = Bukkit.getPlayer(uuid).getName();
        try {
            collectionsMap.put(uuid, otherDataLib.loadPlayerData(uuid, tableName, GlobalManager.prefix));
        } catch (Exception e) {
            plugin.getLogger().severe(GlobalManager.prefix + "Failed to load data to hashmap (" + tableName + ") for " + name +  " : " + e.getMessage());
        }
    }

    @Override
    public void updatePlayerData(@NotNull UUID uuid) {
        Map<Integer, Integer> stats = collectionsMap.get(uuid);
        if (stats == null) {
            return;
        }
        try {
            otherDataLib.savePlayerData(uuid, tableName, stats, GlobalManager.prefix);
        } catch (Exception e) {
            plugin.getLogger().severe(
                    GlobalManager.prefix + "Failed to save data for player " + uuid + "on " + tableName + " : " + e.getMessage());
        }
    }

    @Override
    public void updateAllData() {
        try {
            otherDataLib.saveAllData(tableName, collectionsMap, GlobalManager.prefix);
        } catch (Exception e) {
            plugin.getLogger().severe(GlobalManager.prefix + "Failed to save all data to database: " + e.getMessage());
        }
    }

    @Override
    public void unloadData(@NotNull UUID uuid) {
        try {
            updatePlayerData(uuid);
            collectionsMap.remove(uuid);
        } catch (Exception e) {
            plugin.getLogger().severe(
                    GlobalManager.prefix + "Failed to save data for player " + uuid + "on " + tableName + " : " + e.getMessage());
        }
    }

    public int getDataFromMap(@NotNull UUID uuid, int dataID) {
        return collectionsMap.getOrDefault(uuid, new ConcurrentHashMap<>())
                .getOrDefault(dataID, 0);
    }

    public Map<UUID, Map<Integer, Integer>> getAllDataFromMap() {
        return new ConcurrentHashMap<>(collectionsMap);
    }

    public void incrementData(@NotNull UUID uuid, int dataID) {
        collectionsMap.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>())
                .merge(dataID, 1, Integer::sum);
    }

    public void addCustomValueData(@NotNull UUID uuid, int dataID, int value) {
        collectionsMap.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>())
                .merge(dataID, value, Integer::sum);
    }
}