package asia.virtualmc.vArchaeology.items;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.storage.StorageManager;
import org.jetbrains.annotations.NotNull;

public class ItemManager {
    private final Main plugin;
    private final CustomDrops customDrops;
    private final CustomTools customTools;
    private final CustomBXPStars customBXPStars;
    private final CustomEXPLamps customEXPLamps;
    private final CustomCrafting customCrafting;
    private final CustomCharms customCharms;
    private final CustomUDArtefacts customUDArtefacts;
    private final StorageManager storageManager;

    public ItemManager(@NotNull Main plugin) {
        this.plugin = plugin;
        this.storageManager = plugin.getStorageManager();
        this.customDrops = new CustomDrops(this);
        this.customTools = new CustomTools(this);
        this.customBXPStars = new CustomBXPStars(storageManager);
        this.customEXPLamps = new CustomEXPLamps(storageManager);
        this.customCrafting = new CustomCrafting(this);
        this.customCharms = new CustomCharms(this);
        this.customUDArtefacts = new CustomUDArtefacts(this);
    }

    public Main getMain() {
        return plugin;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public CustomTools getCustomTools() { return customTools; }

    public CustomDrops getCustomDrops() { return customDrops; }

    public CustomBXPStars getCustomBXPStars() { return customBXPStars; }

    public CustomEXPLamps getCustomEXPLamps() { return customEXPLamps; }

    public CustomCrafting getCustomCrafting() { return customCrafting; }

    public CustomUDArtefacts getCustomUDArtefacts() { return customUDArtefacts; }

    public CustomCharms getCustomCharms() { return customCharms; }

    public void reloadItems() {
        customDrops.reloadConfig();
        customTools.reloadConfig();
        customBXPStars.reloadConfig();
        customEXPLamps.reloadConfig();
        customCrafting.reloadConfig();
        customUDArtefacts.reloadConfig();
        customCharms.reloadConfig();
    }
}
