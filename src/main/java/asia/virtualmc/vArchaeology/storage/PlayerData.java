package asia.virtualmc.vArchaeology.storage;

import asia.virtualmc.vArchaeology.Main;

import asia.virtualmc.vLibrary.storage.PlayerDataLib;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerData {
    private final Plugin plugin;
    private final PlayerDataLib playerDataLib;
    private final Map<UUID, PlayerStats> playerStatsMap;
    private final String tableName = "varch_playerData";
    private final int MAX_EXP = 1_000_000_000;
    private final int MIN_LEVEL = 1;
    private final int MAX_LEVEL = 120;

    public PlayerData(@NotNull StorageManager storageManager) {
        this.plugin = storageManager.getMain();
        this.playerDataLib = storageManager.getPlayerDataLib();
        this.playerStatsMap = new ConcurrentHashMap<>();

        playerDataLib.createTable(tableName, Main.prefix);
    }







//    public void updatePlayerData(UUID uuid) {
//
//
//        PrimaryStats stats = primaryMap.get(uuid);
//        if (stats == null) {
//            return;
//        }
//        try {
//            database.storePrimaryData(
//                    uuid,
//                    stats.name,
//                    stats.exp,
//                    stats.bxp,
//                    stats.xpm,
//                    stats.level,
//                    stats.luck,
//                    stats.traitPoints,
//                    stats.talentPoints,
//                    stats.wisdomTrait,
//                    stats.charismaTrait,
//                    stats.karmaTrait,
//                    stats.dexterityTrait,
//                    primaryName,
//                    Main.prefix
//            );
//        } catch (Exception e) {
//            plugin.getLogger().severe(
//                    Main.prefix + "Failed to save data for player " + stats.name + ": " + e.getMessage()
//            );
//        }
//    }

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
}
