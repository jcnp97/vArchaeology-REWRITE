package asia.virtualmc.vArchaeology.storage;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vLibrary.VLibrary;
import asia.virtualmc.vLibrary.storage.PlayerDataLib;
import org.bukkit.Bukkit;

public class StorageManager {
    private final Main plugin;
    private final PlayerData playerData;
    private final VLibrary vlib;
    private final PlayerDataLib playerDataLib;

    public StorageManager(Main plugin) {
        this.plugin = plugin;
        this.vlib = (VLibrary) Bukkit.getPluginManager().getPlugin("vLibrary");
        assert vlib != null;
        this.playerDataLib = vlib.getPlayerDataLib();
        this.playerData = new PlayerData(this);
    }

    public Main getMain() {
        return plugin;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public PlayerDataLib getPlayerDataLib() {
        return playerDataLib;
    }
}
