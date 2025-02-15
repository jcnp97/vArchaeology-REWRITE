package asia.virtualmc.vArchaeology.items;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.global.GlobalManager;
import asia.virtualmc.vArchaeology.storage.CollectionLog;
import asia.virtualmc.vLibrary.interfaces.CustomItemsLib;
import asia.virtualmc.vLibrary.items.DropsLib;
import asia.virtualmc.vLibrary.utils.ConsoleMessageUtil;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CustomDrops implements CustomItemsLib {
    private final Main plugin;
    private final CollectionLog collectionLog;
    public static final NamespacedKey ITEM_KEY;
    public static final NamespacedKey NAME_KEY;
    private static final String ITEM_FILE = "items/drops.yml";
    private final Map<Integer, DropsLib.DropDetails> itemCache;
    private final Map<String, Integer> nameToIDCache;

    static {
        ITEM_KEY = new NamespacedKey(Main.getInstance(), "unidentified_artefact");
        NAME_KEY = new NamespacedKey(Main.getInstance(), "item_name");
    }

    public CustomDrops(@NotNull ItemManager itemManager) {
        this.plugin = itemManager.getMain();
        this.collectionLog = itemManager.getStorageManager().getCollectionLog();
        this.itemCache = new HashMap<>();
        this.nameToIDCache = new HashMap<>();
        createItems();
    }

    @Override
    public void createItems() {
        Map<Integer, DropsLib.DropDetails> loadedItems = DropsLib.loadDropsFromFile(
                plugin,
                ITEM_FILE,
                ITEM_KEY,
                GlobalManager.prefix,
                false
        );

        itemCache.clear();
        itemCache.putAll(loadedItems);
        populateNameToIDCache();
        ConsoleMessageUtil.printLegacy(GlobalManager.coloredPrefix + "Loaded " +
                itemCache.size() + " items from " + ITEM_FILE);
    }

    // Drop items naturally, used for block-break events
    public void dropItem(@NotNull Player player, Location blockLocation, int itemID) {
        ItemStack item = itemCache.get(itemID).itemStack;
        UUID uuid = player.getUniqueId();
        if (item == null) {
            player.sendMessage("§cInvalid item ID: " + itemID + " from " + ITEM_FILE);
            return;
        }
        blockLocation.getWorld().dropItemNaturally(blockLocation, item.clone());
        collectionLog.addCustomValueData(uuid, itemID, 1);
    }

    @Override
    public void giveItem(@NotNull Player player, @NotNull String itemName, int amount) {
        Integer itemID = nameToIDCache.get(itemName);
        ItemStack item = itemCache.get(itemID).itemStack;
        if (item == null) {
            player.sendMessage("§cInvalid item ID: " + itemID);
            return;
        }

        if (!DropsLib.giveItem(player, item, amount)) {
            plugin.getLogger().severe("§There are issues when giving itemID: " + itemID
                    + " with key: " + ITEM_KEY + " to " + player.getName());
        }
    }

    public void takeItem(@NotNull Player player, int itemID, int amount) {
        Map<Integer, ItemStack> itemsMap = DropsLib.checkItemsToRemove(player, ITEM_KEY, itemID, amount);
        if (DropsLib.removeItems(player, itemsMap)) {
            //add transaction logs here
            String itemName = itemCache.get(itemID).itemStack.getItemMeta().getDisplayName();
            player.sendMessage("§cYour item: " + itemName + " x" + amount + " has been taken from you.");
        }
    }

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
        for (DropsLib.DropDetails details : itemCache.values()) {
            if (details != null && details.itemStack != null && details.itemStack.hasItemMeta()) {
                String name = details.itemStack.getItemMeta().getPersistentDataContainer()
                        .get(NAME_KEY, PersistentDataType.STRING);
                if (name != null) {
                    itemNames.add(name);
                }
            }
        }
        return itemNames;
    }

    private void populateNameToIDCache() {
        nameToIDCache.clear();
        for (Map.Entry<Integer, DropsLib.DropDetails> entry : itemCache.entrySet()) {
            ItemStack item = entry.getValue().itemStack;
            if (item != null && item.hasItemMeta()) {
                String name = item.getItemMeta().getPersistentDataContainer()
                        .get(NAME_KEY, PersistentDataType.STRING);
                if (name != null) {
                    nameToIDCache.put(name, entry.getKey());
                }
            }
        }
    }


    public Map<Integer, DropsLib.DropDetails> getItemsCache() {
        return new HashMap<>(itemCache);
    }
}
