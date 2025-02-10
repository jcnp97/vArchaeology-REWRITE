package asia.virtualmc.vArchaeology.storage;

import asia.virtualmc.vArchaeology.Main;

import asia.virtualmc.vArchaeology.global.GlobalManager;
import asia.virtualmc.vLibrary.interfaces.OtherDataHandlingLib;
import asia.virtualmc.vLibrary.storage.OtherDataLib;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Statistics implements OtherDataHandlingLib {
    private final Main plugin;
    private final OtherDataLib otherDataLib;
    private final String tableName = "varch_statistics";
    private final ConcurrentHashMap<UUID, ConcurrentHashMap<Integer, Integer>> statisticsMap;

    public Statistics(@NotNull StorageManager storageManager) {
        this.plugin = storageManager.getMain();
        this.otherDataLib = storageManager.getOtherDataLib();
        this.statisticsMap = new ConcurrentHashMap<>();
        createTable();
    }

    private void createTable() {
        List<String> statList = Arrays.asList("numericalRank", "commonComponents", "uncommonComponents",
                "rareComponents", "uniqueComponents", "specialComponents", "mythicalComponents",
                "exoticComponents", "blocksMined", "artefactsFound", "artefactsRestored",
                "treasuresFound", "moneyEarned", "taxesPaid", "skillAptitude"
        );
        otherDataLib.createTable(statList, tableName, GlobalManager.prefix);
    }

    @Override
    public void loadPlayerData(@NotNull UUID uuid) {
        String name = Bukkit.getPlayer(uuid).getName();
        try {
            statisticsMap.put(uuid, otherDataLib.loadPlayerData(uuid, tableName, GlobalManager.prefix));
        } catch (Exception e) {
            plugin.getLogger().severe(GlobalManager.prefix + "Failed to load data to hashmap (" + tableName + ") for " + name +  " : " + e.getMessage());
        }
    }

    @Override
    public void updatePlayerData(@NotNull UUID uuid) {
        Map<Integer, Integer> stats = statisticsMap.get(uuid);
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
            otherDataLib.saveAllData(tableName, statisticsMap, GlobalManager.prefix);
        } catch (Exception e) {
            plugin.getLogger().severe(GlobalManager.prefix + "Failed to save all data to database: " + e.getMessage());
        }
    }

    @Override
    public void unloadData(@NotNull UUID uuid) {
        try {
            updatePlayerData(uuid);
            statisticsMap.remove(uuid);
        } catch (Exception e) {
            plugin.getLogger().severe(
                    GlobalManager.prefix + "Failed to save data for player " + uuid + "on " + tableName + " : " + e.getMessage());
        }
    }

    public int getDataFromMap(@NotNull UUID uuid, int dataID) {
        return statisticsMap.getOrDefault(uuid, new ConcurrentHashMap<>())
                .getOrDefault(dataID, 0);
    }

    public Map<UUID, Map<Integer, Integer>> getAllDataFromMap() {
        return new ConcurrentHashMap<>(statisticsMap);
    }

    public void incrementData(@NotNull UUID uuid, int dataID) {
        statisticsMap.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>())
                .merge(dataID, 1, Integer::sum);
    }

    public void addCustomValueData(@NotNull UUID uuid, int dataID, int value) {
        statisticsMap.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>())
                .merge(dataID, value, Integer::sum);
    }

    // Exclusive Methods (on class)
    public int[] getComponents(@NotNull UUID uuid) {
        int[] componentsOwned = new int[7];

        for (int i = 2; i < 9; i++) {
            componentsOwned[i - 2] = statisticsMap
                    .getOrDefault(uuid, new ConcurrentHashMap<>())
                    .getOrDefault(i, 0);
        }
        return componentsOwned;
    }

    public void addAllComponents(@NotNull UUID uuid, int amount) {
        ConcurrentHashMap<Integer, Integer> statsMap = statisticsMap.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>());
        statsMap.merge(2, amount, Integer::sum);
        statsMap.merge(3, amount, Integer::sum);
        statsMap.merge(4, amount, Integer::sum);
        statsMap.merge(5, amount, Integer::sum);
        statsMap.merge(6, amount, Integer::sum);
        statsMap.merge(7, amount, Integer::sum);
        statsMap.merge(8, amount, Integer::sum);
        updatePlayerData(uuid);
    }

    public void addAllCustomComponents(@NotNull UUID uuid, int[] componentsNumber) {
        Player player = Bukkit.getPlayer(uuid);
        if (componentsNumber.length != 7) {
            throw new IllegalArgumentException("componentsRequired must have exactly 7 elements.");
        }
        ConcurrentHashMap<Integer, Integer> statsMap = statisticsMap.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>());
        statsMap.merge(2, componentsNumber[0], Integer::sum);
        statsMap.merge(3, componentsNumber[1], Integer::sum);
        statsMap.merge(4, componentsNumber[2], Integer::sum);
        statsMap.merge(5, componentsNumber[3], Integer::sum);
        statsMap.merge(6, componentsNumber[4], Integer::sum);
        statsMap.merge(7, componentsNumber[5], Integer::sum);
        statsMap.merge(8, componentsNumber[6], Integer::sum);
        updatePlayerData(uuid);
//        for (int i = 0; i < componentsNumber.length; i++) {
//            if (componentsNumber[i] > 0) {
//                salvageLog.logTransactionTaken(player.getName(), configManager.dropNames[i], componentsRequired[i]);
//            }
//        }
    }

    public void subtractAllCustomComponents(@NotNull UUID uuid, int[] componentsNumber) {
        Player player = Bukkit.getPlayer(uuid);
        if (componentsNumber.length != 7) {
            throw new IllegalArgumentException("componentsRequired must have exactly 7 elements.");
        }
        ConcurrentHashMap<Integer, Integer> statsMap = statisticsMap.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>());
        statsMap.merge(2, -componentsNumber[0], Integer::sum);
        statsMap.merge(3, -componentsNumber[1], Integer::sum);
        statsMap.merge(4, -componentsNumber[2], Integer::sum);
        statsMap.merge(5, -componentsNumber[3], Integer::sum);
        statsMap.merge(6, -componentsNumber[4], Integer::sum);
        statsMap.merge(7, -componentsNumber[5], Integer::sum);
        statsMap.merge(8, -componentsNumber[6], Integer::sum);
        updatePlayerData(uuid);
//        for (int i = 0; i < componentsNumber.length; i++) {
//            if (componentsNumber[i] > 0) {
//                salvageLog.logTransactionTaken(player.getName(), configManager.dropNames[i], componentsRequired[i]);
//            }
//        }
    }
}