package asia.virtualmc.vArchaeology.events;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.core.CoreManager;
import asia.virtualmc.vArchaeology.exp.EXPManager;
import asia.virtualmc.vArchaeology.handlers.block_break.ArtefactDiscoveryProgress;
import asia.virtualmc.vArchaeology.handlers.block_break.CraftingProvider;
import asia.virtualmc.vArchaeology.handlers.block_break.DropProvider;
import asia.virtualmc.vArchaeology.handlers.item_equip.ToolStats;
import asia.virtualmc.vArchaeology.handlers.item_interact.BXPStar;
import asia.virtualmc.vArchaeology.handlers.item_interact.EXPLamp;
import asia.virtualmc.vArchaeology.handlers.player_join.TraitData;
import asia.virtualmc.vArchaeology.items.ItemManager;
import asia.virtualmc.vArchaeology.storage.StorageManager;
import asia.virtualmc.vLibrary.interfaces.BlockBreakHandler;
import asia.virtualmc.vLibrary.interfaces.ItemEquipHandler;
import asia.virtualmc.vLibrary.interfaces.ItemInteractHandler;
import asia.virtualmc.vLibrary.interfaces.PlayerJoinHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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
    private final ItemInteractEvent itemInteractEvent;
    // event handlers
    private final ArtefactDiscoveryProgress artefactDiscoveryProgress;
    private final ToolStats toolStats;
    private final TraitData traitData;
    private final DropProvider dropProvider;
    private final CraftingProvider craftingProvider;

    public EventManager(@NotNull Main plugin) {
        this.plugin = plugin;
        this.storageManager = plugin.getStorageManager();
        this.coreManager = plugin.getCoreManager();
        this.itemManager = plugin.getItemManager();
        this.expManager = plugin.getExpManager();
        this.miscellaneousEvent = new MiscellaneousEvent(this);

        this.traitData = new TraitData(storageManager.getPlayerData());
        this.toolStats = new ToolStats(storageManager);
        this.artefactDiscoveryProgress = new ArtefactDiscoveryProgress(
                storageManager.getPlayerData(), traitData, toolStats);
        this.dropProvider = new DropProvider(itemManager.getCustomDrops(),
                traitData, toolStats, coreManager.getDropTable(), storageManager,
                expManager.getMaterialGetEXP());
        this.craftingProvider = new CraftingProvider(itemManager.getCustomCrafting(),
                storageManager.getPlayerData());

        List<BlockBreakHandler> blockBreak = Arrays.asList(
                artefactDiscoveryProgress,
                dropProvider,
                craftingProvider
        );

        List<ItemEquipHandler> itemEquip = Arrays.asList(
                toolStats
        );

        List<PlayerJoinHandler> playerJoin = Arrays.asList(
                traitData
        );

        List<ItemInteractHandler> itemInteract = Arrays.asList(
                new EXPLamp(storageManager.getPlayerData()),
                new BXPStar(storageManager.getPlayerData())
        );

        this.blockBreakEvent = new BlockBreakEvent(storageManager,
                toolStats,
                expManager.getBlockBreakEXP(), blockBreak);
        this.itemEquipEvent = new ItemEquipEvent(this, itemEquip);
        this.playerJoinEvent = new PlayerJoinEvent(storageManager, this, coreManager.getDropTable(), playerJoin);
        this.itemInteractEvent = new ItemInteractEvent(this, itemInteract);
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

    public ArtefactDiscoveryProgress getArtefactDiscoveryProgress() { return artefactDiscoveryProgress; }

    public void unloadPlayerEventData(@NotNull UUID uuid) {
        toolStats.unloadToolData(uuid);
        expManager.unloadEXPData(uuid);
    }
}
