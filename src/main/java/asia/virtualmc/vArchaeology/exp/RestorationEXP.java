package asia.virtualmc.vArchaeology.exp;

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

public class RestorationEXP {
    private final PlayerData playerData;
    private final TalentTree talentTree;
    private final Statistics statistics;
    private final Random random;
    private final ConcurrentMap<UUID, ArtefactRestoreData> artefactRestoreDataMap = new ConcurrentHashMap<>();

    public RestorationEXP(@NotNull StorageManager storageManager) {
        this.playerData = storageManager.getPlayerData();
        this.talentTree = storageManager.getTalentTree();
        this.statistics = storageManager.getStatistics();
        this.random = new Random();
    }

    private record ArtefactRestoreData(double baseMultiplier, boolean hasTalentID15) { }

    public void loadEXPData(@NotNull UUID uuid) {
        // Wisdom Trait: Block-break - 2% XP/level / Material-get - 1% XP/level / Artefact-restore: 0.5% XP/level
        int traitBonus = playerData.getWisdomTrait(uuid);
        // Rank Bonuses: 1% XP/level for both block-break and material-get, 0.25% XP/level for artefact-restore
        int rankBonus = Math.min(statistics.getDataFromMap(uuid, 1), 50);
        // XP Multiplier - global multiplier
        double archXPMul = playerData.getCurrentXPM(uuid);

        /* ------------------ Artefact-Restore Data ----------------- */
        // Adept Restoration
        int talentBonus4 = talentTree.getDataFromMap(uuid, 4);
        // Good Fortune
        boolean hasTalentID15 = (talentTree.getDataFromMap(uuid, 15) == 1);

        // Precompute the base multiplier for material-get EXP
        double artefactRestoreBaseMultiplier =
                (traitBonus * TalentTreeValues.wisdomEffects[2] + (talentBonus4) + (rankBonus * 0.25)) / 100.0
                        + archXPMul;

        ArtefactRestoreData artefactRestoreData = new ArtefactRestoreData(
                artefactRestoreBaseMultiplier,
                hasTalentID15
        );
        artefactRestoreDataMap.put(uuid, artefactRestoreData);
    }

    public void unloadEXPData(@NotNull UUID uuid) {
        artefactRestoreDataMap.remove(uuid);
    }

    public boolean hasEXPData(@NotNull UUID uuid) {
        return artefactRestoreDataMap.containsKey(uuid);
    }

    public double getTotalArtefactRestoreEXP(@NotNull UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return 0.0;

        ArtefactRestoreData data = artefactRestoreDataMap.get(uuid);
        if (data == null) {
            loadEXPData(uuid);
            data = artefactRestoreDataMap.get(uuid);
            if (data == null) {
                return 0.0;
            }
        }

        // Material Base EXP (level dependent)
        int archLevel = Math.min(playerData.getCurrentLevel(uuid), 99); // Level Limit
        double archRestoreXP = (Math.pow(archLevel/40.0, 6) * 650 + 1000);

        // Check for Talent ID 15
        if (data.hasTalentID15() && random.nextInt(100) < 5) {
            archRestoreXP *= 2;
        }

        double totalXP = data.baseMultiplier() * archRestoreXP;
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
