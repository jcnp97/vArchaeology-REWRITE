package asia.virtualmc.vArchaeology.events;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.handlers.itemequip.ToolStats;
import asia.virtualmc.vArchaeology.storage.StorageManager;

import asia.virtualmc.vLibrary.interfaces.BlockBreakHandler;
import asia.virtualmc.vLibrary.interfaces.PlayerJoinHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class PlayerJoinEvent implements Listener {
    private final Main plugin;
    private final StorageManager storageManager;
    private final EventManager eventManager;
    private final List<PlayerJoinHandler> handlers;

    public PlayerJoinEvent(@NotNull StorageManager storageManager,
                           @NotNull EventManager eventManager,
                           List<PlayerJoinHandler> handlers) {
        this.storageManager = storageManager;
        this.plugin = storageManager.getMain();
        this.eventManager = eventManager;
        this.handlers = handlers;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        String name = event.getPlayer().getName();
        try {
            storageManager.loadPlayerAllData(uuid, name);
        } catch (Exception e) {
            plugin.getLogger().severe("Error loading player data for " + name + ": " + e.getMessage());
            e.printStackTrace();
        }

        for (PlayerJoinHandler handler : handlers) {
            handler.onPlayerJoinHandler(event);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        String name = event.getPlayer().getName();
        try {
            storageManager.unloadPlayerAllData(uuid, name);
            eventManager.unloadPlayerEventData(uuid);
        } catch (Exception e) {
            plugin.getLogger().severe("Error unloading player data for " + name + ": " + e.getMessage());
            e.printStackTrace();
        }

        for (PlayerJoinHandler handler : handlers) {
            handler.onPlayerQuitHandler(event);
        }
    }
}