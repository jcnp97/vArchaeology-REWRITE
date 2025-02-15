package asia.virtualmc.vArchaeology.items;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.global.GlobalManager;
import asia.virtualmc.vLibrary.interfaces.CustomItemsLib;
import asia.virtualmc.vLibrary.items.ItemsLib;
import asia.virtualmc.vLibrary.utils.ConsoleMessageUtil;
import asia.virtualmc.vLibrary.utils.EffectsUtil;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomCrafting implements CustomItemsLib {
    private final Main plugin;
    private static NamespacedKey ITEM_KEY;
    private static final String ITEM_FILE = "items/crafting-materials.yml";
    private final Map<String, ItemStack> craftingCache;

    static {

    }

    public CustomCrafting(@NotNull ItemManager itemManager) {
        this.plugin = itemManager.getMain();
        this.craftingCache = new HashMap<>();
        ITEM_KEY = new NamespacedKey(plugin, "exp_lamp");
        createItems();
    }

    @Override
    public void createItems() {
        Map<String, ItemStack> loadedItems = ItemsLib.loadItemsFromFile(
                plugin,
                ITEM_FILE,
                ITEM_KEY,
                GlobalManager.prefix,
                false
        );

        craftingCache.clear();
        craftingCache.putAll(loadedItems);

        ConsoleMessageUtil.printLegacy(GlobalManager.coloredPrefix + "Loaded " +
                craftingCache.size() + " items from " + ITEM_FILE);
    }

    public void dropItem(@NotNull Player player, Location blockLocation, String itemName) {
        ItemStack item = craftingCache.get(itemName);
        if (item == null) {
            player.sendMessage("§cInvalid item ID: " + itemName + " from " + ITEM_FILE);
            return;
        }
        blockLocation.getWorld().dropItemNaturally(blockLocation, item.clone());
        EffectsUtil.sendBroadcastMessage("<dark_red>" + player.getName() +
                " <gold>has obtained a " +
                EffectsUtil.convertLegacy(craftingCache.get(itemName).getItemMeta().getDisplayName()) +
                "<gold> while doing Archaeology!"
        );
    }

    @Override
    public void giveItem(@NotNull Player player, String itemName, int amount) {
        ItemStack item = craftingCache.get(itemName);
        if (item == null) {
            player.sendMessage("§cInvalid item ID: " + itemName + " from " + ITEM_FILE);
            return;
        }

        if (!ItemsLib.giveItem(player, item, amount)) {
            plugin.getLogger().severe("§There are issues when giving itemID: " + itemName
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

    @Override
    public void reloadConfig() {
        try {
            createItems();
        } catch (Exception e) {
            plugin.getLogger().severe("§There are issues when reloading " + ITEM_FILE + ": "
                    + e.getMessage());
        }
    }

    @Override
    public List<String> getItemNames() {
        return new ArrayList<>(craftingCache.keySet());
    }

    public Map<String, ItemStack> getItemsCache() {
        return new HashMap<>(craftingCache);
    }

    public static NamespacedKey getItemKey() {
        return ITEM_KEY;
    }
}
