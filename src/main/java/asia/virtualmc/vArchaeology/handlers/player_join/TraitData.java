package asia.virtualmc.vArchaeology.handlers.player_join;

import asia.virtualmc.vArchaeology.global.GlobalManager;
import asia.virtualmc.vArchaeology.global.TraitValues;
import asia.virtualmc.vArchaeology.storage.PlayerData;
import asia.virtualmc.vLibrary.interfaces.PlayerJoinHandler;
import asia.virtualmc.vLibrary.utils.ConsoleMessageUtil;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TraitData implements PlayerJoinHandler {
    private final PlayerData playerData;
    private final Map<UUID, TraitDataRecord> traitDataMap;

    public TraitData(@NotNull PlayerData playerData) {
        this.playerData = playerData;
        traitDataMap = new ConcurrentHashMap<>();
    }

    public record TraitDataRecord(double extraRoll, double nextTier, double doubleADP, double addADP) {}

    @Override
    public void onPlayerJoinHandler(org.bukkit.event.player.PlayerJoinEvent event) {
        addDataToMap(event.getPlayer().getUniqueId());
    }

    @Override
    public void onPlayerQuitHandler(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        traitDataMap.remove(uuid);
    }

    private void addDataToMap(@NotNull UUID uuid) {
        traitDataMap.remove(uuid);

        int karmaLevel = playerData.getKarmaTrait(uuid);
        int dexterityLevel = playerData.getDexterityTrait(uuid);

        double extraRoll = (double) karmaLevel * TraitValues.karmaEffects[1];
        double nextTierRoll = (double) karmaLevel * TraitValues.karmaEffects[2];
        double doubleADP = (double) dexterityLevel * TraitValues.dexterityEffects[1];
        double addADP = (double) dexterityLevel * TraitValues.dexterityEffects[2];

        traitDataMap.put(uuid, new TraitDataRecord(extraRoll,
                nextTierRoll, doubleADP, addADP));
    }

    public void reloadAllData() {
        try {
            for (UUID uuid : traitDataMap.keySet()) {
                addDataToMap(uuid);
            }
        } catch (Exception e) {
            ConsoleMessageUtil.printSevere(GlobalManager.prefix +
                    "An error has occurred when reloading trait data map: " + e.getMessage());
        }
    }

    public TraitDataRecord getTraitData(@NotNull UUID uuid) {
        return traitDataMap.get(uuid);
    }
}
