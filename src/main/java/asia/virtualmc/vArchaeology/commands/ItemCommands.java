package asia.virtualmc.vArchaeology.commands;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.global.GlobalManager;
import asia.virtualmc.vArchaeology.items.CustomBXPStars;
import asia.virtualmc.vArchaeology.items.CustomEXPLamps;
import asia.virtualmc.vArchaeology.items.CustomMaterials;
import asia.virtualmc.vArchaeology.items.CustomTools;
import asia.virtualmc.vLibrary.libs.commandapi.CommandAPICommand;
import asia.virtualmc.vLibrary.libs.commandapi.arguments.*;

import asia.virtualmc.vLibrary.libs.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ItemCommands {
    private final Main plugin;
    private final CustomMaterials customMaterials;
    private final CustomTools customTools;
    private final CustomEXPLamps customEXPLamps;
    private final CustomBXPStars customBXPStars;
    private final String sPrefix = GlobalManager.severePrefix;
    private final String cPrefix = GlobalManager.coloredPrefix;

    public ItemCommands(@NotNull CommandManager commandManager) {
        this.plugin = commandManager.getMain();
        this.customMaterials = commandManager.getItemManager().getCustomMaterials();
        this.customTools = commandManager.getItemManager().getCustomTools();
        this.customEXPLamps = commandManager.getItemManager().getCustomEXPLamps();
        this.customBXPStars = commandManager.getItemManager().getCustomBXPStars();
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
                        .executesPlayer((player, args) -> {
                            giveDropItem(player, args);
                        })
                        .executes((sender, args) -> {
                            giveDropItem(sender, args);
                        }))
                .withSubcommand(new CommandAPICommand("tools")
                        .withArguments(new MultiLiteralArgument("item_name",
                                "copper_mattock", "flint_mattock", "prismarine_mattock", "carbon_steel_mattock",
                                "netherium_mattock", "amethyst_mattock", "gold_alloy_mattock", "titanium_mattock",
                                "dark_echo_mattock", "mattock_of_time_and_space", "admin"
                        ))
                        .withOptionalArguments(new PlayerArgument("player"))
                        .withPermission("varchaeology.admin.command.get.tools")
                        .executesPlayer((player, args) -> {
                            giveToolItem(player, args);
                        })
                        .executes((sender, args) -> {
                            giveToolItem(sender, args);
                        }))
                .withSubcommand(new CommandAPICommand("lamps")
                        .withArguments(new MultiLiteralArgument("item_name",
                                "small", "medium", "large", "huge"))
                        .withOptionalArguments(new PlayerArgument("player"), new IntegerArgument("value", 1))
                        .withPermission("varchaeology.admin.command.get.lamps")
                        .executesPlayer((player, args) -> {
                            giveLampItem(player, args);
                        })
                        .executes((sender, args) -> {
                            giveLampItem(sender, args);
                        }))
                .withSubcommand(new CommandAPICommand("stars")
                        .withArguments(new MultiLiteralArgument("item_name",
                                "small", "medium", "large", "huge"))
                        .withOptionalArguments(new PlayerArgument("player"), new IntegerArgument("value", 1))
                        .withPermission("varchaeology.admin.command.get.stars")
                        .executesPlayer((player, args) -> {
                            giveStarItem(player, args);
                        })
                        .executes((sender, args) -> {
                            giveStarItem(sender, args);
                        }))
//                .withSubcommand(new CommandAPICommand("charms")
//                        .withArguments(new MultiLiteralArgument("item_name",
//                                "common", "uncommon", "rare", "unique",
//                                        "special", "mythical", "exotic"))
//                        .withOptionalArguments(new PlayerArgument("player"), new IntegerArgument("value", 1))
//                        .withPermission("varchaeology.admin.command.get.charms")
//                        .executesPlayer((player, args) -> {
//                            giveCharmItem(player, args);
//                        })
//                        .executes((sender, args) -> {
//                            giveCharmItem(sender, args);
//                        }))
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
        int itemID = getDropIDFromName(itemName);
        customMaterials.giveMaterialID(target, itemID, amount);
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
            case "admin" -> 11;
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
        sender.sendMessage(cPrefix + "Attempting to give " + amount + " of " + itemName + " (drop) to " + target.getName());
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
        sender.sendMessage(cPrefix + "Attempting to give " + amount + " of " + itemName + " (drop) to " + target.getName());
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
}