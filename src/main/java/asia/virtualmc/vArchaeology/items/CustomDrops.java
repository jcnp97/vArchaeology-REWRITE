package asia.virtualmc.vArchaeology.items;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.core.DropTable;
import asia.virtualmc.vArchaeology.global.GlobalManager;
import asia.virtualmc.vArchaeology.storage.CollectionLog;
import asia.virtualmc.vLibrary.items.DropsLib;
import asia.virtualmc.vLibrary.items.DropsLib;
import asia.virtualmc.vLibrary.utils.ConsoleMessageUtil;
import asia.virtualmc.vLibrary.utils.EffectsUtil;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public class CustomDrops {
    private final Main plugin;
    public static NamespacedKey ITEM_KEY;
    private final String ITEM_FILE = "items/drops.yml";
    private static Map<Integer, DropsLib.DropDetails> itemCache;
    private final CollectionLog collectionLog;

    public CustomDrops(@NotNull ItemManager itemManager) {
        this.plugin = itemManager.getMain();
        this.collectionLog = itemManager.getStorageManager().getCollectionLog();
        ITEM_KEY = new NamespacedKey(plugin, "material_item");
        createItems();
    }

    private void createItems() {
        Map<Integer, DropsLib.DropDetails> loadedItems = DropsLib.loadDropsFromFile(
                plugin,
                ITEM_FILE,
                ITEM_KEY,
                GlobalManager.prefix,
                false
        );
        itemCache = Map.copyOf(loadedItems);
        ConsoleMessageUtil.printLegacy(GlobalManager.coloredPrefix + "Loaded " +
                itemCache.size() + " items from " + ITEM_FILE);
    }

    public static Map<Integer, DropsLib.DropDetails> getItemCache() {
        return itemCache;
    }

    public void dropMaterialNaturally(@NotNull Player player, Location blockLocation, int itemID) {
        ItemStack item = itemCache.get(itemID).itemStack;
        UUID uuid = player.getUniqueId();
        if (item == null) {
            player.sendMessage("§cInvalid item ID: " + itemID + " from " + ITEM_FILE);
            return;
        }
        blockLocation.getWorld().dropItemNaturally(blockLocation, item.clone());
        collectionLog.addCustomValueData(uuid, itemID, 1);
    }

    public void giveMaterialID(@NotNull Player player, int itemID, int amount) {
        ItemStack item = itemCache.get(itemID).itemStack;
        if (item == null) {
            player.sendMessage("§cInvalid item ID: " + itemID);
            return;
        }

        if (!DropsLib.giveItemID(player, item, amount)) {
            plugin.getLogger().severe("§There are issues when giving itemID: " + itemID
                    + " with key: " + ITEM_KEY + " to " + player.getName());
        }
    }

    public void takeMaterialID(@NotNull Player player, int itemID, int amount) {
        Map<Integer, ItemStack> itemsMap = DropsLib.checkItemsToRemove(player, ITEM_KEY, itemID, amount);
        if (DropsLib.removeItems(player, itemsMap)) {
            //add transaction logs here
            String itemName = itemCache.get(itemID).itemStack.getItemMeta().getDisplayName();
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
