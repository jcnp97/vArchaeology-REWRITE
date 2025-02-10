package asia.virtualmc.vArchaeology.items;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.global.GlobalManager;
import asia.virtualmc.vLibrary.items.ItemsLib;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class CustomUDArtefacts {
    private final Main plugin;
    public static NamespacedKey ITEM_KEY;
    private final String ITEM_FILE = "items/artefacts.yml";
    private static Map<Integer, ItemStack> udArtefactCache;

    public CustomUDArtefacts(@NotNull ItemManager itemManager) {
        this.plugin = itemManager.getMain();
        ITEM_KEY = new NamespacedKey(plugin, "varch_artefact");
        createItems();
    }

    private void createItems() {
        String ITEM_SECTION_PATH = "artefactsList";
        Map<Integer, ItemStack> loadedItems = ItemsLib.loadItemsFromFile(
                plugin,
                ITEM_FILE,
                ITEM_SECTION_PATH,
                ITEM_KEY,
                GlobalManager.prefix,
                true
        );
        udArtefactCache = Map.copyOf(loadedItems);
    }

    public static Map<Integer, ItemStack> getItemCache() {
        return udArtefactCache;
    }

    public void giveMaterialID(@NotNull Player player, int itemID, int amount) {
        ItemStack item = udArtefactCache.get(itemID);
        if (item == null) {
            player.sendMessage("§cInvalid item ID: " + itemID + " from " + ITEM_FILE);
            return;
        }

        if (!ItemsLib.giveItemID(player, item, amount)) {
            plugin.getLogger().severe("§There are issues when giving itemID: " + itemID
                    + " with key: " + ITEM_KEY + " to " + player.getName());
        }
    }

    public void reloadConfig() {
        try {
            udArtefactCache.clear();
            createItems();
        } catch (Exception e) {
            plugin.getLogger().severe("§There are issues when reloading " + ITEM_FILE + ": "
                    + e.getMessage());
        }
    }
}
