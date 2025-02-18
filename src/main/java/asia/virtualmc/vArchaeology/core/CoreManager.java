package asia.virtualmc.vArchaeology.core;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.storage.StorageManager;
import org.jetbrains.annotations.NotNull;

public class CoreManager {
    private final Main plugin;
    private final StorageManager storageManager;
    private final DropTable dropTable;
    private final FishingGame fishingGame;

    public CoreManager(@NotNull Main plugin) {
        this.plugin = plugin;
        this.storageManager = plugin.getStorageManager();
        this.dropTable = new DropTable(storageManager);
        this.fishingGame = new FishingGame(plugin);
    }

    public Main getMain() {
        return plugin;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public DropTable getDropTable() { return  dropTable; };

    public FishingGame getFishingGame() { return fishingGame; }
}
