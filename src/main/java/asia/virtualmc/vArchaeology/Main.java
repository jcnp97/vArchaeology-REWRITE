package asia.virtualmc.vArchaeology;

import asia.virtualmc.vArchaeology.storage.StorageManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    public static final String prefix = "[vArchaeology] ";
    private StorageManager storageManager;

    @Override
    public void onEnable() {
        this.storageManager = new StorageManager(this);
    }

    @Override
    public void onDisable() {
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }
}
