package asia.virtualmc.vArchaeology;

import asia.virtualmc.vArchaeology.commands.CommandManager;
import asia.virtualmc.vArchaeology.core.CoreManager;
import asia.virtualmc.vArchaeology.events.EventManager;
import asia.virtualmc.vArchaeology.exp.EXPManager;
import asia.virtualmc.vArchaeology.global.GlobalManager;
import asia.virtualmc.vArchaeology.items.ItemManager;
import asia.virtualmc.vArchaeology.storage.StorageManager;
import asia.virtualmc.vArchaeology.tasks.TaskManager;
import asia.virtualmc.vLibrary.VLibrary;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private StorageManager storageManager;
    private TaskManager taskManager;
    private EventManager eventManager;
    private GlobalManager globalManager;
    private ItemManager itemManager;
    private CoreManager coreManager;
    private CommandManager commandManager;
    private EXPManager expManager;
    // VLibrary (Core)
    private VLibrary vlib;

    @Override
    public void onEnable() {
        this.vlib = (VLibrary) Bukkit.getPluginManager().getPlugin("vLibrary");
        if (vlib == null) {
            getLogger().severe(GlobalManager.prefix + "vLibrary not found/loaded! Disabling this plugin..");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.globalManager = new GlobalManager(this);
        this.taskManager = new TaskManager(this);
        this.storageManager = new StorageManager(this);
        this.coreManager = new CoreManager(this);
        this.itemManager = new ItemManager(this);
        this.expManager = new EXPManager(this);
        this.eventManager = new EventManager(this);
        this.commandManager = new CommandManager(this);
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onDisable() {
        storageManager.saveAllPluginData();
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

    public GlobalManager getGlobalManager() { return globalManager; }

    public EventManager getEventManager() { return eventManager; }

    public ItemManager getItemManager() { return itemManager; }

    public CoreManager getCoreManager() { return coreManager; }

    public EXPManager getExpManager() { return  expManager; }
}
