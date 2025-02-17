package asia.virtualmc.vArchaeology.handlers.item_equip;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vArchaeology.global.TraitValues;
import asia.virtualmc.vArchaeology.items.CustomTools;
import asia.virtualmc.vArchaeology.storage.PlayerData;
import asia.virtualmc.vArchaeology.storage.StorageManager;
import asia.virtualmc.vArchaeology.storage.TalentTree;
import asia.virtualmc.vLibrary.interfaces.ItemEquipHandler;
import asia.virtualmc.vLibrary.items.ToolsLib;
import asia.virtualmc.vLibrary.utils.EffectsUtil;
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
    private final Map<UUID, ToolData> toolDataMap;
    public record ToolData(double gather, double adb, boolean enhancedT99) {}

    public ToolStats(@NotNull StorageManager storageManager) {
        this.plugin = storageManager.getMain();
        this.talentTree = storageManager.getTalentTree();
        this.playerData = storageManager.getPlayerData();
        this.toolDataMap = new ConcurrentHashMap<>();
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

    public void loadToolData(@NotNull Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();

        if (!ToolsLib.isCustomTool(item, CustomTools.getToolKey())) {
            return;
        }

        UUID uuid = player.getUniqueId();
        unloadToolData(uuid);

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        double gatherRate = calculateGatherRate(uuid, pdc);
        double adbProgress = calculateADB(uuid, pdc);
        boolean hasEnhancedT99 = ToolsLib.compareTool(item, CustomTools.getToolKey(), 11);

        toolDataMap.put(uuid, new ToolData(gatherRate, adbProgress, hasEnhancedT99));
        EffectsUtil.sendActionBarMessage(player, "<gray>Gathering Rate: <yellow>" +
                gatherRate + "% | <gray>Discovery Rate: <yellow>" + adbProgress + "%");
    }

    private double calculateGatherRate(@NotNull UUID uuid, PersistentDataContainer pdc) {
        int karmaLevel = playerData.getKarmaTrait(uuid);
        // Tool Gather Rate
        double gatherRate = pdc.getOrDefault(CustomTools.getGatherKey(), PersistentDataType.DOUBLE, 0.0);
        // Talent ID 3
        gatherRate += talentTree.getDataFromMap(uuid, 3) * 0.1;
        // Karma Trait
        gatherRate += karmaLevel * TraitValues.karmaEffects[0];
        // Karma Trait (Max level bonus)
        if (karmaLevel >= 50) gatherRate += TraitValues.karmaEffects[3];

        return gatherRate;
    }

    private double calculateADB(@NotNull UUID uuid, PersistentDataContainer pdc) {
        int dexterityLevel = playerData.getDexterityTrait(uuid);
        // Tool ADB
        double adpRate = pdc.getOrDefault(CustomTools.getADPRate(), PersistentDataType.DOUBLE, 0.0);
        // Talent ID 8
        adpRate += talentTree.getDataFromMap(uuid, 8) * 0.01;
        // Talent ID 17
        adpRate += talentTree.getDataFromMap(uuid, 17) * 0.15;
        // Dexterity Trait
        adpRate += dexterityLevel * TraitValues.dexterityEffects[0];
        // Dexterity Trait (Max level bonus)
        if (dexterityLevel >= 50) adpRate += TraitValues.dexterityEffects[3];

        return adpRate;
    }

    public void unloadToolData(@NotNull UUID uuid) {
        toolDataMap.remove(uuid);
    }

    public boolean hasToolData(@NotNull UUID uuid) {
        return toolDataMap.containsKey(uuid);
    }

    public ToolData getToolStats(@NotNull UUID uuid) {
        return toolDataMap.get(uuid);
    }
}
