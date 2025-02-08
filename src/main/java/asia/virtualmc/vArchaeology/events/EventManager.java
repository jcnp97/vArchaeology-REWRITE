package asia.virtualmc.vArchaeology.events;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.storage.StorageManager;
import asia.virtualmc.vLibrary.VLibrary;
import org.jetbrains.annotations.NotNull;

public class EventManager {
    private final Main plugin;
    private final VLibrary vlib;
    private final StorageManager storageManager;
    private final PlayerJoinEvent playerJoinEvent;
    private final MiscellaneousEvent miscellaneousEvent;
    private final BlockBreakEvent blockBreakEvent;

    public EventManager(@NotNull Main plugin, @NotNull VLibrary vlib, @NotNull StorageManager storageManager) {
        this.plugin = plugin;
        this.vlib = vlib;
        this.storageManager = storageManager;
        this.playerJoinEvent = new PlayerJoinEvent(this);
        this.miscellaneousEvent = new MiscellaneousEvent(this);
        this.blockBreakEvent = new BlockBreakEvent(this);
    }

    public Main getMain() {
        return plugin;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }
}
