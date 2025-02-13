package asia.virtualmc.vArchaeology.events;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vLibrary.interfaces.ItemInteractHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemInteractEvent implements Listener {
    private final List<ItemInteractHandler> handlers;

    public ItemInteractEvent(@NotNull EventManager eventManager,
                             List<ItemInteractHandler> handlers) {
        Main plugin = eventManager.getMain();
        this.handlers = handlers;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) {
            return;
        }

        for (ItemInteractHandler handler : handlers) {
            handler.onItemInteractHandler(event);
        }

    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!item.hasItemMeta()) {
            return;
        }

        for (ItemInteractHandler handler : handlers) {
            handler.onBlockPlaceHandler(event);
        }
    }
}
