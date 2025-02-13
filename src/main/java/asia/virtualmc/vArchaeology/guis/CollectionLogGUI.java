package asia.virtualmc.vArchaeology.guis;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.global.GlobalManager;
import asia.virtualmc.vArchaeology.storage.CollectionLog;
import asia.virtualmc.vLibrary.configs.CollectionLogConfig;
import asia.virtualmc.vLibrary.guis.CollectionLogGUILib;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CollectionLogGUI {
    private final CollectionLogGUILib collectionLogGUILib;
    private final CollectionLog collectionLog;
    private final Map<Integer, ItemStack> collectionsMap;
    private final int totalCollections;

    public CollectionLogGUI(@NotNull GUIManager guiManager) {
        Main plugin = guiManager.getMain();
        this.collectionLogGUILib = plugin.getVLibrary().getGuiManager().getCollectionLogGUI();
        this.collectionLog = guiManager.getStorageManager().getCollectionLog();

        // Use thread-safe ConcurrentHashMap
        this.collectionsMap = new ConcurrentHashMap<>(CollectionLogConfig.loadCLFileForGUI(
                plugin, "collection-log.yml", GlobalManager.prefix));

        this.totalCollections = collectionsMap.size();
    }

    // Method below used for collection.getAllDataFromMap
    // public Map<UUID, Map<Integer, Integer>> getAllDataFromMap() {
    //        return new ConcurrentHashMap<>(collectionsMap);
    // }
    public void openCollectionLog(@NotNull Player player) {
        UUID uuid = player.getUniqueId();
        collectionLogGUILib.openCollectionLog(player, collectionsMap, collectionLog.getAllDataFromMap().get(uuid), totalCollections, 1);
    }
}

//    public void openCollectionLog(Player player, int pageNumber) {
//        pageNumber = Math.min(Math.max(1, pageNumber), totalPages);
//        UUID uuid = player.getUniqueId();
//        Map<Integer, Integer> playerCollection = collectionLog.getAllDataFromMap().get(uuid);
//
//        int GUI_ROWS = 6;
//        ChestGui gui = new ChestGui(GUI_ROWS, getGUIDesign(pageNumber));
//        gui.setOnGlobalClick(event -> event.setCancelled(true));
//        StaticPane staticPane = new StaticPane(0, 0, GUI_COLUMNS, GUI_ROWS);
//
//        // Add collection items
//        int startIndex = (pageNumber - 1) * itemPerPage;
//        for (int row = 0; row < CONTENT_ROWS; row++) {
//            for (int col = 0; col < GUI_COLUMNS; col++) {
//                int itemID = startIndex + (row * GUI_COLUMNS) + col + 1;
//                if (itemID <= totalCollections) {
//                    Integer amount = playerCollection.getOrDefault(itemID, 0);
//                    ItemStack collectionItem = amount > 0
//                            ? createCollectionItem(itemID, amount)
//                            : createCollectionItemNew(itemID);
//                    staticPane.addItem(new GuiItem(collectionItem), col, row);
//                }
//            }
//        }
//
//        addNavigationButtons(staticPane, pageNumber, player);
//
//        for (int x = 3; x <= 5; x++) {
//            ItemStack closeButton = createCloseButton();
//            staticPane.addItem(new GuiItem(closeButton, event -> event.getWhoClicked().closeInventory()), x, 5);
//        }
//
//        gui.addPane(staticPane);
//        gui.show(player);
//    }
//
//    private void addNavigationButtons(StaticPane pane, int currentPage, Player player) {
//        if (currentPage < totalPages) {
//            ItemStack nextButton = createNextButton();
//            GuiItem nextItem = new GuiItem(nextButton, event -> openCollectionLog(player, currentPage + 1));
//            pane.addItem(nextItem, 6, 5);
//        }
//
//        if (currentPage > 1) {
//            ItemStack previousButton = createPreviousButton();
//            GuiItem prevItem = new GuiItem(previousButton, event -> openCollectionLog(player, currentPage - 1));
//            pane.addItem(prevItem, 2, 5);
//        }
//    }
//
//    private String getGUIDesign(int pageNumber) {
//        if (pageNumber == 1) {
//            return GUIConfig.COLLECTION_TITLE_NEXT;
//        } else if (pageNumber == totalPages) {
//            return GUIConfig.COLLECTION_TITLE_PREV;
//        } else {
//            return GUIConfig.COLLECTION_TITLE;
//        }
//    }
//
//    private ItemStack createCollectionItem(int collectionID, int amount) {
//        ItemStack button = new ItemStack(Material.FLINT);
//        ItemMeta meta = button.getItemMeta();
//        if (meta == null) return button;
//
//        meta.setDisplayName("§6" + getDisplayName(collectionID));
//        meta.setCustomModelData(99999 + collectionID);
//        List<String> lore = new ArrayList<>(collectionsMap.get(collectionID).getLore());
//        lore.add("§7Acquired: §e" + amount);
//        meta.setLore(lore);
//        button.setItemMeta(meta);
//
//        return button;
//    }
//
//    private ItemStack createCollectionItemNew(int collectionID) {
//        ItemStack button = new ItemStack(Material.PAPER);
//        ItemMeta meta = button.getItemMeta();
//        if (meta == null) return button;
//
//        meta.setDisplayName("§6" + getDisplayName(collectionID));
//        meta.setCustomModelData(100020);
//        List<String> lore = new ArrayList<>(collectionsMap.get(collectionID).getLore());
//        meta.setLore(lore);
//        button.setItemMeta(meta);
//        button.setItemMeta(meta);
//
//        return button;
//    }
//
//    private ItemStack createNextButton() {
//        return createButton("§aNext Page", GUIConfig.INVISIBLE_ITEM);
//    }
//
//    private ItemStack createPreviousButton() {
//        return createButton("§aPrevious Page", GUIConfig.INVISIBLE_ITEM);
//    }
//
//    private ItemStack createCloseButton() {
//        return createButton("§cClose", GUIConfig.INVISIBLE_ITEM);
//    }
//
//    private ItemStack createButton(String displayName, int modelData) {
//        ItemStack button = new ItemStack(Material.PAPER);
//        ItemMeta meta = button.getItemMeta();
//        if (meta != null) {
//            meta.setDisplayName(displayName);
//            meta.setCustomModelData(modelData);
//            button.setItemMeta(meta);
//        }
//        return button;
//    }
//
//    private String getDisplayName(int collectionID) {
//        ItemStack item = collectionsMap.get(collectionID);
//        if (item == null) {
//            plugin.getLogger().warning("Item with ID " + collectionID + " not found in collectionCache.");
//            return "Unknown Item";
//        }
//        ItemMeta meta = item.getItemMeta();
//        if (meta == null || !meta.hasDisplayName()) {
//            return "Unknown Item";
//        }
//
//        return meta.getDisplayName();
//    }
