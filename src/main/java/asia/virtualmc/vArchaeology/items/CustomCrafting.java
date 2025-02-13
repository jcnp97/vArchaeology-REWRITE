package asia.virtualmc.vArchaeology.items;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.global.GlobalManager;
import asia.virtualmc.vLibrary.items.ItemsLib;
import asia.virtualmc.vLibrary.utils.ConsoleMessageUtil;
import asia.virtualmc.vLibrary.utils.EffectsUtil;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

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
        Map<Integer, ItemStack> loadedItems = ItemsLib.loadItemsFromFileNoID(
                plugin,
                ITEM_FILE,
                ITEM_SECTION_PATH,
                ITEM_KEY,
                GlobalManager.prefix,
                false
        );
        craftingCache = Map.copyOf(loadedItems);
        ConsoleMessageUtil.printLegacy(GlobalManager.coloredPrefix + "Loaded " +
                craftingCache.size() + " items from " + ITEM_FILE);
    }

    public static Map<Integer, ItemStack> getItemCache() {
        return craftingCache;
    }

    public void dropItemNaturally(@NotNull Player player, Location blockLocation, int itemID) {
        ItemStack item = craftingCache.get(itemID);
        if (item == null) {
            player.sendMessage("§cInvalid item ID: " + itemID + " from " + ITEM_FILE);
            return;
        }
        blockLocation.getWorld().dropItemNaturally(blockLocation, item.clone());
        EffectsUtil.sendBroadcastMessage("<dark_red>" + player.getName() +
                " <gold>has obtained a " +
                EffectsUtil.convertLegacy(craftingCache.get(itemID).getItemMeta().getDisplayName()) +
                "<gold> while doing Archaeology!"
        );
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
