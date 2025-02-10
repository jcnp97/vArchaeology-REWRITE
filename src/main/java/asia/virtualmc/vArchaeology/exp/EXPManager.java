package asia.virtualmc.vArchaeology.exp;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.storage.StorageManager;
import org.jetbrains.annotations.NotNull;

public class EXPManager {
    private final Main plugin;
    private final StorageManager storageManager;
    private final BlockBreakEXP blockBreakEXP;
    //private final MaterialGetEXP materialGetEXP;
    //private final RestorationEXP restorationEXP;

    public EXPManager(@NotNull Main plugin) {
        this.plugin = plugin;
        this.storageManager = plugin.getStorageManager();
        this.blockBreakEXP = new BlockBreakEXP(storageManager);
        //this.materialGetEXP = new MaterialGetEXP(storageManager);
        //this.restorationEXP = new RestorationEXP(storageManager);
    }

    public Main getMain() {
        return plugin;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public BlockBreakEXP getBlockBreakEXP() { return blockBreakEXP; }

    //public MaterialGetEXP getMaterialGetEXP() { return materialGetEXP; }

    //public RestorationEXP getRestorationEXP() { return restorationEXP; }
}
