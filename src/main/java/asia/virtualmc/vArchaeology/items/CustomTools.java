package asia.virtualmc.vArchaeology.items;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.global.GlobalManager;
import asia.virtualmc.vLibrary.items.ToolsLib;
import asia.virtualmc.vLibrary.utils.ConsoleMessageUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomTools {
    private final Main plugin;
    private static NamespacedKey TOOL_KEY;
    private static NamespacedKey GATHER_KEY;
    private static NamespacedKey REQ_LEVEL_KEY;
    private static NamespacedKey ADP_RATE;
    private static final String ITEM_FILE = "items/tools.yml";
    private final Map<String, ItemStack> toolCache;

    static {
    }

    public CustomTools(@NotNull ItemManager itemManager) {
        this.plugin = itemManager.getMain();
        this.toolCache = new HashMap<>();
        TOOL_KEY = new NamespacedKey(plugin, "custom_tool");
        GATHER_KEY = new NamespacedKey(plugin, "gathering_rate");
        REQ_LEVEL_KEY = new NamespacedKey(plugin, "required_level");
        ADP_RATE = new NamespacedKey(plugin, "adp_rate");
        createTools();
    }

    private void createTools() {
        Map<String, ItemStack> loadedItems = ToolsLib.loadToolsFromFile(
                plugin,
                ITEM_FILE,
                GlobalManager.prefix
        );

        toolCache.clear();
        toolCache.putAll(loadedItems);

        ConsoleMessageUtil.printLegacy(GlobalManager.coloredPrefix + "Loaded " +
                toolCache.size() + " items from " + ITEM_FILE);
    }

    public void giveToolID(@NotNull Player player, String toolName) {
        ItemStack item = toolCache.get(toolName);
        if (item == null) {
            player.sendMessage("§cInvalid item ID: " + toolName + " from " + ITEM_FILE);
            return;
        }

        if (ToolsLib.giveTool(player, item)) {
            // add transaction logs here
        } else {
            plugin.getLogger().severe("§There are issues when giving toolID: " + toolName
                    + " with key: " + TOOL_KEY + " to " + player.getName());
        }
    }

    public void takeToolID(@NotNull Player player, int toolID) {
        try {
            if (ToolsLib.takeTool(player, TOOL_KEY, toolID)) {
                // add transaction logs here
            }
        } catch (Exception e) {
            plugin.getLogger().severe("§cError when taking item from " + player.getName() + " :" + e.getMessage());
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
        return new ArrayList<>(toolCache.keySet());
    }

    public Map<String, ItemStack> getToolsCache() {
        return new HashMap<>(toolCache);
    }

    // NamespacedKeys
    public static NamespacedKey getToolKey() {
        return TOOL_KEY;
    }

    public static NamespacedKey getGatherKey() {
        return GATHER_KEY;
    }

    public static NamespacedKey getReqLevelKey() {
        return REQ_LEVEL_KEY;
    }

    public static NamespacedKey getADPRate() {
        return ADP_RATE;
    }
}
