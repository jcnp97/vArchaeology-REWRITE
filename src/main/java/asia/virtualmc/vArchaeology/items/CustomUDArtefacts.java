package asia.virtualmc.vArchaeology.items;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.global.GlobalManager;
import asia.virtualmc.vLibrary.interfaces.CustomItemsLib;
import asia.virtualmc.vLibrary.items.ItemsLib;
import asia.virtualmc.vLibrary.utils.ConsoleMessageUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomUDArtefacts implements CustomItemsLib {
    private final Main plugin;
    private static NamespacedKey ITEM_KEY;
    private static final String ITEM_FILE = "items/unidentified-artefacts.yml";
    private final Map<String, ItemStack> udArtefactCache;

    public CustomUDArtefacts(@NotNull ItemManager itemManager) {
        this.plugin = itemManager.getMain();
        this.udArtefactCache = new HashMap<>();
        ITEM_KEY = new NamespacedKey(plugin, "unidentified_artefact");
        createItems();
    }

    @Override
    public void createItems() {
        Map<String, ItemStack> loadedItems = ItemsLib.loadItemsFromFile(
                plugin,
                ITEM_FILE,
                ITEM_KEY,
                GlobalManager.prefix,
                true
        );

        udArtefactCache.clear();
        udArtefactCache.putAll(loadedItems);

        ConsoleMessageUtil.printLegacy(GlobalManager.coloredPrefix + "Loaded " +
                udArtefactCache.size() + " items from " + ITEM_FILE);
    }

    @Override
    public void giveItem(@NotNull Player player, String itemName, int amount) {
        ItemStack item = udArtefactCache.get(itemName);
        if (item == null) {
            player.sendMessage("§cInvalid item ID: " + itemName + " from " + ITEM_FILE);
            return;
        }

        if (!ItemsLib.giveItem(player, item, amount)) {
            plugin.getLogger().severe("There are issues when giving itemID: " + itemName
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
        return new ArrayList<>(udArtefactCache.keySet());
    }

    public Map<String, ItemStack> getItemsCache() {
        return new HashMap<>(udArtefactCache);
    }

    public static NamespacedKey getItemKey() {
        return ITEM_KEY;
    }
}