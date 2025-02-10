package asia.virtualmc.vArchaeology.exp;

import asia.virtualmc.vArchaeology.global.GlobalManager;
import asia.virtualmc.vArchaeology.global.TalentTreeValues;
import asia.virtualmc.vArchaeology.storage.PlayerData;
import asia.virtualmc.vArchaeology.storage.Statistics;
import asia.virtualmc.vArchaeology.storage.StorageManager;
import asia.virtualmc.vArchaeology.storage.TalentTree;
import asia.virtualmc.vLibrary.enums.EnumsLib;
import asia.virtualmc.vLibrary.utils.EXPDisplayUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BlockBreakEXP {
    private final PlayerData playerData;
    private final TalentTree talentTree;
    private final Statistics statistics;
    private final Random random;
    private final String sPrefix = GlobalManager.severePrefix;
    private final String cPrefix = GlobalManager.coloredPrefix;
    public static final ConcurrentMap<UUID, BlockBreakData> blockBreakDataMap = new ConcurrentHashMap<>();

    public BlockBreakEXP(@NotNull StorageManager storageManager) {
        this.playerData = storageManager.getPlayerData();
        this.talentTree = storageManager.getTalentTree();
        this.statistics = storageManager.getStatistics();
        this.random = new Random();
    }

    public record BlockBreakData(double baseMultiplier, boolean hasTalentID12) { }

    public void calculateEXPMap(@NotNull UUID uuid) {
        // Wisdom Trait: Block-break - 2% XP/level / Material-get - 1% XP/level / Artefact-restore: 0.5% XP/level
        int traitBonus = playerData.getWisdomTrait(uuid);
        // Rank Bonuses: 1% XP/level for both block-break and material-get, 0.25% XP/level for artefact-restore
        int rankBonus = Math.min(statistics.getDataFromMap(uuid, 1), 50);
        // XP Multiplier - global multiplier
        double archXPMul = playerData.getCurrentXPM(uuid);

        /* ------------------ Block-Break Data ------------------ */
        // Sagacity Talent
        int talentBonus1 = talentTree.getDataFromMap(uuid, 1);
        // Lucky Break Talent
        boolean hasTalentID12 = (talentTree.getDataFromMap(uuid, 12) == 1);

        // Precompute the base multiplier for block-break EXP
        double blockBreakBaseMultiplier =
                ((traitBonus * TalentTreeValues.wisdomEffects[0]) + (talentBonus1 * 15) + (rankBonus)) / 100.0
                        + archXPMul;

        BlockBreakData blockBreakData = new BlockBreakData(
                blockBreakBaseMultiplier,
                hasTalentID12
        );
        blockBreakDataMap.put(uuid, blockBreakData);
    }

    public void unloadPlayerEXPMap(@NotNull UUID uuid) {
        blockBreakDataMap.remove(uuid);
    }

    public double getTotalBlockBreakEXP(@NotNull UUID uuid, float blockEXP) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return 0.0;

        BlockBreakData data = blockBreakDataMap.get(uuid);
        if (data == null) {
            calculateEXPMap(uuid);
            data = blockBreakDataMap.get(uuid);
            if (data == null) {
                return 0.0;
            }
        }

        // Check for Talent ID 12
        if (data.hasTalentID12() && random.nextInt(10) < 1) {
            blockEXP *= 2;
        }

        double totalXP = data.baseMultiplier() * blockEXP;
        double currentBonus = playerData.getCurrentBXP(uuid);

        if (currentBonus <= 0) {
            EXPDisplayUtils.showEXPActionBar(player, playerData.getCurrentEXP(uuid),
                    totalXP, 0, PlayerData.expTable.get(playerData.getCurrentLevel(uuid))
                    );
            return totalXP;
        }

        double bonusXP = Math.min(currentBonus, totalXP);
        if (bonusXP >= totalXP) {
            playerData.updateBXP(player, EnumsLib.UpdateType.SUBTRACT, totalXP);
        } else {
            playerData.updateBXP(player, EnumsLib.UpdateType.SET, 0);
        }

        EXPDisplayUtils.showEXPActionBar(player, playerData.getCurrentEXP(uuid),
                totalXP, bonusXP, PlayerData.expTable.get(playerData.getCurrentLevel(uuid))
        );
        return Math.ceil(totalXP + bonusXP);
    }
}
