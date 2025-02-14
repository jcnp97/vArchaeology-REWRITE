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
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomCrafting implements CustomItemsLib {
    private final Main plugin;
    public static final NamespacedKey ITEM_KEY;
    public static final NamespacedKey NAME_KEY;
    private static final String ITEM_FILE = "items/crafting-materials.yml";
    private final Map<Integer, ItemStack> craftingCache;

    static {
        ITEM_KEY = new NamespacedKey(Main.getInstance(), "exp_lamp");
        NAME_KEY = new NamespacedKey(Main.getInstance(), "item_name");
    }

    public CustomCrafting(@NotNull ItemManager itemManager) {
        this.plugin = itemManager.getMain();
        this.craftingCache = new HashMap<>();
        createItems();
    }

    @Override
    public void createItems() {
        Map<Integer, ItemStack> loadedItems = ItemsLib.loadItemsFromFile(
                plugin,
                ITEM_FILE,
                ITEM_KEY,
                NAME_KEY,
                GlobalManager.prefix,
                false
        );

        craftingCache.clear();
        craftingCache.putAll(loadedItems);

        ConsoleMessageUtil.printLegacy(GlobalManager.coloredPrefix + "Loaded " +
                craftingCache.size() + " items from " + ITEM_FILE);
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

    @Override
    public void giveItem(@NotNull Player player, int itemID, int amount) {
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
        List<String> itemNames = new ArrayList<>();
        for (ItemStack item : craftingCache.values()) {
            if (item != null && item.hasItemMeta()) {
                String name = item.getItemMeta().getPersistentDataContainer()
                        .get(NAME_KEY, PersistentDataType.STRING);
                if (name != null) {
                    itemNames.add(name);
                }
            }
        }
        return itemNames;
    }

    public Map<Integer, ItemStack> getItemsCache() {
        return new HashMap<>(craftingCache);
    }
}
