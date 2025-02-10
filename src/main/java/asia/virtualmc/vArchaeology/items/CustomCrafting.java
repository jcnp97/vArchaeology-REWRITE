package asia.virtualmc.vArchaeology.items;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.global.GlobalManager;
import asia.virtualmc.vLibrary.items.ItemsLib;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class CustomCrafting {
    private final Main plugin;
    public static NamespacedKey ITEM_KEY;
    private final String ITEM_FILE = "items/crafting-materials.yml";
    private static Map<Integer, ItemStack> craftingCache;

    public CustomCrafting(@NotNull ItemManager itemManager) {
        this.plugin = itemManager.getMain();
        ITEM_KEY = new NamespacedKey(plugin, "varch_crafting");
        createItems();
    }

    private void createItems() {
        String ITEM_SECTION_PATH = "craftingList";
        Map<Integer, ItemStack> loadedItems = ItemsLib.loadItemsFromFile(
                plugin,
                ITEM_FILE,
                ITEM_SECTION_PATH,
                ITEM_KEY,
                GlobalManager.prefix,
                false
        );
        craftingCache = Map.copyOf(loadedItems);
    }

    public static Map<Integer, ItemStack> getItemCache() {
        return craftingCache;
    }

    public void giveMaterialID(@NotNull Player player, int itemID, int amount) {
        ItemStack item = craftingCache.get(itemID);
        if (item == null) {
            player.sendMessage("§cInvalid item ID: " + itemID + " from " + ITEM_FILE);
            return;
        }

        if (!ItemsLib.giveItemID(player, item, amount)) {
            plugin.getLogger().severe("§There are issues when giving itemID: " + itemID
                    + " with key: " + ITEM_KEY + " to " + player.getName());
        }
    }

//    public void takeMaterialID(@NotNull Player player, int itemID, int amount) {
//        Map<Integer, ItemStack> itemsMap = ItemsLib.checkItemsToRemove(player, ITEM_KEY, itemID, amount);
//        if (ItemsLib.removeItems(player, itemsMap)) {
//            //add transaction logs here
//            String itemName = itemCache.get(itemID).getItemMeta().getDisplayName();
//            player.sendMessage("§cYour item: " + itemName + " x" + amount + " has been taken from you.");
//        }
//    }

    public void reloadConfig() {
        try {
            craftingCache.clear();
            createItems();
        } catch (Exception e) {
            plugin.getLogger().severe("§There are issues when reloading " + ITEM_FILE + ": "
                    + e.getMessage());
        }
    }
}
