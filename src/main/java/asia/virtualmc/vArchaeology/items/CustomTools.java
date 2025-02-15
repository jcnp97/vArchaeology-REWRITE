package asia.virtualmc.vArchaeology.items;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.global.GlobalManager;
import asia.virtualmc.vLibrary.items.ToolsLib;
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

public class CustomTools {
    private final Main plugin;
    public static final NamespacedKey TOOL_KEY;
    public static final NamespacedKey NAME_KEY;
    public static final NamespacedKey GATHER_KEY;
    public static final NamespacedKey REQ_LEVEL_KEY;
    public static final NamespacedKey ADP_RATE;
    private static final String ITEM_FILE = "items/tools.yml";
    private final Map<Integer, ItemStack> toolCache;
    private final Map<String, Integer> nameToIDCache;

    static {
        TOOL_KEY = new NamespacedKey(Main.getInstance(), "custom_tool");
        NAME_KEY = new NamespacedKey(Main.getInstance(), "tool_name");
        GATHER_KEY = new NamespacedKey(Main.getInstance(), "gathering_rate");
        REQ_LEVEL_KEY = new NamespacedKey(Main.getInstance(), "required_level");
        ADP_RATE = new NamespacedKey(Main.getInstance(), "adp_rate");
    }

    public CustomTools(@NotNull ItemManager itemManager) {
        this.plugin = itemManager.getMain();
        this.toolCache = new HashMap<>();
        this.nameToIDCache = new HashMap<>();
        createTools();
    }

    private void createTools() {
        Map<Integer, ItemStack> loadedItems = ToolsLib.loadToolsFromFile(
                plugin,
                ITEM_FILE,
                GlobalManager.prefix
        );

        toolCache.clear();
        toolCache.putAll(loadedItems);
        populateNameToIDCache();

        ConsoleMessageUtil.printLegacy(GlobalManager.coloredPrefix + "Loaded " +
                toolCache.size() + " items from " + ITEM_FILE);
    }

    public void giveToolID(@NotNull Player player, String toolName) {
        Integer toolID = nameToIDCache.get(toolName);
        ItemStack item = toolCache.get(toolID);
        if (item == null) {
            player.sendMessage("§cInvalid item ID: " + toolID + " from " + ITEM_FILE);
            return;
        }

        if (ToolsLib.giveToolID(player, item)) {
            // add transaction logs here
        } else {
            plugin.getLogger().severe("§There are issues when giving toolID: " + toolID
                    + " with key: " + TOOL_KEY + " to " + player.getName());
        }
    }

    public void takeToolID(@NotNull Player player, int toolID) {
        if (ToolsLib.takeToolID(player, TOOL_KEY, toolID)) {
            String toolName = toolCache.get(toolID).getItemMeta().getDisplayName();
            player.sendMessage("§cYour item: " + toolName + " has been taken from you.");
            // add transaction logs here
        }
    }

    public void reloadConfig() {
        try {
            createTools();
        } catch (Exception e) {
            plugin.getLogger().severe("§There are issues when reloading " + ITEM_FILE + ": "
                    + e.getMessage());
        }
    }

    public List<String> getItemNames() {
        List<String> itemNames = new ArrayList<>();
        for (ItemStack item : toolCache.values()) {
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
        for (Map.Entry<Integer, ItemStack> entry : toolCache.entrySet()) {
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

    public Map<Integer, ItemStack> getToolsCache() {
        return new HashMap<>(toolCache);
    }
}
