package asia.virtualmc.vArchaeology.storage;

import asia.virtualmc.vArchaeology.Main;

import asia.virtualmc.vArchaeology.tasks.BossBarUpdater;
import asia.virtualmc.vLibrary.configs.EXPTableConfig;
import asia.virtualmc.vLibrary.enums.EnumsLib;
import asia.virtualmc.vLibrary.interfaces.DataHandlingLib;
import asia.virtualmc.vLibrary.storage.PlayerDataLib;
import asia.virtualmc.vLibrary.utils.EXPDisplayUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class PlayerData implements DataHandlingLib {
    private final Plugin plugin;
    private final PlayerDataLib playerDataLib;
    private final BossBarUpdater bossBarUpdater;
    private final Map<UUID, PlayerStats> playerStatsMap;
    private final String tableName = "varch_playerData";
    private final int MAX_LEVEL = 120;
    private final int MIN_LEVEL = 1;
    public static final List<Integer> expTable = EXPTableConfig.ARCH_EXP_TABLE;

    public PlayerData(@NotNull StorageManager storageManager) {
        this.plugin = storageManager.getMain();
        this.playerDataLib = storageManager.getPlayerDataLib();
        this.bossBarUpdater = storageManager.getTaskManager().getBossBarUpdater();
        this.playerStatsMap = new ConcurrentHashMap<>();

        playerDataLib.createTable(tableName, Main.prefix);
    }

    private static class PlayerStats {
        String name;
        double exp;
        double bxp;
        double xpm;
        int level;
        int luck;
        int traitPoints;
        int talentPoints;
        int wisdomTrait;
        int charismaTrait;
        int karmaTrait;
        int dexterityTrait;
        int rank;

        PlayerStats(String name, double exp, double bxp, double xpm, int level, int luck,
                    int traitPoints, int talentPoints, int wisdomTrait, int charismaTrait,
                    int karmaTrait, int dexterityTrait, int rank
        ) {
            this.name = name;
            this.exp = exp;
            this.bxp = bxp;
            this.xpm = xpm;
            this.level = level;
            this.luck = luck;
            this.traitPoints = traitPoints;
            this.talentPoints = talentPoints;
            this.wisdomTrait = wisdomTrait;
            this.charismaTrait = charismaTrait;
            this.karmaTrait = karmaTrait;
            this.dexterityTrait = dexterityTrait;
            this.rank = rank;
        }
    }

    @Override
    public void updatePlayerData(UUID uuid) {
        PlayerStats stats = playerStatsMap.get(uuid);
        if (stats == null) {
            return;
        }
        try {
            playerDataLib.storePlayerData(
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
                    stats.rank,
                    tableName,
                    Main.prefix
            );
        } catch (Exception e) {
            plugin.getLogger().severe(
                    Main.prefix + "Failed to save data for player " + stats.name + ": " + e.getMessage()
            );
        }
    }

    @Override
    public void updateAllData() {
        playerStatsMap.forEach((uuid, stats) -> {
            try {
                playerDataLib.storePlayerData(
                        uuid, stats.name, stats.exp, stats.bxp,
                        stats.xpm, stats.level, stats.luck,
                        stats.traitPoints, stats.talentPoints,
                        stats.wisdomTrait, stats.charismaTrait,
                        stats.karmaTrait, stats.dexterityTrait,
                        stats.rank, tableName, Main.prefix
                );
            } catch (Exception e) {
                plugin.getLogger().severe(Main.prefix + "Failed to save data for player " + stats.name + ": " + e.getMessage());
            }
        });
    }

    @Override
    public void unloadData(@NotNull UUID uuid) {
        try {
            updatePlayerData(uuid);
            playerStatsMap.remove(uuid);
        } catch (Exception e) {
            plugin.getLogger().severe(Main.prefix + "Failed to save data for player " + uuid + ": " + e.getMessage());
        }
    }

    @Override
    public void updateEXP(@NotNull Player player,
                          @NotNull EnumsLib.UpdateType type,
                          double value) {
        UUID uuid = player.getUniqueId();
        PlayerStats stats = playerStatsMap.get(uuid);

        if (stats != null) {
            stats.exp = playerDataLib.getNewEXP(type, stats.exp, value);

            if (type == EnumsLib.UpdateType.ADD) {
                EXPDisplayUtils.showEXPActionBar(player, stats.exp, value,
                        expTable.get(stats.level));
                bossBarUpdater.updateEXPMetrics(uuid, stats.exp, value,
                        expTable.get(stats.level), stats.level);
                checkAndApplyLevelUp(player);
            }
        }
    }

    private void checkAndApplyLevelUp(@NotNull Player player) {
        PlayerStats stats = playerStatsMap.get(player.getUniqueId());
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
        PlayerStats stats = playerStatsMap.get(player.getUniqueId());

        if (stats != null) {
            stats.level = playerDataLib.getNewLevel(type, stats.level, value);
        }
    }

    @Override
    public void updateXPM(@NotNull Player player,
                            @NotNull EnumsLib.UpdateType type,
                            double value) {
        PlayerStats stats = playerStatsMap.get(player.getUniqueId());

        if (stats != null) {
            stats.xpm = playerDataLib.getNewXPM(type, stats.xpm, value);
        }
    }

    @Override
    public void updateBXP(@NotNull Player player,
                          @NotNull EnumsLib.UpdateType type,
                          double value) {
        PlayerStats stats = playerStatsMap.get(player.getUniqueId());

        if (stats != null) {
            stats.bxp = playerDataLib.getNewBXP(type, stats.bxp, value);
        }
    }

    @Override
    public void updateTraitPoints(@NotNull Player player,
                            @NotNull EnumsLib.UpdateType type,
                            int value) {
        PlayerStats stats = playerStatsMap.get(player.getUniqueId());

        if (stats != null) {
            stats.traitPoints = playerDataLib.getNewTraitPoints(type, stats.traitPoints, value);
        }
    }

    @Override
    public void updateTalentPoints(@NotNull Player player,
                                  @NotNull EnumsLib.UpdateType type,
                                  int value) {
        PlayerStats stats = playerStatsMap.get(player.getUniqueId());

        if (stats != null) {
            stats.talentPoints = playerDataLib.getNewTalentPoints(type, stats.talentPoints, value);
        }
    }

    @Override
    public void updateNumericalRank(@NotNull Player player,
                                   @NotNull EnumsLib.UpdateType type,
                                   int value) {
        PlayerStats stats = playerStatsMap.get(player.getUniqueId());

        if (stats != null) {
            stats.rank = playerDataLib.getNewNumericalRank(type, stats.rank, value);
        }
    }

    @Override
    public void updateLuck(@NotNull Player player,
                                   @NotNull EnumsLib.UpdateType type,
                                   int value) {
        PlayerStats stats = playerStatsMap.get(player.getUniqueId());

        if (stats != null) {
            stats.luck = playerDataLib.getNewLuck(type, stats.luck, value);
        }
    }

    @Override
    public void addWisdomTrait(@NotNull Player player, int value) {
        PlayerStats stats = playerStatsMap.get(player.getUniqueId());
        if (stats != null) {
            stats.wisdomTrait += value;
        }
    }

    @Override
    public void addKarmaTrait(@NotNull Player player, int value) {
        PlayerStats stats = playerStatsMap.get(player.getUniqueId());
        if (stats != null) {
            stats.karmaTrait += value;
        }
    }

    @Override
    public void addCharismaTrait(@NotNull Player player, int value) {
        PlayerStats stats = playerStatsMap.get(player.getUniqueId());
        if (stats != null) {
            stats.charismaTrait += value;
        }
    }

    @Override
    public void addDexterityTrait(@NotNull Player player, int value) {
        PlayerStats stats = playerStatsMap.get(player.getUniqueId());
        if (stats != null) {
            stats.dexterityTrait += value;
        }
    }

    // Getter Methods
    public String getPlayerName(@NotNull UUID uuid) {
        PlayerStats stats = playerStatsMap.get(uuid);
        return stats != null ? stats.name : null;
    }

    public double getCurrentEXP(@NotNull UUID uuid) {
        PlayerStats stats = playerStatsMap.get(uuid);
        return stats != null ? stats.exp : 0.0;
    }

    public int getCurrentLevel(@NotNull UUID uuid) {
        PlayerStats stats = playerStatsMap.get(uuid);
        return stats != null ? stats.level : MIN_LEVEL;
    }

    public double getCurrentBXP(@NotNull UUID uuid) {
        PlayerStats stats = playerStatsMap.get(uuid);
        return stats != null ? stats.bxp : 0.0;
    }

    public int getCurrentLuck(@NotNull UUID uuid) {
        PlayerStats stats = playerStatsMap.get(uuid);
        return stats != null ? stats.luck : 0;
    }

    public double getCurrentXPM(@NotNull UUID uuid) {
        PlayerStats stats = playerStatsMap.get(uuid);
        return stats != null ? stats.xpm : 1.0;
    }

    public int getTraitPoints(@NotNull UUID uuid) {
        PlayerStats stats = playerStatsMap.get(uuid);
        return stats != null ? stats.traitPoints : 1;
    }

    public int getTalentPoints(@NotNull UUID uuid) {
        PlayerStats stats = playerStatsMap.get(uuid);
        return stats != null ? stats.talentPoints : 0;
    }

    public int getWisdomTrait(@NotNull UUID uuid) {
        PlayerStats stats = playerStatsMap.get(uuid);
        return stats != null ? stats.wisdomTrait : 0;
    }

    public int getKarmaTrait(@NotNull UUID uuid) {
        PlayerStats stats = playerStatsMap.get(uuid);
        return stats != null ? stats.karmaTrait : 0;
    }

    public int getCharismaTrait(@NotNull UUID uuid) {
        PlayerStats stats = playerStatsMap.get(uuid);
        return stats != null ? stats.charismaTrait : 0;
    }

    public int getDexterityTrait(@NotNull UUID uuid) {
        PlayerStats stats = playerStatsMap.get(uuid);
        return stats != null ? stats.dexterityTrait : 0;
    }

    public int getNumericalRank(@NotNull UUID uuid) {
        PlayerStats stats = playerStatsMap.get(uuid);
        return stats != null ? stats.rank : 0;
    }
}
