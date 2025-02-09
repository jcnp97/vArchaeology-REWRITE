package asia.virtualmc.vArchaeology.events;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.core.CoreManager;
import asia.virtualmc.vArchaeology.handlers.blockbreak.ArtefactDiscoveryProgress;
import asia.virtualmc.vArchaeology.handlers.blockbreak.ToolPassiveEffect;
import asia.virtualmc.vArchaeology.handlers.itemequip.ToolStats;
import asia.virtualmc.vArchaeology.handlers.playerjoin.TraitData;
import asia.virtualmc.vArchaeology.items.ItemManager;
import asia.virtualmc.vArchaeology.storage.StorageManager;
import asia.virtualmc.vLibrary.interfaces.BlockBreakHandler;
import asia.virtualmc.vLibrary.interfaces.ItemEquipHandler;
import asia.virtualmc.vLibrary.interfaces.PlayerJoinHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class EventManager {
    private final Main plugin;
    private final StorageManager storageManager;
    private final CoreManager coreManager;
    private final ItemManager itemManager;
    private final PlayerJoinEvent playerJoinEvent;
    private final MiscellaneousEvent miscellaneousEvent;
    private final BlockBreakEvent blockBreakEvent;
    private final ItemEquipEvent itemEquipEvent;

    public EventManager(@NotNull Main plugin) {
        this.plugin = plugin;
        this.storageManager = plugin.getStorageManager();
        this.coreManager = plugin.getCoreManager();
        this.itemManager = plugin.getItemManager();
        this.miscellaneousEvent = new MiscellaneousEvent(this);

        List<BlockBreakHandler> blockBreak = Arrays.asList(
                new ArtefactDiscoveryProgress(storageManager),
                new ToolPassiveEffect(storageManager, coreManager.getDropTable(), itemManager)
        );

        List<ItemEquipHandler> itemEquip = Arrays.asList(
                new ToolStats(storageManager)
        );

        List<PlayerJoinHandler> playerJoin = Arrays.asList(
                new TraitData(storageManager)
        );

        this.blockBreakEvent = new BlockBreakEvent(this, blockBreak);
        this.itemEquipEvent = new ItemEquipEvent(this, itemEquip);
        this.playerJoinEvent = new PlayerJoinEvent(this, playerJoin);
    }

    public Main getMain() {
        return plugin;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public PlayerJoinEvent getPlayerJoinEvent() {
        return playerJoinEvent;
    }

    public MiscellaneousEvent getMiscellaneousEvent() {
        return miscellaneousEvent;
    }

    public BlockBreakEvent getBlockBreakEvent() {
        return blockBreakEvent;
    }

    public ItemEquipEvent getItemEquipEvent() {
        return itemEquipEvent;
    }
}
