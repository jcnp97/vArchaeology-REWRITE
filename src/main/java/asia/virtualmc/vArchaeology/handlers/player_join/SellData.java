package asia.virtualmc.vArchaeology.handlers.player_join;

import asia.virtualmc.vArchaeology.global.GlobalManager;
import asia.virtualmc.vArchaeology.global.TraitValues;
import asia.virtualmc.vArchaeology.storage.PlayerData;
import asia.virtualmc.vArchaeology.storage.Statistics;
import asia.virtualmc.vArchaeology.storage.StorageManager;
import asia.virtualmc.vArchaeology.storage.TalentTree;
import asia.virtualmc.vLibrary.interfaces.PlayerJoinHandler;
import asia.virtualmc.vLibrary.utils.ConsoleMessageUtil;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SellData implements PlayerJoinHandler {
    private final PlayerData playerData;
    private final TalentTree talentTree;
    private final Statistics statistics;
    private final Map<UUID, SellDataRecord> sellDataMap;

    public SellData(@NotNull StorageManager storageManager) {
        this.playerData = storageManager.getPlayerData();
        this.talentTree = storageManager.getTalentTree();
        this.statistics = storageManager.getStatistics();
        sellDataMap = new ConcurrentHashMap<>();
    }

    public record SellDataRecord(double drops, double artefacts, double taxes) {}

    @Override
    public void onPlayerJoinHandler(org.bukkit.event.player.PlayerJoinEvent event) {
        //if (sellDataMap.containsKey(uuid)) return;
        addDataToMap(event.getPlayer().getUniqueId());
    }

    @Override
    public void onPlayerQuitHandler(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        sellDataMap.remove(uuid);
    }

    public void addDataToMap(@NotNull UUID uuid) {
        sellDataMap.remove(uuid);

        double drops = calculateDropMultiplier(uuid);
        double artefacts = calculateArtefactMultiplier(uuid);
        double tax = calculateTaxMultiplier(uuid);

        sellDataMap.put(uuid, new SellDataRecord(
                drops, artefacts, tax));
    }

    private double calculateDropMultiplier(UUID uuid) {
        double baseMultiplier = 100.0;
        int charismaLevel = playerData.getCharismaTrait(uuid);

        // Charisma Trait Bonus
        baseMultiplier += (double) charismaLevel * TraitValues.charismaEffects[0];
        // Talent ID 5
        baseMultiplier += (double) talentTree.getDataFromMap(uuid, 5) * 2;

        return baseMultiplier / 100.0;
    }

    private double calculateArtefactMultiplier(UUID uuid) {
        double baseMultiplier = 100.0;
        int charismaLevel = playerData.getCharismaTrait(uuid);

        // Charisma Trait Bonus
        baseMultiplier += (double) charismaLevel * TraitValues.charismaEffects[1];
        // Talent ID 5
        baseMultiplier += (double) talentTree.getDataFromMap(uuid, 7);

        return baseMultiplier / 100.0;
    }

    private double calculateTaxMultiplier(UUID uuid) {
        return (0.75 -
                (Math.min(statistics.getDataFromMap(uuid, 1), 50)
                        * 1.2) / 100.0);
    }

    public void reloadAllData() {
        try {
            for (UUID uuid : sellDataMap.keySet()) {
                addDataToMap(uuid);
            }
        } catch (Exception e) {
            ConsoleMessageUtil.printSevere(GlobalManager.prefix +
                    "An error has occurred when reloading sell data map: " + e.getMessage());
        }
    }

    public SellDataRecord getSellData(@NotNull UUID uuid) {
        return sellDataMap.get(uuid);
    }
}
