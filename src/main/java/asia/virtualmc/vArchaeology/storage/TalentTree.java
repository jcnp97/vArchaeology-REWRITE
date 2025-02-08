package asia.virtualmc.vArchaeology.storage;

import asia.virtualmc.vArchaeology.Main;

import asia.virtualmc.vLibrary.configs.TalentTreeConfig;
import asia.virtualmc.vLibrary.interfaces.OtherDataHandlingLib;
import asia.virtualmc.vLibrary.storage.OtherDataLib;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TalentTree implements OtherDataHandlingLib {
    private final Plugin plugin;
    private final OtherDataLib otherDataLib;
    private final String tableName = "varch_talents";
    private final ConcurrentHashMap<UUID, ConcurrentHashMap<Integer, Integer>> talentsMap;

    public TalentTree(@NotNull StorageManager storageManager) {
        this.plugin = storageManager.getMain();
        this.otherDataLib = storageManager.getOtherDataLib();
        this.talentsMap = new ConcurrentHashMap<>();
        createTable();
    }

    private void createTable() {
        List<String> statList = TalentTreeConfig.loadTalentNames(plugin);
        otherDataLib.createTable(statList, tableName, Main.prefix);
    }

    @Override
    public void loadPlayerData(@NotNull UUID uuid) {
        String name = Bukkit.getPlayer(uuid).getName();
        try {
            talentsMap.put(uuid, otherDataLib.loadPlayerData(uuid, tableName, Main.prefix));
        } catch (Exception e) {
            plugin.getLogger().severe(Main.prefix + "Failed to load data to hashmap (" + tableName + ") for " + name +  " : " + e.getMessage());
        }
    }

    @Override
    public void updatePlayerData(@NotNull UUID uuid) {
        Map<Integer, Integer> stats = talentsMap.get(uuid);
        if (stats == null) {
            return;
        }
        try {
            otherDataLib.savePlayerData(uuid, tableName, stats, Main.prefix);
        } catch (Exception e) {
            plugin.getLogger().severe(
                    Main.prefix + "Failed to save data for player " + uuid + "on " + tableName + " : " + e.getMessage());
        }
    }

    @Override
    public void updateAllData() {
        try {
            otherDataLib.saveAllData(tableName, talentsMap, Main.prefix);
        } catch (Exception e) {
            plugin.getLogger().severe(Main.prefix + "Failed to save all data to database: " + e.getMessage());
        }
    }

    @Override
    public void unloadData(@NotNull UUID uuid) {
        try {
            updatePlayerData(uuid);
            talentsMap.remove(uuid);
        } catch (Exception e) {
            plugin.getLogger().severe(
                    Main.prefix + "Failed to save data for player " + uuid + "on " + tableName + " : " + e.getMessage());
        }
    }

    public int getDataFromMap(@NotNull UUID uuid, int dataID) {
        return talentsMap.getOrDefault(uuid, new ConcurrentHashMap<>())
                .getOrDefault(dataID, 0);
    }

    public Map<UUID, Map<Integer, Integer>> getAllDataFromMap() {
        return new ConcurrentHashMap<>(talentsMap);
    }

    public void incrementData(@NotNull UUID uuid, int dataID) {
        talentsMap.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>())
                .merge(dataID, 1, Integer::sum);
    }

    public void addCustomValueData(@NotNull UUID uuid, int dataID, int value) {
        talentsMap.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>())
                .merge(dataID, value, Integer::sum);
    }
}