package asia.virtualmc.vArchaeology.handlers.itemequip;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.global.TalentTreeValues;
import asia.virtualmc.vArchaeology.items.CustomTools;
import asia.virtualmc.vArchaeology.storage.PlayerData;
import asia.virtualmc.vArchaeology.storage.StorageManager;
import asia.virtualmc.vArchaeology.storage.TalentTree;
import asia.virtualmc.vLibrary.interfaces.ItemEquipHandler;
import asia.virtualmc.vLibrary.items.ToolsLib;
import asia.virtualmc.vLibrary.utils.EffectsUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ToolStats implements ItemEquipHandler {
    private final Main plugin;
    private final TalentTree talentTree;
    private final PlayerData playerData;
    public static Map<UUID, ToolData> toolDataMap;
    public record ToolData(double gather, double adb) {}

    public ToolStats(@NotNull StorageManager storageManager) {
        this.plugin = storageManager.getMain();
        this.talentTree = storageManager.getTalentTree();
        this.playerData = storageManager.getPlayerData();
        toolDataMap = new ConcurrentHashMap<>();
    }

    @Override
    public void onItemEquipHandler(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) return;
                loadToolData(player);

            }
        }.runTaskLater(plugin, 5L);
    }

    private boolean isArchTool(ItemStack item, NamespacedKey TOOL_KEY) {
        return item != null
                && item.getType() != Material.AIR
                && ToolsLib.isCustomTool(item, TOOL_KEY);
    }

    public void loadToolData(@NotNull Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();

        if (!isArchTool(item, CustomTools.TOOL_KEY)) {
            return;
        }

        UUID uuid = player.getUniqueId();
        unloadToolData(uuid);

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        double gatherRate = calculateGatherRate(uuid, pdc);
        double adbProgress = calculateADB(uuid, pdc);

        toolDataMap.put(uuid, new ToolData(gatherRate, adbProgress));
        EffectsUtil.sendActionBarMessage(player, "<gray>Gathering Rate: <yellow>%" +
                gatherRate + " | <gray>Discovery Rate: <yellow>%" + adbProgress);
    }

    private double calculateGatherRate(@NotNull UUID uuid, PersistentDataContainer pdc) {
        int karmaLevel = playerData.getKarmaTrait(uuid);
        // Tool Gather Rate
        double gatherRate = pdc.getOrDefault(CustomTools.GATHER_KEY, PersistentDataType.DOUBLE, 0.0);
        // Talent ID 3
        gatherRate += talentTree.getDataFromMap(uuid, 3) * 0.1;
        // Karma Trait
        gatherRate += karmaLevel * TalentTreeValues.karmaEffects[0];
        // Karma Trait (Max level bonus)
        if (karmaLevel >= 50) gatherRate += TalentTreeValues.karmaEffects[3];

        return gatherRate;
    }

    private double calculateADB(@NotNull UUID uuid, PersistentDataContainer pdc) {
        int dexterityLevel = playerData.getDexterityTrait(uuid);
        // Tool ADB
        double adpRate = pdc.getOrDefault(CustomTools.ADP_RATE, PersistentDataType.DOUBLE, 0.0);
        // Talent ID 8
        adpRate += talentTree.getDataFromMap(uuid, 8) * 0.01;
        // Talent ID 17
        adpRate += talentTree.getDataFromMap(uuid, 17) * 0.15;
        // Dexterity Trait
        adpRate += dexterityLevel * TalentTreeValues.dexterityEffects[0];
        // Dexterity Trait (Max level bonus)
        if (dexterityLevel >= 50) adpRate += TalentTreeValues.dexterityEffects[3];

        return adpRate;
    }

    public void unloadToolData(@NotNull UUID uuid) {
        toolDataMap.remove(uuid);
    }

    public boolean hasToolData(@NotNull UUID uuid) {
        return toolDataMap.containsKey(uuid);
    }
}
