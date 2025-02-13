package asia.virtualmc.vArchaeology.items;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.global.GlobalManager;
import asia.virtualmc.vArchaeology.storage.PlayerData;
import asia.virtualmc.vArchaeology.storage.StorageManager;
import asia.virtualmc.vLibrary.enums.EnumsLib;
import asia.virtualmc.vLibrary.items.ItemsLib;
import asia.virtualmc.vLibrary.utils.ConsoleMessageUtil;
import asia.virtualmc.vLibrary.utils.DigitUtils;
import asia.virtualmc.vLibrary.utils.EffectsUtil;
import net.kyori.adventure.sound.Sound;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class CustomBXPStars {
    private final Main plugin;
    private final PlayerData playerData;
    public static NamespacedKey ITEM_KEY;
    private final String ITEM_FILE = "items/stars.yml";
    private static Map<Integer, ItemStack> starCache;

    public CustomBXPStars(@NotNull StorageManager storageManager) {
        this.plugin = storageManager.getMain();
        this.playerData = storageManager.getPlayerData();
        ITEM_KEY = new NamespacedKey(plugin, "varch_star");
        createItems();
    }

    private void createItems() {
        String ITEM_SECTION_PATH = "starsList";
        Map<Integer, ItemStack> loadedItems = ItemsLib.loadItemsFromFile(
                plugin,
                ITEM_FILE,
                ITEM_SECTION_PATH,
                ITEM_KEY,
                GlobalManager.prefix,
                false
        );
        starCache = Map.copyOf(loadedItems);
        ConsoleMessageUtil.printLegacy(GlobalManager.coloredPrefix + "Loaded " +
                starCache.size() + " items from " + ITEM_FILE);
    }

    public static Map<Integer, ItemStack> getItemCache() {
        return starCache;
    }

    public void giveMaterialID(@NotNull Player player, int itemID, int amount) {
        ItemStack item = starCache.get(itemID);
        if (item == null) {
            player.sendMessage("§cInvalid item ID: " + itemID + " from " + ITEM_FILE);
            return;
        }

        if (!ItemsLib.giveItemID(player, item, amount)) {
            plugin.getLogger().severe("§There are issues when giving itemID: " + itemID
                    + " with key: " + ITEM_KEY + " to " + player.getName());
        }
    }

    public void reloadConfig() {
        try {
            starCache.clear();
            createItems();
        } catch (Exception e) {
            plugin.getLogger().severe("§There are issues when reloading " + ITEM_FILE + ": "
                    + e.getMessage());
        }
    }

    // Usage Method
    public void addStarBXP(@NotNull Player player, double exp) {
        String formattedEXP = DigitUtils.formattedNoDecimals(exp);
        playerData.updateBXP(player, EnumsLib.UpdateType.ADD, exp);
        EffectsUtil.sendPlayerMessage(player, "<green>You have received " + formattedEXP + " Archaeology bonus XP!");
        EffectsUtil.playSound(player, "minecraft:entity.player.levelup", Sound.Source.PLAYER, 1.0f, 1.0f);
    }
}
