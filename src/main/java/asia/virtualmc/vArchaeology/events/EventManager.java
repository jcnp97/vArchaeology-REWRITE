package asia.virtualmc.vArchaeology.events;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.storage.StorageManager;
import asia.virtualmc.vLibrary.VLibrary;
import org.jetbrains.annotations.NotNull;

public class EventManager {
    private final Main plugin;
    private final VLibrary vlib;
    private final StorageManager storageManager;
    private final PlayerJoin playerJoin;

    public EventManager(@NotNull Main plugin, @NotNull VLibrary vlib, @NotNull StorageManager storageManager) {
        this.plugin = plugin;
        this.vlib = vlib;
        this.storageManager = storageManager;
        this.playerJoin = new PlayerJoin(this);
    }

    public Main getMain() {
        return plugin;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }
}
