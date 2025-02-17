package asia.virtualmc.vArchaeology.exp;

import asia.virtualmc.vArchaeology.global.MaterialDrop;
import asia.virtualmc.vArchaeology.global.TraitValues;
import asia.virtualmc.vArchaeology.storage.PlayerData;
import asia.virtualmc.vArchaeology.storage.Statistics;
import asia.virtualmc.vArchaeology.storage.StorageManager;
import asia.virtualmc.vArchaeology.storage.TalentTree;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MaterialGetEXP {
    private final PlayerData playerData;
    private final TalentTree talentTree;
    private final Statistics statistics;
    private final Random random;
    private final ConcurrentMap<UUID, MaterialGetData> materialGetDataMap = new ConcurrentHashMap<>();

    public MaterialGetEXP(@NotNull StorageManager storageManager) {
        this.playerData = storageManager.getPlayerData();
        this.talentTree = storageManager.getTalentTree();
        this.statistics = storageManager.getStatistics();
        this.random = new Random();
    }

    private record MaterialGetData(double baseMultiplier, boolean hasTalentID14) { }

    public void loadEXPData(@NotNull UUID uuid) {
        // Wisdom Trait: Block-break - 2% XP/level / Material-get - 1% XP/level / Artefact-restore: 0.5% XP/level
        int traitBonus = playerData.getWisdomTrait(uuid);
        // Rank Bonuses: 1% XP/level for both block-break and material-get, 0.25% XP/level for artefact-restore
        int rankBonus = Math.min(statistics.getDataFromMap(uuid, 1), 50);
        // XP Multiplier - global multiplier
        double archXPMul = playerData.getCurrentXPM(uuid);

        /* ------------------ Material-Get Data ----------------- */
        // Insightful Judgement
        int talentBonus2 = talentTree.getDataFromMap(uuid, 2);
        // Good Fortune
        boolean hasTalentID14 = (talentTree.getDataFromMap(uuid, 14) == 1);

        // Precompute the base multiplier for material-get EXP
        double materialGetBaseMultiplier =
                (traitBonus * TraitValues.wisdomEffects[1] + (talentBonus2 * 2) + (rankBonus * 0.5)) / 100.0
                        + archXPMul;

        MaterialGetData materialGetData = new MaterialGetData(
                materialGetBaseMultiplier,
                hasTalentID14
        );
        materialGetDataMap.put(uuid, materialGetData);
    }

    public void unloadEXPData(@NotNull UUID uuid) {
        materialGetDataMap.remove(uuid);
    }

    public boolean hasEXPData(@NotNull UUID uuid) {
        return materialGetDataMap.containsKey(uuid);
    }

    public double getTotalMaterialGetEXP(@NotNull UUID uuid, int dropTable) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return 0.0;

        MaterialGetData data = materialGetDataMap.get(uuid);
        if (data == null) {
            loadEXPData(uuid);
            data = materialGetDataMap.get(uuid);
            if (data == null) {
                return 0.0;
            }
        }

        // Material Base EXP from ID 1 to ID 7
        int matEXP = MaterialDrop.dropEXP[dropTable - 1];

        // Check for Talent ID 14
        if (data.hasTalentID14() && random.nextInt(100) < 5) {
            matEXP *= 2;
        }

        return data.baseMultiplier() * matEXP;
//        double currentBonus = playerData.getCurrentBXP(uuid);
//
//        if (currentBonus <= 0) {
//            EXPDisplayUtils.showEXPActionBar(player, playerData.getCurrentEXP(uuid),
//                    totalXP, 0, PlayerData.expTable.get(playerData.getCurrentLevel(uuid))
//            );
//            return totalXP;
//        }
//
//        double bonusXP = Math.min(currentBonus, totalXP);
//        if (bonusXP >= totalXP) {
//            playerData.updateBXP(player, EnumsLib.UpdateType.SUBTRACT, totalXP);
//        } else {
//            playerData.updateBXP(player, EnumsLib.UpdateType.SET, 0);
//        }
//
//        EXPDisplayUtils.showEXPActionBar(player, playerData.getCurrentEXP(uuid),
//                totalXP, bonusXP, PlayerData.expTable.get(playerData.getCurrentLevel(uuid))
//        );
//        return Math.ceil(totalXP + bonusXP);
    }
}
