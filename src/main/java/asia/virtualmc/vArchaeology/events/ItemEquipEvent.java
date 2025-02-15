package asia.virtualmc.vArchaeology.events;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.items.CustomTools;
import asia.virtualmc.vLibrary.interfaces.ItemEquipHandler;
import asia.virtualmc.vLibrary.items.ToolsLib;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemEquipEvent implements Listener {
    private final Main plugin;
    private final List<ItemEquipHandler> handlers;

    public ItemEquipEvent(@NotNull EventManager eventManager,
                          List<ItemEquipHandler> handlers) {
        this.plugin = eventManager.getMain();
        this.handlers = handlers;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        for (ItemEquipHandler handler : handlers) {
            handler.onItemEquipHandler(event);
        }
    }
}