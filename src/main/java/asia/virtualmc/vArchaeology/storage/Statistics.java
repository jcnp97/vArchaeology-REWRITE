package asia.virtualmc.vArchaeology.storage;

import asia.virtualmc.vArchaeology.Main;

import asia.virtualmc.vLibrary.interfaces.DataHandlingLib;
import asia.virtualmc.vLibrary.interfaces.OtherDataHandlingLib;
import asia.virtualmc.vLibrary.storage.OtherDataLib;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Statistics implements OtherDataHandlingLib {
    private final Plugin plugin;
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
        List<String> statList = Arrays.asList("rankAchieved", "commonComponents", "uncommonComponents",
                "rareComponents", "uniqueComponents", "specialComponents", "mythicalComponents",
                "exoticComponents", "blocksMined", "artefactsFound", "artefactsRestored",
                "treasuresFound", "moneyEarned", "taxesPaid"
        );
        otherDataLib.createTable(statList, tableName, Main.prefix);
    }

    @Override
    public void createNewPlayerData(@NotNull UUID uuid) {

    }

    @Override
    public void loadPlayerData(@NotNull UUID uuid) {

    }

    @Override
    public void updatePlayerData(@NotNull UUID uuid) {
        Map<Integer, Integer> stats = statisticsMap.get(uuid);
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
            otherDataLib.saveAllData(tableName, statisticsMap, Main.prefix);
        } catch (Exception e) {
            plugin.getLogger().severe(Main.prefix + "Failed to save all data to database: " + e.getMessage());
        }
    }

    @Override
    public void unloadData(@NotNull UUID uuid) {
        try {
            updatePlayerData(uuid);
            statisticsMap.remove(uuid);
        } catch (Exception e) {
            plugin.getLogger().severe(
                    Main.prefix + "Failed to save data for player " + uuid + "on " + tableName + " : " + e.getMessage());
        }
    }

    public int getStatistics(UUID playerUUID, int statsID) {
        return playerStatistics.getOrDefault(playerUUID, new ConcurrentHashMap<>())
                .getOrDefault(statsID, 0);
    }

    public Map<UUID, Map<Integer, Integer>> getPlayerStatistics() {
        return new ConcurrentHashMap<>(playerStatistics);
    }

    public void incrementStatistics(UUID playerUUID, int statsID) {
        playerStatistics.computeIfAbsent(playerUUID, k -> new ConcurrentHashMap<>())
                .merge(statsID, 1, Integer::sum);
        //updatePlayerData(playerUUID);
    }

    public void addStatistics(UUID playerUUID, int statsID, int value) {
        playerStatistics.computeIfAbsent(playerUUID, k -> new ConcurrentHashMap<>())
                .merge(statsID, value, Integer::sum);
        //updatePlayerData(playerUUID);
    }

    public int[] getComponents(UUID playerUUID) {
        int[] componentsOwned = new int[7];

        for (int i = 2; i < 9; i++) {
            componentsOwned[i - 2] = playerStatistics
                    .getOrDefault(playerUUID, new ConcurrentHashMap<>())
                    .getOrDefault(i, 0);
        }
        return componentsOwned;
    }

    public void addComponents(UUID uuid, int amount) {
        ConcurrentHashMap<Integer, Integer> statsMap = playerStatistics.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>());
        statsMap.merge(2, amount, Integer::sum);
        statsMap.merge(3, amount, Integer::sum);
        statsMap.merge(4, amount, Integer::sum);
        statsMap.merge(5, amount, Integer::sum);
        statsMap.merge(6, amount, Integer::sum);
        statsMap.merge(7, amount, Integer::sum);
        statsMap.merge(8, amount, Integer::sum);
        updatePlayerData(uuid);
    }

    public void subtractComponents(UUID uuid, int[] componentsRequired) {
        Player player = Bukkit.getPlayer(uuid);
        if (componentsRequired.length != 7) {
            throw new IllegalArgumentException("componentsRequired must have exactly 7 elements.");
        }
        ConcurrentHashMap<Integer, Integer> statsMap = playerStatistics.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>());
        statsMap.merge(2, -componentsRequired[0], Integer::sum);
        statsMap.merge(3, -componentsRequired[1], Integer::sum);
        statsMap.merge(4, -componentsRequired[2], Integer::sum);
        statsMap.merge(5, -componentsRequired[3], Integer::sum);
        statsMap.merge(6, -componentsRequired[4], Integer::sum);
        statsMap.merge(7, -componentsRequired[5], Integer::sum);
        statsMap.merge(8, -componentsRequired[6], Integer::sum);
        updatePlayerData(uuid);
        for (int i = 0; i < componentsRequired.length; i++) {
            if (componentsRequired[i] > 0) {
                salvageLog.logTransactionTaken(player.getName(), configManager.dropNames[i], componentsRequired[i]);
            }
        }
    }
}