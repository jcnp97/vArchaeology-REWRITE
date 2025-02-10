package asia.virtualmc.vArchaeology.events;

import asia.virtualmc.vArchaeology.Main;

import asia.virtualmc.vArchaeology.exp.BlockBreakEXP;
import asia.virtualmc.vArchaeology.handlers.itemequip.ToolStats;
import asia.virtualmc.vArchaeology.items.CustomTools;
import asia.virtualmc.vArchaeology.storage.CollectionLog;
import asia.virtualmc.vArchaeology.storage.PlayerData;
import asia.virtualmc.vArchaeology.storage.Statistics;
import asia.virtualmc.vArchaeology.storage.StorageManager;
import asia.virtualmc.vLibrary.configs.MaterialBlockConfig;
import asia.virtualmc.vLibrary.enums.EnumsLib;
import asia.virtualmc.vLibrary.interfaces.BlockBreakHandler;
import asia.virtualmc.vLibrary.items.ToolsLib;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BlockBreakEvent implements Listener {
    private final Main plugin;
    private final StorageManager storageManager;
    private final CollectionLog collectionLog;
    private final PlayerData playerData;
    private final Statistics statistics;
    private final BlockBreakEXP blockBreakEXP;
    private final ToolStats toolStats;
    private final String repairCommand = "/varch repair";
    public static Map<Material, Integer> archBlocks;
    private final List<BlockBreakHandler> handlers;

    public BlockBreakEvent(@NotNull StorageManager storageManager,
                           @NotNull ToolStats toolStats,
                           @NotNull BlockBreakEXP blockBreakEXP,
                           List<BlockBreakHandler> handlers) {
        this.storageManager = storageManager;
        this.plugin = storageManager.getMain();
        this.toolStats = toolStats;
        this.collectionLog = storageManager.getCollectionLog();
        this.playerData = storageManager.getPlayerData();
        this.statistics = storageManager.getStatistics();
        this.blockBreakEXP = blockBreakEXP;
        this.handlers = handlers;

        archBlocks = MaterialBlockConfig.loadArchBlocks(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockBreak(org.bukkit.event.block.BlockBreakEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();

        if (!ToolsLib.isCustomTool(mainHandItem, CustomTools.TOOL_KEY)) {
            return;
        }

        if (!canProcessAction(player, uuid, mainHandItem)) {
            event.setCancelled(true);
            return;
        }

        // preload data map if currently not exist
        preloadAllData(player, uuid);

        Block block = event.getBlock();
        Material material = block.getType();
        Integer blockBaseEXP = archBlocks.get(material);
        if (blockBaseEXP == null) {
            return;
        }
        double finalEXP = blockBreakEXP.getTotalBlockBreakEXP(uuid, blockBaseEXP);

        // remove vanilla drops
        event.setDropItems(false);
        // increment blocks mined statistics
        statistics.incrementData(uuid, 9);
        // updateEXP
        playerData.updateEXP(player, EnumsLib.UpdateType.ADD, finalEXP);

        // iterate all handlers
        for (BlockBreakHandler handler : handlers) {
            handler.onBlockBreakHandler(event);
        }
    }

    private void preloadAllData(@NotNull Player player, @NotNull UUID uuid) {
        if (!toolStats.hasToolData(uuid)) toolStats.loadToolData(player);
    }

    private boolean canProcessAction(@NotNull Player player, @NotNull UUID uuid, ItemStack item) {
        if (player.hasPotionEffect(PotionEffectType.HASTE)) {
            player.sendMessage("§cYour haste buff prevents you from breaking this block.");
            return false;
        }

        if (ToolsLib.getDurability(item) <= 10) {
            player.sendMessage("§cYou need to repair your tool (using +" + repairCommand + " before you can use it again.");
            return false;
        }

        if (ToolsLib.getToolLevel(item, CustomTools.REQ_LEVEL_KEY) > playerData.getCurrentLevel(uuid)) {
            player.sendMessage("§cYou do not have the required level to use this tool!");
            return false;
        }
        return true;
    }
}
