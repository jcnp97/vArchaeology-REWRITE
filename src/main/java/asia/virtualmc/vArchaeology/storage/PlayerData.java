package asia.virtualmc.vArchaeology.storage;

import asia.virtualmc.vArchaeology.Main;

import asia.virtualmc.vArchaeology.global.GlobalManager;
import asia.virtualmc.vArchaeology.tasks.BossBarUpdater;
import asia.virtualmc.vLibrary.configs.EXPTableConfig;
import asia.virtualmc.vLibrary.enums.EnumsLib;
import asia.virtualmc.vLibrary.interfaces.DataHandlingLib;
import asia.virtualmc.vLibrary.storage.PlayerDataLib;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class PlayerData implements DataHandlingLib {
    private final Main plugin;
    private final PlayerDataLib playerDataLib;
    private BossBarUpdater bossBarUpdater;
    private final ConcurrentHashMap<UUID, PlayerDataLib.PlayerStats> playerDataMap;
    private final String tableName = "varch_playerData";
    private final int MAX_LEVEL;
    private final int MIN_LEVEL;
    public static List<Integer> expTable;

    public PlayerData(@NotNull StorageManager storageManager) {
        this.plugin = storageManager.getMain();
        this.playerDataLib = storageManager.getPlayerDataLib();
        this.playerDataMap = new ConcurrentHashMap<>();
        this.MAX_LEVEL = 120;
        this.MIN_LEVEL = 1;
        expTable = EXPTableConfig.loadEXPTable(plugin, GlobalManager.prefix);

        playerDataLib.createTable(tableName, GlobalManager.prefix);
    }

    @Override
    public void loadPlayerData(@NotNull UUID uuid) {
        String name = getPlayerName(uuid);
        try {
            PlayerDataLib.PlayerStats stats = playerDataLib.loadPlayerData(uuid, tableName, GlobalManager.prefix);
            playerDataMap.put(uuid, stats);
        } catch (Exception e) {
            plugin.getLogger().severe(GlobalManager.prefix + "Failed to load data to hashmap (" + tableName + ") for " + name +  " : " + e.getMessage());
        }
    }

    @Override
    public void updatePlayerData(@NotNull UUID uuid) {
        PlayerDataLib.PlayerStats stats = playerDataMap.get(uuid);
        if (stats == null) {
            return;
        }
        try {
            playerDataLib.savePlayerData(
                    uuid,
                    stats.name,
                    stats.exp,
                    stats.bxp,
                    stats.xpm,
                    stats.level,
                    stats.luck,
                    stats.traitPoints,
                    stats.talentPoints,
                    stats.wisdomTrait,
                    stats.charismaTrait,
                    stats.karmaTrait,
                    stats.dexterityTrait,
                    stats.data1,
                    stats.data2,
                    stats.data3,
                    tableName,
                    GlobalManager.prefix
            );
        } catch (Exception e) {
            plugin.getLogger().severe(
                    GlobalManager.prefix + "Failed to send map data to database for player " + stats.name + ": " + e.getMessage()
            );
        }
    }

    @Override
    public void updateAllData() {
        try {
            playerDataLib.saveAllData(playerDataMap, tableName, GlobalManager.prefix);
        } catch (Exception e) {
            plugin.getLogger().severe(GlobalManager.prefix + "Failed to send all data from hashmap to database: " + e.getMessage());
        }
    }

    @Override
    public void unloadData(@NotNull UUID uuid) {
        String name = getPlayerName(uuid);
        try {
            updatePlayerData(uuid);
            playerDataMap.remove(uuid);
        } catch (Exception e) {
            plugin.getLogger().severe(GlobalManager.prefix + "Failed to unload data for player " + name + ": " + e.getMessage());
        }
    }

    @Override
    public void updateEXP(@NotNull Player player,
                          @NotNull EnumsLib.UpdateType type,
                          double value) {

        if (bossBarUpdater == null) {
            bossBarUpdater = plugin.getTaskManager().getBossBarUpdater();
        }

        UUID uuid = player.getUniqueId();
        PlayerDataLib.PlayerStats stats = playerDataMap.get(uuid);

        if (stats != null) {
            stats.exp = playerDataLib.getNewEXP(type, stats.exp, value);

            if (type == EnumsLib.UpdateType.ADD) {
//                EXPDisplayUtils.showEXPActionBar(player, stats.exp, value,
//                        expTable.get(stats.level));
                bossBarUpdater.updateEXPMetrics(uuid, stats.exp, value,
                        expTable.get(stats.level), stats.level);
                checkAndApplyLevelUp(player);
            }
        }
    }

    private void checkAndApplyLevelUp(@NotNull Player player) {
        PlayerDataLib.PlayerStats stats = playerDataMap.get(player.getUniqueId());
        boolean levelUp = false;
        if (stats == null) return;
        int previousLevel = stats.level;

        while (stats.exp >= expTable.get(stats.level) && stats.level < MAX_LEVEL) {
            stats.level++;
            stats.traitPoints++;
            if (stats.level > 100) stats.traitPoints += 2;
            levelUp = true;
        }

        if (levelUp) playerDataLib.levelingEffects(player, stats.level, previousLevel, stats.traitPoints);
    }

    @Override
    public void updateLevel(@NotNull Player player,
                          @NotNull EnumsLib.UpdateType type,
                          int value) {
        PlayerDataLib.PlayerStats stats = playerDataMap.get(player.getUniqueId());

        if (stats != null) {
            stats.level = playerDataLib.getNewLevel(type, stats.level, value);
        }
    }

    @Override
    public void updateXPM(@NotNull Player player,
                            @NotNull EnumsLib.UpdateType type,
                            double value) {
        PlayerDataLib.PlayerStats stats = playerDataMap.get(player.getUniqueId());

        if (stats != null) {
            stats.xpm = playerDataLib.getNewXPM(type, stats.xpm, value);
        }
    }

    @Override
    public void updateBXP(@NotNull Player player,
                          @NotNull EnumsLib.UpdateType type,
                          double value) {
        PlayerDataLib.PlayerStats stats = playerDataMap.get(player.getUniqueId());

        if (stats != null) {
            stats.bxp = playerDataLib.getNewBXP(type, stats.bxp, value);
        }
    }

    @Override
    public void updateTraitPoints(@NotNull Player player,
                            @NotNull EnumsLib.UpdateType type,
                            int value) {
        PlayerDataLib.PlayerStats stats = playerDataMap.get(player.getUniqueId());

        if (stats != null) {
            stats.traitPoints = playerDataLib.getNewTraitPoints(type, stats.traitPoints, value);
        }
    }

    @Override
    public void updateTalentPoints(@NotNull Player player,
                                  @NotNull EnumsLib.UpdateType type,
                                  int value) {
        PlayerDataLib.PlayerStats stats = playerDataMap.get(player.getUniqueId());

        if (stats != null) {
            stats.talentPoints = playerDataLib.getNewTalentPoints(type, stats.talentPoints, value);
        }
    }

    public void updateADP(@NotNull Player player,
                          @NotNull EnumsLib.UpdateType type,
                          double value) {
        PlayerDataLib.PlayerStats stats = playerDataMap.get(player.getUniqueId());

        if (stats != null) {
            double newADP = playerDataLib.getNewData1(type, stats.data1, value);
            if (newADP >= 100.0) {
                stats.data1 = 0;
            } else {
                stats.data1 = newADP;
            }
        }
    }

    public void updateLuck(@NotNull Player player,
                                   @NotNull EnumsLib.UpdateType type,
                                   int value) {
        PlayerDataLib.PlayerStats stats = playerDataMap.get(player.getUniqueId());

        if (stats != null) {
            stats.luck = playerDataLib.getNewLuck(type, stats.luck, value);
        }
    }

    public void addWisdomTrait(@NotNull Player player, int value) {
        PlayerDataLib.PlayerStats stats = playerDataMap.get(player.getUniqueId());
        if (stats != null) {
            stats.wisdomTrait += value;
        }
    }

    public void addKarmaTrait(@NotNull Player player, int value) {
        PlayerDataLib.PlayerStats stats = playerDataMap.get(player.getUniqueId());
        if (stats != null) {
            stats.karmaTrait += value;
        }
    }

    public void addCharismaTrait(@NotNull Player player, int value) {
        PlayerDataLib.PlayerStats stats = playerDataMap.get(player.getUniqueId());
        if (stats != null) {
            stats.charismaTrait += value;
        }
    }

    public void addDexterityTrait(@NotNull Player player, int value) {
        PlayerDataLib.PlayerStats stats = playerDataMap.get(player.getUniqueId());
        if (stats != null) {
            stats.dexterityTrait += value;
        }
    }

    // Getter Methods
    public String getPlayerName(@NotNull UUID uuid) {
        PlayerDataLib.PlayerStats stats = playerDataMap.get(uuid);
        return stats != null ? stats.name : null;
    }

    public double getCurrentEXP(@NotNull UUID uuid) {
        PlayerDataLib.PlayerStats stats = playerDataMap.get(uuid);
        return stats != null ? stats.exp : 0.0;
    }

    public int getCurrentLevel(@NotNull UUID uuid) {
        PlayerDataLib.PlayerStats stats = playerDataMap.get(uuid);
        return stats != null ? stats.level : MIN_LEVEL;
    }

    public double getCurrentBXP(@NotNull UUID uuid) {
        PlayerDataLib.PlayerStats stats = playerDataMap.get(uuid);
        return stats != null ? stats.bxp : 0.0;
    }

    public int getCurrentLuck(@NotNull UUID uuid) {
        PlayerDataLib.PlayerStats stats = playerDataMap.get(uuid);
        return stats != null ? stats.luck : 0;
    }

    public double getCurrentXPM(@NotNull UUID uuid) {
        PlayerDataLib.PlayerStats stats = playerDataMap.get(uuid);
        return stats != null ? stats.xpm : 1.0;
    }

    public int getTraitPoints(@NotNull UUID uuid) {
        PlayerDataLib.PlayerStats stats = playerDataMap.get(uuid);
        return stats != null ? stats.traitPoints : 1;
    }

    public int getTalentPoints(@NotNull UUID uuid) {
        PlayerDataLib.PlayerStats stats = playerDataMap.get(uuid);
        return stats != null ? stats.talentPoints : 0;
    }

    public int getWisdomTrait(@NotNull UUID uuid) {
        PlayerDataLib.PlayerStats stats = playerDataMap.get(uuid);
        return stats != null ? stats.wisdomTrait : 0;
    }

    public int getKarmaTrait(@NotNull UUID uuid) {
        PlayerDataLib.PlayerStats stats = playerDataMap.get(uuid);
        return stats != null ? stats.karmaTrait : 0;
    }

    public int getCharismaTrait(@NotNull UUID uuid) {
        PlayerDataLib.PlayerStats stats = playerDataMap.get(uuid);
        return stats != null ? stats.charismaTrait : 0;
    }

    public int getDexterityTrait(@NotNull UUID uuid) {
        PlayerDataLib.PlayerStats stats = playerDataMap.get(uuid);
        return stats != null ? stats.dexterityTrait : 0;
    }

    public double getADP(@NotNull UUID uuid) {
        PlayerDataLib.PlayerStats stats = playerDataMap.get(uuid);
        return stats != null ? stats.data1 : 0;
    }
}
