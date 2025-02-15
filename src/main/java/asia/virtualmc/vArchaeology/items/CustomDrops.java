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
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CustomDrops implements CustomItemsLib {
    private final Main plugin;
    private final CollectionLog collectionLog;
    private static NamespacedKey ITEM_KEY;
    private static final String ITEM_FILE = "items/drops.yml";
    private final Map<String, DropsLib.DropDetails> dropCache;
    private final Map<Integer, List<String>> dropByRarity;

    public CustomDrops(@NotNull ItemManager itemManager) {
        this.plugin = itemManager.getMain();
        this.collectionLog = itemManager.getStorageManager().getCollectionLog();
        this.dropCache = new HashMap<>();
        this.dropByRarity = new HashMap<>();
        ITEM_KEY = new NamespacedKey(plugin, "archaeology_drop");
        createItems();
    }

    @Override
    public void createItems() {
        Map<String, DropsLib.DropDetails> loadedItems = DropsLib.loadDropsFromFile(
                plugin,
                ITEM_FILE,
                ITEM_KEY,
                GlobalManager.prefix,
                false
        );

        dropCache.clear();
        dropCache.putAll(loadedItems);
        populateDropByRarity();

        ConsoleMessageUtil.printLegacy(GlobalManager.coloredPrefix + "Loaded " +
                dropCache.size() + " items from " + ITEM_FILE);
    }

    // Drop items naturally, used for block-break events
    public void dropItem(@NotNull Player player, int itemID, Location blockLocation) {
        String itemName = getRandomItem(itemID);
        ItemStack item = dropCache.get(itemName).itemStack;
        UUID uuid = player.getUniqueId();
        if (item == null) {
            player.sendMessage("§cInvalid item ID: " + itemName + " from " + ITEM_FILE);
            return;
        }
        blockLocation.getWorld().dropItemNaturally(blockLocation, item.clone());
        collectionLog.addCustomValueData(uuid, itemID, 1);
    }

    private String getRandomItem(int key) {
        List<String> items = dropByRarity.get(key);
        if (items == null || items.isEmpty()) {
            return null;
        }
        Random random = new Random();
        return items.get(random.nextInt(items.size()));
    }

    @Override
    public void giveItem(@NotNull Player player, @NotNull String itemName, int amount) {
        ItemStack item = dropCache.get(itemName).itemStack;
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
        return new ArrayList<>(dropCache.keySet());
    }

    private void populateDropByRarity() {
        dropByRarity.clear();

        for (Map.Entry<String, DropsLib.DropDetails> entry : dropCache.entrySet()) {
            String itemName = entry.getKey();
            ItemStack item = entry.getValue().itemStack;

            if (item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "rarity_id"), org.bukkit.persistence.PersistentDataType.INTEGER)) {
                Integer rarityID = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "rarity_id"), org.bukkit.persistence.PersistentDataType.INTEGER);

                if (rarityID != null) {
                    dropByRarity.computeIfAbsent(rarityID, k -> new ArrayList<>()).add(itemName);
                } else {
                    plugin.getLogger().warning("Rarity ID is missing for item: " + itemName);
                }
            }
        }
    }


    public Map<String, DropsLib.DropDetails> getItemsCache() {
        return new HashMap<>(dropCache);
    }

    public static NamespacedKey getItemKey() {
        return ITEM_KEY;
    }
}
