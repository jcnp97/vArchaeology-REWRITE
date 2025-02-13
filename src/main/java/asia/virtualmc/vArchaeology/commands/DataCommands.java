package asia.virtualmc.vArchaeology.commands;

import asia.virtualmc.vArchaeology.global.GlobalManager;
import asia.virtualmc.vArchaeology.storage.*;
import asia.virtualmc.vLibrary.enums.EnumsLib;
import asia.virtualmc.vLibrary.libs.commandapi.CommandAPICommand;
import asia.virtualmc.vLibrary.libs.commandapi.arguments.IntegerArgument;
import asia.virtualmc.vLibrary.libs.commandapi.arguments.MultiLiteralArgument;
import asia.virtualmc.vLibrary.libs.commandapi.arguments.PlayerArgument;
import asia.virtualmc.vLibrary.libs.commandapi.executors.CommandArguments;
import asia.virtualmc.vLibrary.libs.commandapi.executors.CommandExecutor;
import asia.virtualmc.vLibrary.libs.commandapi.executors.PlayerCommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DataCommands {
    private final StorageManager storageManager;
    private final PlayerData playerData;
    private final String sPrefix = GlobalManager.severePrefix;
    private final String cPrefix = GlobalManager.coloredPrefix;

    public DataCommands(@NotNull StorageManager storageManager) {
        this.storageManager = storageManager;
        this.playerData = storageManager.getPlayerData();
        registerCommands();
    }

    private void registerCommands() {
        new CommandAPICommand("varch")
                .withSubcommand(editData())
                .withHelp("[vArchaeology] Main command for vArchaeology", "Access vArchaeology commands")
                .register();
    }

    private CommandAPICommand editData() {
        return new CommandAPICommand("edit")
                .withSubcommand(createEditSubcommand("exp", "varchaeology.admin.command.edit.exp"))
                .withSubcommand(createEditSubcommand("level", "varchaeology.admin.command.edit.level"))
                .withSubcommand(createEditSubcommand("xp_multiplier", "varchaeology.admin.command.edit.xp_multiplier"))
                .withSubcommand(createEditSubcommand("bonus_xp", "varchaeology.admin.command.edit.bonus_xp"))
                .withSubcommand(createEditSubcommand("luck_factor", "varchaeology.admin.command.edit.luck_factor"))
                .withSubcommand(createEditSubcommand("trait_points", "varchaeology.admin.command.edit.trait_points"))
                .withSubcommand(createEditSubcommand("talent_points", "varchaeology.admin.command.edit.talent_points"));
    }

    private CommandAPICommand createEditSubcommand(String type, String permission) {
        return new CommandAPICommand(type)
                .withArguments(new MultiLiteralArgument("operator", "add", "subtract", "set"))
                .withOptionalArguments(new PlayerArgument("player"), new IntegerArgument("value", 1))
                .withPermission(permission)
                .executesPlayer((PlayerCommandExecutor) (player, args) -> editPlayerData(player, args, type))
                .executes((CommandExecutor) (sender, args) -> editPlayerData(sender, args, type));
    }

    private void editPlayerData(CommandSender sender, CommandArguments args, String commandType) {
        Player target = (Player) args.getOptional("player").map(obj -> (Player) obj)
                .orElse(sender instanceof Player ? (Player) sender : null);
        int amount = args.getOptional("value").map(obj -> (int) obj).orElse(1);

        if (target == null) {
            sender.sendMessage(sPrefix + "You must specify a player when using this command from the console.");
            return;
        }

        String operatorType = (String) args.get("operator");
        assert operatorType != null;
        EnumsLib.UpdateType updateType = getOperator(operatorType);

        switch (commandType) {
            case "exp" -> {
                sender.sendMessage(cPrefix + "Modifying " + target.getName() + "'s EXP by " + amount);
                playerData.updateEXP(target, updateType, amount);
            }
            case "level" -> {
                sender.sendMessage(cPrefix + "Modifying " + target.getName() + "'s Level by " + amount);
                playerData.updateLevel(target, updateType, amount);
            }
            case "xp_multiplier" -> {
                sender.sendMessage(cPrefix + "Modifying " + target.getName() + "'s XP Multiplier by " + amount);
                playerData.updateXPM(target, updateType, amount);
            }
            case "bonus_xp" -> {
                sender.sendMessage(cPrefix + "Modifying " + target.getName() + "'s Bonus XP by " + amount);
                playerData.updateBXP(target, updateType, amount);
            }
            case "luck_factor" -> {
                sender.sendMessage(cPrefix + "Modifying " + target.getName() + "'s Luck Factor by " + amount);
                playerData.updateLuck(target, updateType, amount);
            }
            case "trait_points" -> {
                sender.sendMessage(cPrefix + "Modifying " + target.getName() + "'s Trait Points by " + amount);
                playerData.updateTraitPoints(target, updateType, amount);
            }
            case "talent_points" -> {
                sender.sendMessage(cPrefix + "Modifying " + target.getName() + "'s Talent Points by " + amount);
                playerData.updateTalentPoints(target, updateType, amount);
            }
        }
    }

    private EnumsLib.UpdateType getOperator(String name) {
        return switch (name.toLowerCase()) {
            case "add" -> EnumsLib.UpdateType.ADD;
            case "subtract" -> EnumsLib.UpdateType.SUBTRACT;
            default -> EnumsLib.UpdateType.SET;
        };
    }
}
