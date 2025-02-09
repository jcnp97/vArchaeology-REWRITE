package asia.virtualmc.vArchaeology.handlers.playerjoin;

import asia.virtualmc.vArchaeology.global.TalentTreeValues;
import asia.virtualmc.vArchaeology.storage.PlayerData;
import asia.virtualmc.vArchaeology.storage.StorageManager;
import asia.virtualmc.vLibrary.interfaces.PlayerJoinHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TraitData implements PlayerJoinHandler {
    private final PlayerData playerData;
    public static Map<UUID, TraitDataRecord> traitDataMap;
    public record TraitDataRecord(double extraRoll, double nextTier, double doubleADP, double addADP) {}

    public TraitData(@NotNull StorageManager storageManager) {
        this.playerData = storageManager.getPlayerData();
        traitDataMap = new ConcurrentHashMap<>();
    }

    @Override
    public void onPlayerJoinHandler(org.bukkit.event.player.PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        int karmaLevel = playerData.getKarmaTrait(uuid);
        int dexterityLevel = playerData.getDexterityTrait(uuid);

        double extraRoll = (double) karmaLevel * TalentTreeValues.karmaEffects[1];
        double nextTierRoll = (double) karmaLevel * TalentTreeValues.karmaEffects[2];
        double doubleADP = (double) dexterityLevel * TalentTreeValues.dexterityEffects[1];
        double addADP = (double) dexterityLevel * TalentTreeValues.dexterityEffects[2];

        traitDataMap.put(uuid, new TraitDataRecord(extraRoll, nextTierRoll, doubleADP, addADP));
    }

    @Override
    public void onPlayerQuitHandler(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        traitDataMap.remove(uuid);
    }
}
