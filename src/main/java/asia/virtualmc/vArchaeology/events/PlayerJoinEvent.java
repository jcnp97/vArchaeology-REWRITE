package asia.virtualmc.vArchaeology.events;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.storage.StorageManager;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerJoinEvent implements Listener {
    private final Main plugin;
    private final StorageManager storageManager;

    public PlayerJoinEvent(@NotNull EventManager eventManager) {
        this.plugin = eventManager.getMain();
        this.storageManager = eventManager.getStorageManager();
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
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        String name = event.getPlayer().getName();
        try {
            storageManager.unloadPlayerAllData(uuid, name);
        } catch (Exception e) {
            plugin.getLogger().severe("Error loading player data for " + name + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}