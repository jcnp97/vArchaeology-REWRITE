package asia.virtualmc.vArchaeology.items;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.storage.CollectionLog;
import asia.virtualmc.vLibrary.items.ItemsLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public class CustomMaterials {
    private final Main plugin;
    public static NamespacedKey ITEM_KEY;
    private final String ITEM_FILE = "items/items.yml";
    private static Map<Integer, ItemStack> itemCache;
    private CollectionLog collectionLog;

    public CustomMaterials(@NotNull ItemManager itemManager) {
        this.plugin = itemManager.getMain();
        this.collectionLog = itemManager.getStorageManager().getCollectionLog();
        ITEM_KEY = new NamespacedKey(plugin, "material_item");
        createItems();
    }

    private void createItems() {
        String ITEM_SECTION_PATH = "itemsList";
        Map<Integer, ItemStack> loadedItems = ItemsLib.loadItemsFromFile(
                plugin,
                ITEM_FILE,
                ITEM_SECTION_PATH,
                ITEM_KEY,
                Main.prefix,
                false
        );
        itemCache = Map.copyOf(loadedItems);
    }

    public static Map<Integer, ItemStack> getItemCache() {
        return itemCache;
    }

    public void dropMaterialNaturally(@NotNull Player player, Location blockLocation, int itemID) {
        ItemStack item = itemCache.get(itemID);
        UUID uuid = player.getUniqueId();
        if (item == null) {
            player.sendMessage("§cInvalid item ID: " + itemID);
            return;
        }
        blockLocation.getWorld().dropItemNaturally(blockLocation, item.clone());
        collectionLog.addCustomValueData(uuid, itemID, 1);
    }

    public void giveMaterialID(@NotNull Player player, int itemID, int amount) {
        ItemStack item = itemCache.get(itemID);
        if (item == null) {
            player.sendMessage("§cInvalid item ID: " + itemID);
            return;
        }

        if (!ItemsLib.giveItemID(player, item, amount)) {
            plugin.getLogger().severe("§There are issues when giving itemID: " + itemID
                    + " with key: " + ITEM_KEY + " to " + player.getName());
        }
    }

    public void takeMaterialID(@NotNull Player player, int itemID, int amount) {
        Map<Integer, ItemStack> itemsMap = ItemsLib.checkItemsToRemove(player, ITEM_KEY, itemID, amount);
        if (ItemsLib.removeItems(player, itemsMap)) {
            //add transaction logs here
            String itemName = itemCache.get(itemID).getItemMeta().getDisplayName();
            player.sendMessage("§cYour item: " + itemName + " x" + amount + " has been taken from you.");
        }
    }

    public void reloadConfig() {
        try {
            itemCache.clear();
            createItems();
        } catch (Exception e) {
            plugin.getLogger().severe("§There are issues when reloading " + ITEM_FILE + ": "
                    + e.getMessage());
        }
    }
}
