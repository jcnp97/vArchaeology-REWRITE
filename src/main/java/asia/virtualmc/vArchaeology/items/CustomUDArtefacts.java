package asia.virtualmc.vArchaeology.items;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.global.GlobalManager;
import asia.virtualmc.vLibrary.interfaces.CustomItemsLib;
import asia.virtualmc.vLibrary.items.DropsLib;
import asia.virtualmc.vLibrary.items.ItemsLib;
import asia.virtualmc.vLibrary.utils.ConsoleMessageUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomUDArtefacts implements CustomItemsLib {
    private final Main plugin;
    public static final NamespacedKey ITEM_KEY;
    public static final NamespacedKey NAME_KEY;
    private static final String ITEM_FILE = "items/unidentified-artefacts.yml";
    private final Map<Integer, ItemStack> udArtefactCache;
    private final Map<String, Integer> nameToIDCache;

    static {
        ITEM_KEY = new NamespacedKey(Main.getInstance(), "unidentified_artefact");
        NAME_KEY = new NamespacedKey(Main.getInstance(), "item_name");
    }

    public CustomUDArtefacts(@NotNull ItemManager itemManager) {
        this.plugin = itemManager.getMain();
        this.udArtefactCache = new HashMap<>();
        this.nameToIDCache = new HashMap<>();
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
                true
        );

        udArtefactCache.clear();
        udArtefactCache.putAll(loadedItems);
        populateNameToIDCache();

        ConsoleMessageUtil.printLegacy(GlobalManager.coloredPrefix + "Loaded " +
                udArtefactCache.size() + " items from " + ITEM_FILE);
    }

    @Override
    public void giveItem(@NotNull Player player, String itemName, int amount) {
        Integer itemID = nameToIDCache.get(itemName);
        ItemStack item = udArtefactCache.get(itemID);
        if (item == null) {
            player.sendMessage("§cInvalid item ID: " + itemID + " from " + ITEM_FILE);
            return;
        }

        if (!ItemsLib.giveItemID(player, item, amount)) {
            plugin.getLogger().severe("There are issues when giving itemID: " + itemID
                    + " with key: " + ITEM_KEY + " to " + player.getName());
        }
    }

    @Override
    public void reloadConfig() {
        try {
            createItems();
        } catch (Exception e) {
            plugin.getLogger().severe("There are issues when reloading " + ITEM_FILE + ": "
                    + e.getMessage());
        }
    }

    @Override
    public List<String> getItemNames() {
        List<String> itemNames = new ArrayList<>();
        for (ItemStack item : udArtefactCache.values()) {
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

    private void populateNameToIDCache() {
        nameToIDCache.clear();
        for (Map.Entry<Integer, ItemStack> entry : udArtefactCache.entrySet()) {
            ItemStack item = entry.getValue();
            if (item != null && item.hasItemMeta()) {
                String name = item.getItemMeta().getPersistentDataContainer()
                        .get(NAME_KEY, PersistentDataType.STRING);
                if (name != null) {
                    nameToIDCache.put(name, entry.getKey());
                }
            }
        }
    }

    public Map<Integer, ItemStack> getItemsCache() {
        return new HashMap<>(udArtefactCache);
    }
}