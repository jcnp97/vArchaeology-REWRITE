package asia.virtualmc.vArchaeology.commands;

import asia.virtualmc.vArchaeology.global.GlobalManager;
import asia.virtualmc.vArchaeology.items.*;
import asia.virtualmc.vLibrary.libs.commandapi.CommandAPICommand;
import asia.virtualmc.vLibrary.libs.commandapi.arguments.*;

import asia.virtualmc.vLibrary.libs.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ItemCommands {
    private final CustomDrops customDrops;
    private final CustomTools customTools;
    private final CustomEXPLamps customEXPLamps;
    private final CustomBXPStars customBXPStars;
    private final CustomCrafting customCrafting;
    private final CustomCharms customCharms;
    private final CustomUDArtefacts customUDArtefacts;
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
        registerCommands();
    }

    private void registerCommands() {
        new CommandAPICommand("varch")
                .withSubcommand(getItem())
                .withHelp("[vArchaeology] Main command for vArchaeology", "Access vArchaeology commands")
                .register();
    }

    private CommandAPICommand getItem() {
        return new CommandAPICommand("get")
                .withSubcommand(new CommandAPICommand("drops")
                        .withArguments(new MultiLiteralArgument("item_name",
                                "common", "uncommon", "rare", "unique",
                                "special", "mythical", "exotic"
                        ))
                        .withOptionalArguments(new PlayerArgument("player"), new IntegerArgument("value", 1))
                        .withPermission("varchaeology.admin.command.get.drops")
                        .executesPlayer(this::giveDropItem)
                        .executes(this::giveDropItem))
                .withSubcommand(new CommandAPICommand("tools")
                        .withArguments(new MultiLiteralArgument("item_name",
                                "copper_mattock", "flint_mattock", "prismarine_mattock", "carbon_steel_mattock",
                                "netherium_mattock", "amethyst_mattock", "gold_alloy_mattock", "titanium_mattock",
                                "dark_echo_mattock", "mattock_of_time_and_space", "mattock_of_time_and_space_e", "admin"
                        ))
                        .withOptionalArguments(new PlayerArgument("player"))
                        .withPermission("varchaeology.admin.command.get.tools")
                        .executesPlayer(this::giveToolItem)
                        .executes(this::giveToolItem))
                .withSubcommand(new CommandAPICommand("lamps")
                        .withArguments(new MultiLiteralArgument("item_name",
                                "small", "medium", "large", "huge"))
                        .withOptionalArguments(new PlayerArgument("player"), new IntegerArgument("value", 1))
                        .withPermission("varchaeology.admin.command.get.lamps")
                        .executesPlayer(this::giveLampItem)
                        .executes(this::giveLampItem))
                .withSubcommand(new CommandAPICommand("stars")
                        .withArguments(new MultiLiteralArgument("item_name",
                                "small", "medium", "large", "huge"))
                        .withOptionalArguments(new PlayerArgument("player"), new IntegerArgument("value", 1))
                        .withPermission("varchaeology.admin.command.get.stars")
                        .executesPlayer(this::giveStarItem)
                        .executes(this::giveStarItem))
                .withSubcommand(new CommandAPICommand("charms")
                        .withArguments(new MultiLiteralArgument("item_name",
                                "common", "uncommon", "rare", "unique",
                                        "special", "mythical", "exotic"))
                        .withOptionalArguments(new PlayerArgument("player"), new IntegerArgument("value", 1))
                        .withPermission("varchaeology.admin.command.get.charms")
                        .executesPlayer(this::giveCharmItem)
                        .executes(this::giveCharmItem))
//                .withSubcommand(new CommandAPICommand("crafting")
//                        .withArguments(new MultiLiteralArgument("item_name",
//                                "small", "medium", "large", "huge"))
//                        .withOptionalArguments(new PlayerArgument("player"), new IntegerArgument("value", 1))
//                        .withPermission("varchaeology.admin.command.get.crafting")
//                        .executesPlayer((player, args) -> {
//                            giveCraftingItem(player, args);
//                        })
//                        .executes((sender, args) -> {
//                            giveCraftingItem(sender, args);
//                        }))
//                .withSubcommand(new CommandAPICommand("collections")
//                        .withArguments(new IntegerArgument("item_id", 1))
//                        .withOptionalArguments(new PlayerArgument("player"), new IntegerArgument("value", 1))
//                        .withPermission("varchaeology.admin.command.get.collections")
//                        .executesPlayer((player, args) -> {
//                            giveCollectionItem(player, args);
//                        })
//                        .executes((sender, args) -> {
//                            giveCollectionItem(sender, args);
//                        }))
                .withSubcommand(new CommandAPICommand("ud_artefacts")
                        .withArguments(new MultiLiteralArgument("item_name",
                                "small", "medium", "large", "huge"))
                        .withOptionalArguments(new PlayerArgument("player"), new IntegerArgument("value", 1))
                        .withPermission("varchaeology.admin.command.get.ud_artefacts")
                        .executesPlayer(this::giveUDArtefactItem)
                        .executes(this::giveUDArtefactItem))
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
        int itemID = getDropIDFromName(itemName);
        customDrops.giveMaterialID(target, itemID, amount);
    }

    private int getDropIDFromName(String name) {
        return switch (name.toLowerCase()) {
            case "common" -> 1;
            case "uncommon" -> 2;
            case "rare" -> 3;
            case "unique" -> 4;
            case "special" -> 5;
            case "mythical" -> 6;
            case "exotic" -> 7;
            default -> throw new IllegalArgumentException("Unknown item: " + name);
        };
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
        assert itemName != null;
        int itemID = getToolIDFromName(itemName);
        customTools.giveToolID(target, itemID);
    }

    private int getToolIDFromName(String name) {
        return switch (name.toLowerCase()) {
            case "copper_mattock" -> 1;
            case "flint_mattock" -> 2;
            case "prismarine_mattock" -> 3;
            case "carbon_steel_mattock" -> 4;
            case "netherium_mattock" -> 5;
            case "amethyst_mattock" -> 6;
            case "gold_alloy_mattock" -> 7;
            case "titanium_mattock" -> 8;
            case "dark_echo_mattock" -> 9;
            case "mattock_of_time_and_space" -> 10;
            case "mattock_of_time_and_space_e" -> 11;
            case "admin" -> 12;
            default -> throw new IllegalArgumentException("Unknown item: " + name);
        };
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
        assert itemName != null;
        int itemID = getStarLampIDFromName(itemName);
        customEXPLamps.giveMaterialID(target, itemID, amount);
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
        assert itemName != null;
        int itemID = getStarLampIDFromName(itemName);
        customBXPStars.giveMaterialID(target, itemID, amount);
    }

    private int getStarLampIDFromName(String name) {
        return switch (name.toLowerCase()) {
            case "small" -> 1;
            case "medium" -> 2;
            case "large" -> 3;
            case "huge" -> 4;
            default -> throw new IllegalArgumentException("Unknown item: " + name);
        };
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
        int itemID = getDropIDFromName(itemName);
        customDrops.giveMaterialID(target, itemID, amount);
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
        assert itemName != null;
        int itemID = getDropIDFromName(itemName);
        customDrops.giveMaterialID(target, itemID, amount);
    }
}