package asia.virtualmc.vArchaeology.core;

import asia.virtualmc.vArchaeology.global.MaterialDrop;
import asia.virtualmc.vArchaeology.storage.StorageManager;
import asia.virtualmc.vArchaeology.storage.TalentTree;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DropTable {
    private final Random random;
    private final TalentTree talentTree;
    private final Map<UUID, List<Integer>> playerDropTables;

    public DropTable(@NotNull StorageManager storageManager) {
        this.talentTree = storageManager.getTalentTree();
        this.playerDropTables = new ConcurrentHashMap<>();
        this.random = new Random();
    }

    public void loadDropTable(@NotNull UUID uuid, int archLevel) {
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

    public void unloadDropTable(@NotNull UUID uuid) {
        playerDropTables.remove(uuid);
    }
    
    public int rollDropTable(@NotNull UUID uuid) {
        List<Integer> dropTable = playerDropTables.get(uuid);

        int totalWeight = dropTable.stream().mapToInt(Integer::intValue).sum();
        int randomNumber = random.nextInt(totalWeight) + 1;

        int cumulativeWeight = 0;
        for (int i = 0; i < dropTable.size(); i++) {
            cumulativeWeight += dropTable.get(i);
            if (randomNumber <= cumulativeWeight) {
                //Bukkit.getPlayer(uuid).sendMessage("Number: " + (i + 1) + " over " + totalWeight);
                return i + 1;
            }
        }
        return 0;
    }

    public boolean hasDropTable(@NotNull UUID uuid) {
        return playerDropTables.containsKey(uuid);
    }
}
