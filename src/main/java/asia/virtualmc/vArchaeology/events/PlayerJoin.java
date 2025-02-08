package asia.virtualmc.vArchaeology.events;

import asia.virtualmc.vArchaeology.Main;
//import asia.virtualmc.vArchaeology.droptables.ItemsDropTable;
//import asia.virtualmc.vArchaeology.guis.RankGUI;
//import asia.virtualmc.vArchaeology.guis.SellGUI;
import asia.virtualmc.vArchaeology.storage.*;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerJoin implements Listener {
    private final Main plugin;
    private final StorageManager storageManager;

//    private final TalentTree talentTree;
//    private final CollectionLog collectionLog;
//    private final ItemEquipListener itemEquipListener;
//    private final ItemsDropTable itemsDropTable;
//    private final BlockBreakListener blockBreakListener;
//    private final SellGUI sellGUI;
//    private final RankGUI rankGUI;

    public PlayerJoin(@NotNull EventManager eventManager) {
        this.plugin = eventManager.getMain();
        this.storageManager = eventManager.getStorageManager();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
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