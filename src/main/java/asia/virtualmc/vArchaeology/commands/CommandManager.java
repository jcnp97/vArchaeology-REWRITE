package asia.virtualmc.vArchaeology.commands;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.items.ItemManager;
import org.jetbrains.annotations.NotNull;

public class CommandManager {
    private final Main plugin;
    private final ItemCommands itemCommands;
    private final ItemManager itemManager;

    public CommandManager(@NotNull Main plugin) {
        this.plugin = plugin;
        this.itemManager = plugin.getItemManager();
        this.itemCommands = new ItemCommands(this);
    }

    public Main getMain() {
        return plugin;
    }

    public ItemManager getItemManager() { return itemManager; }
}
