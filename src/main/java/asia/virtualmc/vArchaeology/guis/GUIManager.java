package asia.virtualmc.vArchaeology.guis;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.storage.StorageManager;
import org.jetbrains.annotations.NotNull;

public class GUIManager {
    private final Main plugin;
    private final StorageManager storageManager;
    private final CollectionLogGUI collectionLogGUI;
    private final RestoredArtefactGUI restoredArtefactGUI;
    private final SellGUI sellGUI;

    public GUIManager(@NotNull Main plugin) {
        this.plugin = plugin;
        this.storageManager = plugin.getStorageManager();
        this.collectionLogGUI = new CollectionLogGUI(this);
        this.restoredArtefactGUI = new RestoredArtefactGUI(storageManager);
        this.sellGUI = new SellGUI(storageManager);
    }

    public Main getMain() {
        return plugin;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public CollectionLogGUI getCollectionLogGUI() { return collectionLogGUI; }

    public RestoredArtefactGUI getRestoredArtefactGUI() { return restoredArtefactGUI; }

    public SellGUI getSellGUI() { return sellGUI; }
}
