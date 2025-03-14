package asia.virtualmc.vArchaeology.commands;

import asia.virtualmc.vArchaeology.global.GlobalManager;
import asia.virtualmc.vArchaeology.guis.CollectionLogGUI;
import asia.virtualmc.vArchaeology.guis.GUIManager;
import asia.virtualmc.vArchaeology.guis.SellGUI;
import asia.virtualmc.vLibrary.libs.commandapi.CommandAPICommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GUICommands {
    private final GUIManager guiManager;
    private final CollectionLogGUI collectionLogGUI;
    private final SellGUI sellGUI;
    private final String sPrefix = GlobalManager.severePrefix;
    private final String cPrefix = GlobalManager.coloredPrefix;

    public GUICommands(@NotNull GUIManager guiManager) {
        this.guiManager = guiManager;
        this.collectionLogGUI = guiManager.getCollectionLogGUI();
        this.sellGUI = guiManager.getSellGUI();
        registerCommands();
    }

    private void registerCommands() {
        new CommandAPICommand("varch")
                .withSubcommand(collectionLog())
                .withSubcommand(sellGUI())
                .withHelp("[vArchaeology] Main command for vArchaeology", "Access vArchaeology commands")
                .register();
    }

    private CommandAPICommand collectionLog() {
        return new CommandAPICommand("collection_log")
                .withPermission("varchaeology.use")
                .executes((sender, args) -> {
                    if (sender instanceof Player player) {
                        collectionLogGUI.openCollectionLog(player, 1);
                    } else {
                        sender.sendMessage("This command can only be used by players.");
                    }
                });
    }

    private CommandAPICommand sellGUI() {
        return new CommandAPICommand("sell")
                .withPermission("varchaeology.use")
                .executes((sender, args) -> {
                    if (sender instanceof Player player) {
                        sellGUI.openSellGUI(player);
                    } else {
                        sender.sendMessage("This command can only be used by players.");
                    }
                });
    }
}
