package asia.virtualmc.vArchaeology.items;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.global.GlobalManager;
import asia.virtualmc.vLibrary.items.ToolsLib;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;
import java.util.logging.Level;

public class CustomTools {
    private final Main plugin;
    public static NamespacedKey TOOL_KEY;
    public static NamespacedKey GATHER_KEY;
    public static NamespacedKey REQ_LEVEL_KEY;
    public static NamespacedKey ADP_RATE;
    private static Map<Integer, ItemStack> toolCache;
    private final String ITEM_FILE = "items/tools.yml";

    public CustomTools(@NotNull ItemManager itemManager) {
        this.plugin = itemManager.getMain();
        TOOL_KEY = new NamespacedKey(plugin, "custom_tool");
        GATHER_KEY = new NamespacedKey(plugin, "gathering_rate");
        REQ_LEVEL_KEY = new NamespacedKey(plugin, "required_level");
        ADP_RATE = new NamespacedKey(plugin, "adp_rate");
        createTools();
    }

    private void createTools() {
        String ITEM_SECTION_PATH = "toolsList";
        Map<Integer, ItemStack> loadedItems = ToolsLib.loadToolsFromFile(
                plugin,
                ITEM_FILE,
                ITEM_SECTION_PATH,
                GlobalManager.prefix
        );
        toolCache = Map.copyOf(loadedItems);
    }

    public static Map<Integer, ItemStack> getItemCache() {
        return toolCache;
    }

    public void giveToolID(@NotNull Player player, int toolID) {
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
            toolCache.clear();
            createTools();
        } catch (Exception e) {
            plugin.getLogger().severe("§There are issues when reloading " + ITEM_FILE + ": "
                    + e.getMessage());
        }
    }
}
