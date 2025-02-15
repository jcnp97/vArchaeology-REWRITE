package asia.virtualmc.vArchaeology.commands;

import asia.virtualmc.vArchaeology.global.GlobalManager;
import asia.virtualmc.vArchaeology.items.*;
import asia.virtualmc.vLibrary.libs.commandapi.CommandAPICommand;
import asia.virtualmc.vLibrary.libs.commandapi.arguments.*;
import asia.virtualmc.vLibrary.libs.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemCommands {
    private final CustomDrops customDrops;
    private final CustomTools customTools;
    private final CustomEXPLamps customEXPLamps;
    private final CustomBXPStars customBXPStars;
    private final CustomCrafting customCrafting;
    private final CustomCharms customCharms;
    private final CustomUDArtefacts customUDArtefacts;
    private final CustomCollections customCollections;
    private final String sPrefix = GlobalManager.severePrefix;
    private final String cPrefix = GlobalManager.coloredPrefix;

    public ItemCommands(@NotNull ItemManager itemManager) {
        this.customDrops = itemManager.getCustomDrops();
        this.customTools = itemManager.getCustomTools();
        this.customEXPLamps = itemManager.getCustomEXPLamps();
        this.customBXPStars = itemManager.getCustomBXPStars();
        this.customCrafting = itemManager.getCustomCrafting();
        this.customCharms = itemManager.getCustomCharms();
        this.customUDArtefacts = itemManager.getCustomUDArtefacts();
        this.customCollections = itemManager.getCustomCollections();
        registerCommands();
    }

    private void registerCommands() {
        new CommandAPICommand("varch")
                .withSubcommand(getItem())
                .withHelp("[vArchaeology] Main command for vArchaeology", "Access vArchaeology commands")
                .register();
    }

    private CommandAPICommand getItem() {
        List<String> dropNames = customDrops.getItemNames();
        List<String> toolNames = customTools.getItemNames();
        List<String> udArtefactNames = customUDArtefacts.getItemNames();
        List<String> lampNames = customEXPLamps.getItemNames();
        List<String> starNames = customBXPStars.getItemNames();
        List<String> charmNames = customCharms.getItemNames();
        List<String> craftingNames = customCrafting.getItemNames();
        List<String> collectNames = customCollections.getItemNames();

        return new CommandAPICommand("give")
                .withSubcommand(new CommandAPICommand("drops")
                        .withArguments(new StringArgument("item_name").replaceSuggestions(
                                ArgumentSuggestions.strings(dropNames)))
                        .withOptionalArguments(new PlayerArgument("player"))
                        .withPermission("varchaeology.admin.command.get.drops")
                        .executesPlayer(this::giveDropItem)
                        .executes(this::giveDropItem))
                .withSubcommand(new CommandAPICommand("tools")
                        .withArguments(new StringArgument("item_name").replaceSuggestions(
                                ArgumentSuggestions.strings(toolNames)))
                        .withOptionalArguments(new PlayerArgument("player"))
                        .withPermission("varchaeology.admin.command.get.tools")
                        .executesPlayer(this::giveToolItem)
                        .executes(this::giveToolItem))
                .withSubcommand(new CommandAPICommand("lamps")
                        .withArguments(new StringArgument("item_name").replaceSuggestions(
                                ArgumentSuggestions.strings(lampNames)))
                        .withOptionalArguments(new PlayerArgument("player"), new IntegerArgument("value", 1))
                        .withPermission("varchaeology.admin.command.get.lamps")
                        .executesPlayer(this::giveLampItem)
                        .executes(this::giveLampItem))
                .withSubcommand(new CommandAPICommand("stars")
                        .withArguments(new StringArgument("item_name").replaceSuggestions(
                                ArgumentSuggestions.strings(starNames)))
                        .withOptionalArguments(new PlayerArgument("player"), new IntegerArgument("value", 1))
                        .withPermission("varchaeology.admin.command.get.stars")
                        .executesPlayer(this::giveStarItem)
                        .executes(this::giveStarItem))
                .withSubcommand(new CommandAPICommand("charms")
                        .withArguments(new StringArgument("item_name").replaceSuggestions(
                                ArgumentSuggestions.strings(charmNames)))
                        .withOptionalArguments(new PlayerArgument("player"), new IntegerArgument("value", 1))
                        .withPermission("varchaeology.admin.command.get.charms")
                        .executesPlayer(this::giveCharmItem)
                        .executes(this::giveCharmItem))
                .withSubcommand(new CommandAPICommand("crafting")
                        .withArguments(new StringArgument("item_name").replaceSuggestions(
                                ArgumentSuggestions.strings(craftingNames)))
                        .withOptionalArguments(new PlayerArgument("player"), new IntegerArgument("value", 1))
                        .withPermission("varchaeology.admin.command.get.crafting")
                        .executesPlayer(this::giveCraftingItem)
                        .executes(this::giveCraftingItem))
                .withSubcommand(new CommandAPICommand("ud_artefacts")
                        .withArguments(new StringArgument("item_name").replaceSuggestions(
                                ArgumentSuggestions.strings(udArtefactNames)))
                        .withOptionalArguments(new PlayerArgument("player"), new IntegerArgument("value", 1))
                        .withPermission("varchaeology.admin.command.get.ud_artefacts")
                        .executesPlayer(this::giveUDArtefactItem)
                        .executes(this::giveUDArtefactItem))
                .withSubcommand(new CommandAPICommand("collections")
                        .withArguments(new StringArgument("item_name").replaceSuggestions(
                                ArgumentSuggestions.strings(collectNames)))
                        .withOptionalArguments(new PlayerArgument("player"), new IntegerArgument("value", 1))
                        .withPermission("varchaeology.admin.command.get.collections")
                        .executesPlayer(this::giveCollectionItem)
                        .executes(this::giveCollectionItem))
                ;
    }

    private void giveDropItem(CommandSender sender, CommandArguments args) {
        Player target = (Player) args.getOptional("player").map(obj -> (Player) obj)
                .orElse(sender instanceof Player ? (Player) sender : null);
        int amount = args.getOptional("value").map(obj -> (int) obj).orElse(1);

        if (target == null) {
            sender.sendMessage(sPrefix + "You must specify a player when using this command from the console.");
            return;
        }

        String itemName = (String) args.get("item_name");
        sender.sendMessage(cPrefix + "Attempting to give " + amount + " of " + itemName + " (drop) to " + target.getName());
        assert itemName != null;
        customDrops.giveItem(target, itemName, amount);
    }

    private void giveToolItem(CommandSender sender, CommandArguments args) {
        Player target = (Player) args.getOptional("player").map(obj -> (Player) obj)
                .orElse(sender instanceof Player ? (Player) sender : null);

        if (target == null) {
            sender.sendMessage(sPrefix + "You must specify a player when using this command from the console.");
            return;
        }

        String itemName = (String) args.get("item_name");
        sender.sendMessage(cPrefix + "Attempting to give 1 of " + itemName + " (tool) to " + target.getName());
        customTools.giveToolID(target, itemName);
    }

    private void giveLampItem(CommandSender sender, CommandArguments args) {
        Player target = (Player) args.getOptional("player").map(obj -> (Player) obj)
                .orElse(sender instanceof Player ? (Player) sender : null);
        int amount = args.getOptional("value").map(obj -> (int) obj).orElse(1);

        if (target == null) {
            sender.sendMessage(sPrefix + "You must specify a player when using this command from the console.");
            return;
        }

        String itemName = (String) args.get("item_name");
        sender.sendMessage(cPrefix + "Attempting to give " + amount + " of " + itemName + " (lamp) to " + target.getName());
        customEXPLamps.giveItem(target, itemName, amount);
    }

    private void giveStarItem(CommandSender sender, CommandArguments args) {
        Player target = (Player) args.getOptional("player").map(obj -> (Player) obj)
                .orElse(sender instanceof Player ? (Player) sender : null);
        int amount = args.getOptional("value").map(obj -> (int) obj).orElse(1);

        if (target == null) {
            sender.sendMessage(sPrefix + "You must specify a player when using this command from the console.");
            return;
        }

        String itemName = (String) args.get("item_name");
        sender.sendMessage(cPrefix + "Attempting to give " + amount + " of " + itemName + " (star) to " + target.getName());
        customBXPStars.giveItem(target, itemName, amount);
    }

    private void giveCharmItem(CommandSender sender, CommandArguments args) {
        Player target = (Player) args.getOptional("player").map(obj -> (Player) obj)
                .orElse(sender instanceof Player ? (Player) sender : null);
        int amount = args.getOptional("value").map(obj -> (int) obj).orElse(1);

        if (target == null) {
            sender.sendMessage(sPrefix + "You must specify a player when using this command from the console.");
            return;
        }

        String itemName = (String) args.get("item_name");
        sender.sendMessage(cPrefix + "Attempting to give " + amount + " of " + itemName + " (charm) to " + target.getName());
        assert itemName != null;
        customCharms.giveItem(target, itemName, amount);
    }

    private void giveUDArtefactItem(CommandSender sender, CommandArguments args) {
        Player target = (Player) args.getOptional("player").map(obj -> (Player) obj)
                .orElse(sender instanceof Player ? (Player) sender : null);
        int amount = args.getOptional("value").map(obj -> (int) obj).orElse(1);

        if (target == null) {
            sender.sendMessage(sPrefix + "You must specify a player when using this command from the console.");
            return;
        }

        String itemName = (String) args.get("item_name");
        sender.sendMessage(cPrefix + "Attempting to give " + amount + " of " + itemName + " (charm) to " + target.getName());
        customUDArtefacts.giveItem(target, itemName, amount);
    }

    private void giveCraftingItem(CommandSender sender, CommandArguments args) {
        Player target = (Player) args.getOptional("player").map(obj -> (Player) obj)
                .orElse(sender instanceof Player ? (Player) sender : null);
        int amount = args.getOptional("value").map(obj -> (int) obj).orElse(1);

        if (target == null) {
            sender.sendMessage(sPrefix + "You must specify a player when using this command from the console.");
            return;
        }

        String itemName = (String) args.get("item_name");
        sender.sendMessage(cPrefix + "Attempting to give " + amount + " of " + itemName + " (charm) to " + target.getName());
        customCrafting.giveItem(target, itemName, amount);
    }

    private void giveCollectionItem(CommandSender sender, CommandArguments args) {
        Player target = (Player) args.getOptional("player").map(obj -> (Player) obj)
                .orElse(sender instanceof Player ? (Player) sender : null);
        int amount = args.getOptional("value").map(obj -> (int) obj).orElse(1);

        if (target == null) {
            sender.sendMessage(sPrefix + "You must specify a player when using this command from the console.");
            return;
        }

        String itemName = (String) args.get("item_name");
        sender.sendMessage(cPrefix + "Attempting to give " + amount + " of " + itemName + " (charm) to " + target.getName());
        assert itemName != null;
        customCollections.giveItem(target, itemName, amount);
    }
}