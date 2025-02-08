package asia.virtualmc.vArchaeology.events;

import asia.virtualmc.vArchaeology.Main;

import asia.virtualmc.vArchaeology.storage.CollectionLog;
import asia.virtualmc.vArchaeology.storage.PlayerData;
import asia.virtualmc.vArchaeology.storage.Statistics;
import asia.virtualmc.vArchaeology.storage.StorageManager;
import asia.virtualmc.vLibrary.configs.MaterialBlockConfig;
import asia.virtualmc.vLibrary.enums.EnumsLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public class BlockBreakEvent implements Listener {
    private final Main plugin;
    private final CollectionLog collectionLog;
    private final PlayerData playerData;
    private final Statistics statistics;
    private final NamespacedKey TOOL_KEY;
    public static Map<Material, Integer> archBlocks;

    public BlockBreakEvent(@NotNull EventManager eventManager) {
        this.plugin = eventManager.getMain();
        this.collectionLog = eventManager.getStorageManager().getCollectionLog();
        this.playerData = eventManager.getStorageManager().getPlayerData();
        this.statistics = eventManager.getStorageManager().getStatistics();

        this.TOOL_KEY = new NamespacedKey(plugin, "varch_tool");
        archBlocks = MaterialBlockConfig.loadArchBlocks(plugin);

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockBreak(org.bukkit.event.block.BlockBreakEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();

//        if (!customTools.isArchTool(mainHandItem)) {
//            return;
//        }

//        if (!canBreakBlocks(player, uuid, mainHandItem)) {
//            event.setCancelled(true);
//            return;
//        }

        Block block = event.getBlock();
        Material material = block.getType();
        Integer expValue = archBlocks.get(material);
        if (expValue == null) {
            return;
        }

        event.setDropItems(false);
        statistics.incrementData(uuid, 9);
        playerData.updateEXP(player, EnumsLib.UpdateType.ADD, expValue);

//        if (!itemEquipListener.hasToolData(uuid)) {
//            itemEquipListener.addPlayerData(player, mainHandItem);
//        }

//        // Karma Trait - Two Drops & Next-Tier
//        // Dexterity Trait - Double Artefact Discovery Progress
//        if (!traitDataMap.containsKey(uuid)) {
//            addTraitData(player);
//        }
//
//        // Material Drops
//        if (random.nextDouble() < itemEquipListener.getGatherValue(uuid) / 100) {
//            Location blockLocation = event.getBlock().getLocation();
//            giveArchMaterialDrop(player, blockLocation, expValue, true);
//        } else {
//            playerData.updateExp(uuid, expManager.getTotalBlockBreakEXP(uuid, expValue), "add");
//        }
//
//        // Tier 99 Passive
//        if (itemEquipListener.hasTier99Value(uuid)) {
//            tier99Passive(player);
//        }
//
//        // Artefact Discovery Progress
//        if (canProgressAD(uuid)) {
//            addArtefactProgress(uuid);
//            if (random.nextDouble() < getDoubleADP(uuid) / 100) {
//                addArtefactProgress(uuid);
//                effectsUtil.sendPlayerMessage(uuid, "<green>Your Cosmic Focus (Dexterity Trait) has doubled your Artefact Progress gain.");
//            }
//        }
//
//        // Dexterity Bonus
//        if (random.nextDouble() < getAddADP(uuid) / 100) {
//            playerData.addArtefactDiscovery(uuid, 0.1);
//            effectsUtil.sendADBProgressBarTitle(uuid, playerData.getArchADP(uuid) / 100.0, 0.1);
//        }
//
//        if (random.nextInt(1, 2501) == 1) {
//            String blockType = material.name();
//            switch (blockType) {
//                case "SAND" -> giveCraftingMaterial(player, 2);
//                case "RED_SAND" -> giveCraftingMaterial(player, 3);
//                case "SOUL_SAND" -> giveCraftingMaterial(player, 4);
//                case "DIRT" -> giveCraftingMaterial(player, 5);
//                case "GRAVEL" -> giveCraftingMaterial(player, 6);
//            }
//        }
    }
}
