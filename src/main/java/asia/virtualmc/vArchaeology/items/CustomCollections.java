package asia.virtualmc.vArchaeology.items;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.global.GlobalManager;
import asia.virtualmc.vArchaeology.storage.CollectionLog;
import asia.virtualmc.vLibrary.configs.CollectionLogConfig;
import asia.virtualmc.vLibrary.interfaces.CustomItemsLib;
import asia.virtualmc.vLibrary.items.DropsLib;
import asia.virtualmc.vLibrary.items.ItemsLib;
import asia.virtualmc.vLibrary.utils.ConsoleMessageUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CustomCollections implements CustomItemsLib {
    private final Main plugin;
    private final CollectionLog collectionLog;
    private static NamespacedKey ITEM_KEY;
    private static final String ITEM_FILE = "items/collections.yml";
    private final Map<String, ItemStack> collectionItemCache;
    private final Map<Integer, List<String>> collectionByGroup;

    public CustomCollections(@NotNull ItemManager itemManager) {
        this.plugin = itemManager.getMain();
        this.collectionLog = itemManager.getStorageManager().getCollectionLog();
        this.collectionItemCache = new HashMap<>();
        this.collectionByGroup = new HashMap<>();
        ITEM_KEY = new NamespacedKey(plugin, "collection_item");
        createItems();
    }

    @Override
    public void createItems() {
        Map<String, ItemStack> loadedItems = CollectionLogConfig.loadCLFileForItem(
                plugin,
                ITEM_FILE,
                ITEM_KEY,
                GlobalManager.prefix
        );

        collectionItemCache.clear();
        collectionItemCache.putAll(loadedItems);
        populateCollectionByGroup();

        ConsoleMessageUtil.printLegacy(GlobalManager.coloredPrefix + "Loaded " +
                collectionItemCache.size() + " items from " + ITEM_FILE);
    }

    private String getRandomName(int key) {
        List<String> items = collectionByGroup.get(key);
        if (items == null || items.isEmpty()) {
            return null;
        }
        Random random = new Random();
        return items.get(random.nextInt(items.size()));
    }

    public void giveRandomCollection(@NotNull Player player, int groupID) {
        String itemName = getRandomName(groupID);
        ItemStack item = collectionItemCache.get(itemName);
        if (item == null) {
            player.sendMessage("§cInvalid item ID: " + itemName);
            return;
        }

        if (!DropsLib.giveItem(player, item, 1)) {
            plugin.getLogger().severe("§There are issues when giving itemID: " + itemName
                    + " with key: " + ITEM_KEY + " to " + player.getName());
        }
        collectionLog.incrementData(player.getUniqueId(), ItemsLib.getItemID(item, ITEM_KEY));
    }

    @Override
    public void giveItem(@NotNull Player player, @NotNull String itemName, int amount) {
        ItemStack item = collectionItemCache.get(itemName);
        if (item == null) {
            player.sendMessage("§cInvalid item ID: " + itemName);
            return;
        }

        if (!DropsLib.giveItem(player, item, amount)) {
            plugin.getLogger().severe("§There are issues when giving itemID: " + itemName
                    + " with key: " + ITEM_KEY + " to " + player.getName());
        }
    }

    public void takeItem(@NotNull Player player, int rarityID, int amount) {
        Map<Integer, ItemStack> itemsMap = DropsLib.checkItemsToRemove(player, ITEM_KEY, rarityID, amount);
        if (DropsLib.removeItems(player, itemsMap)) {
            //add transaction logs here
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
        return new ArrayList<>(collectionItemCache.keySet());
    }

    private void populateCollectionByGroup() {
        collectionByGroup.clear();

        for (Map.Entry<String, ItemStack> entry : collectionItemCache.entrySet()) {
            String itemName = entry.getKey();
            ItemStack item = entry.getValue();

            if (item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "rarity_id"), org.bukkit.persistence.PersistentDataType.INTEGER)) {
                Integer rarityID = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "rarity_id"), org.bukkit.persistence.PersistentDataType.INTEGER);

                if (rarityID != null) {
                    collectionByGroup.computeIfAbsent(rarityID, k -> new ArrayList<>()).add(itemName);
                } else {
                    plugin.getLogger().warning("Rarity ID is missing for item: " + itemName);
                }
            }
        }
    }

    public Map<String, ItemStack> getItemsCache() {
        return new HashMap<>(collectionItemCache);
    }

    public static NamespacedKey getItemKey() {
        return ITEM_KEY;
    }
}
