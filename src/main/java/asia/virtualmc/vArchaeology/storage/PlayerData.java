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

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class PlayerData implements DataHandlingLib {
    private final Plugin plugin;
    private final PlayerDataLib playerDataLib;
    private final BossBarUpdater bossBarUpdater;
    private final Map<UUID, PlayerStats> playerStatsMap;
    private final String tableName = "varch_playerData";

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
                        EXPTableConfig.ARCH_EXP_TABLE.get(stats.level + 1));
                bossBarUpdater.updateEXPMetrics(uuid, stats.exp, value,
                        EXPTableConfig.ARCH_EXP_TABLE.get(stats.level + 1), stats.level);
            }
        }
    }
}
