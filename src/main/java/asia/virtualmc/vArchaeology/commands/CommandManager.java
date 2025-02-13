package asia.virtualmc.vArchaeology.commands;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.items.ItemManager;
import org.jetbrains.annotations.NotNull;

public class CommandManager {
    private final Main plugin;
    private final ItemCommands itemCommands;
    private final DataCommands dataCommands;
    private final GUICommands guiCommands;
    private final ItemManager itemManager;

    public CommandManager(@NotNull Main plugin) {
        this.plugin = plugin;
        this.itemManager = plugin.getItemManager();
        this.itemCommands = new ItemCommands(itemManager);
        this.dataCommands = new DataCommands(plugin.getStorageManager());
        this.guiCommands = new GUICommands(plugin.getGuiManager());
    }

    public Main getMain() {
        return plugin;
    }

    public ItemManager getItemManager() { return itemManager; }
}
