package asia.virtualmc.vArchaeology.guis;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.global.GlobalManager;
import asia.virtualmc.vArchaeology.storage.CollectionLog;
import asia.virtualmc.vLibrary.configs.CollectionLogConfig;
import asia.virtualmc.vLibrary.guis.GUILib;
import asia.virtualmc.vLibrary.libs.inventoryframework.gui.GuiItem;
import asia.virtualmc.vLibrary.libs.inventoryframework.gui.type.ChestGui;
import asia.virtualmc.vLibrary.libs.inventoryframework.pane.StaticPane;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CollectionLogGUI {
    private final Main plugin;
    private final CollectionLog collectionLog;
    private final int itemsPerPage = 45;
    private final int totalPages;
    private final int totalCollections;
    private final Map<Integer, ItemStack> collectionsMap;
    // Global base cache for collection items (the unacquired version)
    private final Map<Integer, ItemStack> baseCollectionCache = new HashMap<>();

    public CollectionLogGUI(@NotNull GUIManager guiManager) {
        this.plugin = guiManager.getMain();
        this.collectionLog = guiManager.getStorageManager().getCollectionLog();
        this.collectionsMap = CollectionLogConfig.loadCLFileForGUI(plugin, "collection-log.yml", GlobalManager.prefix);
        this.totalCollections = collectionsMap.size();
        this.totalPages = Math.max(1, (totalCollections + itemsPerPage - 1) / itemsPerPage);

        // Build the global cache: for each collection item, create the base item using FLINT.
        // Both acquired and unacquired items will use FLINT; only custom model data and lore will change.
        for (Map.Entry<Integer, ItemStack> entry : collectionsMap.entrySet()) {
            int id = entry.getKey();
            ItemStack original = entry.getValue();
            ItemStack baseItem = new ItemStack(Material.FLINT);
            ItemMeta meta = baseItem.getItemMeta();
            if (meta == null) continue;
            meta.setDisplayName("§6" + getDisplayName(id));
            meta.setCustomModelData(99999); // Base custom model data for the unacquired version.
            // Copy the lore from the original item if it exists.
            ItemMeta originalMeta = original.getItemMeta();
            List<String> lore = (originalMeta != null && originalMeta.getLore() != null)
                    ? new ArrayList<>(originalMeta.getLore())
                    : new ArrayList<>();
            meta.setLore(lore);
            baseItem.setItemMeta(meta);
            baseCollectionCache.put(id, baseItem);
        }
    }

    public void openCollectionLog(Player player, int pageNumber) {
        pageNumber = Math.min(Math.max(1, pageNumber), totalPages);
        UUID uuid = player.getUniqueId();
        // Retrieve the player's collection data (a concurrent map is returned)
        Map<Integer, Integer> playerCollection = collectionLog.getAllDataFromMap().get(uuid);
        if (playerCollection == null) {
            playerCollection = Collections.emptyMap();
        }

        ChestGui gui = new ChestGui(6, GUILib.getGUIDesign(pageNumber, totalPages));
        gui.setOnGlobalClick(event -> event.setCancelled(true));
        StaticPane staticPane = new StaticPane(0, 0, 9, 6);

        // Add collection items to the pane
        int startIndex = (pageNumber - 1) * itemsPerPage;
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 9; col++) {
                int itemID = startIndex + (row * 9) + col + 1;
                if (itemID <= totalCollections) {
                    Integer amount = playerCollection.getOrDefault(itemID, 0);
                    ItemStack baseItem = baseCollectionCache.get(itemID);
                    if (baseItem == null) continue;
                    // Clone the base item
                    ItemStack displayItem = baseItem.clone();
                    if (amount > 0) {
                        ItemMeta meta = displayItem.getItemMeta();
                        if (meta != null) {
                            // Update custom model data for the acquired state.
                            meta.setCustomModelData(99999 + itemID);
                            // Append acquired lore information.
                            List<String> lore = new ArrayList<>(meta.getLore());
                            lore.add("§7Acquired: §e" + amount);
                            meta.setLore(lore);
                            displayItem.setItemMeta(meta);
                        }
                    }
                    staticPane.addItem(new GuiItem(displayItem), col, row);
                }
            }
        }

        addNavigationButtons(staticPane, pageNumber, player);

        // Add close buttons.
        for (int x = 3; x <= 5; x++) {
            ItemStack closeButton = GUILib.createCloseButton();
            staticPane.addItem(new GuiItem(closeButton, event -> event.getWhoClicked().closeInventory()), x, 5);
        }

        gui.addPane(staticPane);
        gui.show(player);
    }

    private void addNavigationButtons(StaticPane pane, int currentPage, Player player) {
        if (currentPage < totalPages) {
            ItemStack nextButton = GUILib.createNextButton();
            GuiItem nextItem = new GuiItem(nextButton, event -> openCollectionLog(player, currentPage + 1));
            pane.addItem(nextItem, 6, 5);
        }
        if (currentPage > 1) {
            ItemStack previousButton = GUILib.createPreviousButton();
            GuiItem prevItem = new GuiItem(previousButton, event -> openCollectionLog(player, currentPage - 1));
            pane.addItem(prevItem, 2, 5);
        }
    }

    private String getDisplayName(int collectionID) {
        ItemStack item = collectionsMap.get(collectionID);
        if (item == null) {
            plugin.getLogger().warning("Item with ID " + collectionID + " not found in collectionCache.");
            return "Unknown Item";
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return "Unknown Item";
        }
        return meta.getDisplayName();
    }
}
