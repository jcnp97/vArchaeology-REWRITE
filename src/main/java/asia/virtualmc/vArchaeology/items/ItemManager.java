package asia.virtualmc.vArchaeology.items;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.storage.StorageManager;
import org.jetbrains.annotations.NotNull;

public class ItemManager {
    private final Main plugin;
    private final CustomMaterials customMaterials;
    private final CustomTools customTools;
    private final CustomBXPStars customBXPStars;
    private final CustomEXPLamps customEXPLamps;
    private final StorageManager storageManager;

    public ItemManager(@NotNull Main plugin) {
        this.plugin = plugin;
        this.storageManager = plugin.getStorageManager();
        this.customMaterials = new CustomMaterials(this);
        this.customTools = new CustomTools(this);
        this.customBXPStars = new CustomBXPStars(this);
        this.customEXPLamps = new CustomEXPLamps(this);
    }

    public Main getMain() {
        return plugin;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public CustomTools getCustomTools() { return customTools; }

    public CustomMaterials getCustomMaterials() { return customMaterials; }

    public CustomBXPStars getCustomBXPStars() { return customBXPStars; }

    public CustomEXPLamps getCustomEXPLamps() { return customEXPLamps; }

    public void reloadItems() {
        customMaterials.reloadConfig();
        customTools.reloadConfig();
        customBXPStars.reloadConfig();
        customEXPLamps.reloadConfig();
    }
}
