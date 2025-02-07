package asia.virtualmc.vArchaeology;

import asia.virtualmc.vArchaeology.storage.StorageManager;
import asia.virtualmc.vArchaeology.tasks.TaskManager;
import asia.virtualmc.vLibrary.VLibrary;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    public static final String prefix = "[vArchaeology] ";
    private StorageManager storageManager;
    private TaskManager taskManager;
    private VLibrary vlib;

    @Override
    public void onEnable() {
        this.vlib = (VLibrary) Bukkit.getPluginManager().getPlugin("vLibrary");
        if (vlib == null) {
            getLogger().severe("vLibrary not found/loadded! Disabling this plugin..");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.taskManager = new TaskManager(this);
        this.storageManager = new StorageManager(this, vlib, taskManager);
    }

    @Override
    public void onDisable() {
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public VLibrary getVLibrary() {
        return vlib;
    }
}
