package asia.virtualmc.vArchaeology.core;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.global.MaterialDrop;
import asia.virtualmc.vArchaeology.storage.PlayerData;
import asia.virtualmc.vArchaeology.storage.TalentTree;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DropTable {
    private final Main plugin;
    private final Random random;
    private final TalentTree talentTree;
    private final PlayerData playerData;
    public static final Map<UUID, List<Integer>> playerDropTables = new ConcurrentHashMap<>();

    public DropTable(@NotNull CoreManager coreManager) {
        this.plugin = coreManager.getMain();
        this.talentTree = coreManager.getStorageManager().getTalentTree();
        this.playerData = coreManager.getStorageManager().getPlayerData();
        this.random = new Random();
    }

    public void loadPlayerDropTable(@NotNull UUID uuid, int archLevel) {
        if (playerDropTables.containsKey(uuid)) return;
        
        List<Integer> dropTable = new ArrayList<>();
        dropTable.add(MaterialDrop.dropWeight[0]);
        if (archLevel >= 10) dropTable.add(MaterialDrop.dropWeight[1]);
        if (archLevel >= 20) dropTable.add(MaterialDrop.dropWeight[2]);
        if (archLevel >= 30) dropTable.add(MaterialDrop.dropWeight[3]);
        if (archLevel >= 40) dropTable.add(MaterialDrop.dropWeight[4]);
        if (archLevel >= 50) dropTable.add(MaterialDrop.dropWeight[5]);
        if (archLevel >= 60) dropTable.add(MaterialDrop.dropWeight[6]);
        if (talentTree.getDataFromMap(uuid, 10) == 1) dropTable.set(6, MaterialDrop.dropWeight[6] + 3);
        playerDropTables.put(uuid, dropTable);
    }

    public void unloadPlayerDropTable(@NotNull UUID uuid) {
        playerDropTables.remove(uuid);
    }
    
    public int rollDropTable(@NotNull UUID uuid) {
        List<Integer> dropTable = playerDropTables.get(uuid);
        if (dropTable == null || dropTable.isEmpty()) {
            loadPlayerDropTable(uuid, playerData.getCurrentLevel(uuid));
        }

        int totalWeight = dropTable.stream().mapToInt(Integer::intValue).sum();
        int randomNumber = random.nextInt(totalWeight) + 1;

        int cumulativeWeight = 0;
        for (int i = 0; i < dropTable.size(); i++) {
            cumulativeWeight += dropTable.get(i);
            if (randomNumber <= cumulativeWeight) {
                return i + 1;
            }
        }
        return 0;
    }
}
