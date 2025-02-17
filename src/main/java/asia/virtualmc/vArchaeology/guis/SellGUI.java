package asia.virtualmc.vArchaeology.guis;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.global.MaterialDrop;
import asia.virtualmc.vArchaeology.handlers.player_join.SellData;
import asia.virtualmc.vArchaeology.items.CustomDrops;
import asia.virtualmc.vArchaeology.storage.PlayerData;
import asia.virtualmc.vArchaeology.storage.Statistics;
import asia.virtualmc.vArchaeology.storage.StorageManager;
import asia.virtualmc.vArchaeology.storage.TalentTree;

import asia.virtualmc.vLibrary.configs.GUIConfig;
import asia.virtualmc.vLibrary.core.EconomyLib;
import asia.virtualmc.vLibrary.guis.GUILib;
import asia.virtualmc.vLibrary.libs.inventoryframework.gui.GuiItem;
import asia.virtualmc.vLibrary.libs.inventoryframework.gui.type.ChestGui;
import asia.virtualmc.vLibrary.libs.inventoryframework.pane.OutlinePane;
import asia.virtualmc.vLibrary.libs.inventoryframework.pane.StaticPane;
import asia.virtualmc.vLibrary.utils.EffectsUtil;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SellGUI {
    private final Main plugin;
    private final Statistics statistics;
    private final SellData sellData;
    private final EconomyLib economyLib;

    public SellGUI(@NotNull StorageManager storageManager) {
        this.plugin = storageManager.getMain();
        this.statistics = storageManager.getStatistics();
        this.sellData = plugin.getEventManager().getSellData();
        this.economyLib = plugin.getVLibrary().getCoreManager().getEconomyLib();
    }

    public void openSellGUI(@NotNull Player player) {
        Map<ItemStack, Double> inventory = getSellableItems(player);
        double initialValue = calculateInventoryValue(inventory);
        double sellMultiplier = sellData.getSellData(player.getUniqueId()).drops();

        ChestGui gui = new ChestGui(5, GUIConfig.SELL_TITLE);
        gui.setOnGlobalClick(event -> event.setCancelled(true));
        StaticPane staticPane = new StaticPane(0, 0, 9, 5);

        // Add sell buttons
        for (int x = 1; x <= 3; x++) {
            ItemStack sellButton = createSellButton(initialValue);
            GuiItem guiItem = new GuiItem(sellButton, event -> processSellAction(player, initialValue, sellMultiplier));
            staticPane.addItem(guiItem, x, 4);
        }

        // Add close buttons
        for (int x = 5; x <= 7; x++) {
            ItemStack closeButton = GUILib.createCancelButton();
            staticPane.addItem(new GuiItem(closeButton, event -> event.getWhoClicked().closeInventory()), x, 4);
        }

        // create item displays for visualization
        OutlinePane itemPane = createItemPane(inventory);

        gui.addPane(staticPane);
        gui.addPane(itemPane);
        gui.show(player);
    }

    private ItemStack createSellButton(double totalValue) {
        String displayName = "§aSell Items";
        List<String> lore = List.of(
                "§7Current total: §2$" + totalValue
        );

        return GUILib.createDetailedButton(Material.PAPER,
                displayName, GUIConfig.INVISIBLE_ITEM, lore);
    }

    private void processSellAction(Player player, double initialValue, double multiplier) {
        Map<ItemStack, Double> inventory = getSellableItems(player);
        double finalValue = calculateInventoryValue(inventory);

        if (initialValue != finalValue) {
            player.sendMessage("§cError: Inventory has changed. Please reopen the GUI.");
            player.closeInventory();
            return;
        }

        try {
            if (finalValue > 0) {
                UUID uuid = player.getUniqueId();
                double taxPercentage = sellData.getSellData(uuid).taxes();
                sellItems(player, finalValue, inventory);
                double taxesPaid = economyLib.taxDeduction(player, taxPercentage, finalValue);
                sellEffects(player, uuid, finalValue, taxesPaid);
            } else {
                player.sendMessage("§cNo sellable items found in your inventory!");
            }
        } catch (Exception e) {
            player.sendMessage("§cAn error occurred while processing your sale. Please try again.");
            plugin.getLogger().severe("Error processing sale for " + player.getName() + ": " + e.getMessage());
            e.printStackTrace();
        }

        player.closeInventory();
    }

    private void sellItems(Player player, double finalValue, Map<ItemStack, Double> inventory) {
        try {
            for (Map.Entry<ItemStack, Double> entry : inventory.entrySet()) {
                ItemStack item = entry.getKey();
                player.getInventory().remove(item);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Error processing sale for " + player.getName() + ": " + e.getMessage());
        } finally {
            // Todo: Add transaction logs here
            economyLib.addEconomy(player, finalValue);
        }
    }

    private void sellEffects(Player player, UUID uuid, double finalValue, double taxesPaid) {
        statistics.addCustomValueData(uuid, 13, (int) finalValue);
        statistics.addCustomValueData(uuid, 14, (int) taxesPaid);
        EffectsUtil.sendPlayerMessage(player, "<#00FFA2>You have sold your items for <gold>$" + finalValue);
        EffectsUtil.playSound(player, "minecraft:cozyvanilla.sell_confirmed", Sound.Source.PLAYER, 1.0f, 1.0f);
    }

    private Map<ItemStack, Double> getSellableItems(Player player) {
        Map<ItemStack, Double> sellableItems = new HashMap<>();

        for (ItemStack item : player.getInventory().getContents()) {
            if (isSellable(item)) {
                double value = getItemValue(item) * item.getAmount();
                sellableItems.put(item, value);
            }
        }
        return sellableItems;
    }

    private static boolean isSellable(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return pdc.has(CustomDrops.getItemKey(), PersistentDataType.INTEGER);
    }

    private double getItemValue(ItemStack item) {
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        int rarityID = pdc.getOrDefault(CustomDrops.getRarityKey(), PersistentDataType.INTEGER, 0);

        return switch (rarityID) {
            case 1 -> MaterialDrop.dropPrice[0];
            case 2 -> MaterialDrop.dropPrice[1];
            case 3 -> MaterialDrop.dropPrice[2];
            case 4 -> MaterialDrop.dropPrice[3];
            case 5 -> MaterialDrop.dropPrice[4];
            case 6 -> MaterialDrop.dropPrice[5];
            case 7 -> MaterialDrop.dropPrice[6];
            default -> 0;
        };
    }

    private OutlinePane createItemPane(Map<ItemStack, Double> sellableItems) {
        OutlinePane itemPane = new OutlinePane(0, 0, 9, 4);

        for (ItemStack item : sellableItems.keySet()) {
            itemPane.addItem(new GuiItem(item.clone()));
        }

        return itemPane;
    }

    private double calculateInventoryValue(Map<ItemStack, Double> sellableItems) {
        double sum = sellableItems.values().stream().mapToDouble(Double::doubleValue).sum();
        return Math.round(sum * 100.0) / 100.0;
    }
}