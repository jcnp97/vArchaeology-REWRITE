package asia.virtualmc.vArchaeology.events;

import asia.virtualmc.vArchaeology.Main;

import asia.virtualmc.vArchaeology.items.CustomMaterials;
import asia.virtualmc.vArchaeology.items.CustomTools;
import asia.virtualmc.vArchaeology.storage.CollectionLog;
import asia.virtualmc.vArchaeology.storage.PlayerData;
import asia.virtualmc.vArchaeology.storage.Statistics;
import asia.virtualmc.vArchaeology.storage.StorageManager;
import asia.virtualmc.vLibrary.configs.MaterialBlockConfig;
import asia.virtualmc.vLibrary.enums.EnumsLib;
import asia.virtualmc.vLibrary.interfaces.BlockBreakHandler;
import asia.virtualmc.vLibrary.items.ItemsLib;
import asia.virtualmc.vLibrary.items.ToolsLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
    private final CollectionLog collectionLog;
    private final PlayerData playerData;
    private final Statistics statistics;
    private final String repairCommand = "/varch repair";
    public static Map<Material, Integer> archBlocks;
    private final List<BlockBreakHandler> handlers;

    public BlockBreakEvent(@NotNull EventManager eventManager,
                           List<BlockBreakHandler> handlers) {
        this.handlers = handlers;
        this.plugin = eventManager.getMain();
        this.collectionLog = eventManager.getStorageManager().getCollectionLog();
        this.playerData = eventManager.getStorageManager().getPlayerData();
        this.statistics = eventManager.getStorageManager().getStatistics();
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

        Block block = event.getBlock();
        Material material = block.getType();
        Integer expValue = archBlocks.get(material);
        if (expValue == null) {
            return;
        }

        event.setDropItems(false);
        statistics.incrementData(uuid, 9);
        playerData.updateEXP(player, EnumsLib.UpdateType.ADD, expValue);

        for (BlockBreakHandler handler : handlers) {
            handler.onBlockBreakHandler(event);
        }
    }

    private boolean canProcessAction(Player player, UUID uuid, ItemStack item) {
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
