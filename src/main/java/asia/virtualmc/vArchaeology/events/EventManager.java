package asia.virtualmc.vArchaeology.events;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.core.CoreManager;
import asia.virtualmc.vArchaeology.exp.EXPManager;
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
    // other managers
    private final StorageManager storageManager;
    private final CoreManager coreManager;
    private final ItemManager itemManager;
    private final EXPManager expManager;
    // event classes
    private final PlayerJoinEvent playerJoinEvent;
    private final MiscellaneousEvent miscellaneousEvent;
    private final BlockBreakEvent blockBreakEvent;
    private final ItemEquipEvent itemEquipEvent;
    // event handlers
    private final ArtefactDiscoveryProgress artefactDiscoveryProgress;
    private final ToolStats toolStats;
    private final TraitData traitData;


    public EventManager(@NotNull Main plugin) {
        this.plugin = plugin;
        this.storageManager = plugin.getStorageManager();
        this.coreManager = plugin.getCoreManager();
        this.itemManager = plugin.getItemManager();
        this.expManager = plugin.getExpManager();
        this.miscellaneousEvent = new MiscellaneousEvent(this);
        this.artefactDiscoveryProgress = new ArtefactDiscoveryProgress(storageManager.getPlayerData());
        this.toolStats = new ToolStats(storageManager);
        this.traitData = new TraitData(storageManager.getPlayerData());

        List<BlockBreakHandler> blockBreak = Arrays.asList(
                artefactDiscoveryProgress,
                new ToolPassiveEffect(storageManager.getPlayerData(),
                        coreManager.getDropTable(),
                        itemManager.getCustomMaterials())
        );

        List<ItemEquipHandler> itemEquip = Arrays.asList(
                toolStats
        );

        List<PlayerJoinHandler> playerJoin = Arrays.asList(
                traitData
        );

        this.blockBreakEvent = new BlockBreakEvent(storageManager,
                toolStats,
                expManager.getBlockBreakEXP(), blockBreak);
        this.itemEquipEvent = new ItemEquipEvent(this, itemEquip);
        this.playerJoinEvent = new PlayerJoinEvent(storageManager, playerJoin);
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

    public ToolStats getToolStats() { return toolStats; }
}
