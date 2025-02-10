package asia.virtualmc.vArchaeology.handlers.blockbreak;

import asia.virtualmc.vArchaeology.handlers.itemequip.ToolStats;
import asia.virtualmc.vArchaeology.handlers.playerjoin.TraitData;
import asia.virtualmc.vArchaeology.storage.PlayerData;
import asia.virtualmc.vLibrary.enums.EnumsLib;
import asia.virtualmc.vLibrary.interfaces.BlockBreakHandler;
import asia.virtualmc.vLibrary.utils.EffectsUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ArtefactDiscoveryProgress implements BlockBreakHandler {
    private final PlayerData playerData;
    private static final long ADP_COOLDOWN = 60_000;
    private final Map<UUID, Long> adpCooldowns = new ConcurrentHashMap<>();
    private final Random random;

    public ArtefactDiscoveryProgress(@NotNull PlayerData playerData) {
        this.playerData = playerData;
        this.random = new Random();
    }

    @Override
    public void onBlockBreakHandler(BlockBreakEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        Long lastUsage = adpCooldowns.get(uuid);

        if (lastUsage != null && (currentTime - lastUsage) < ADP_COOLDOWN) {
            return;
        }

        adpCooldowns.put(uuid, currentTime);
        addArtefactProgress(player);
        if (random.nextDouble() < TraitData.traitDataMap.get(uuid).doubleADP() / 100) {
            addArtefactProgress(player);
            EffectsUtil.sendPlayerMessage(player, "<green>Your Dexterity trait has doubled your Artefact Discovery progress.");
        }
    }

    private void addArtefactProgress(@NotNull Player player) {
        UUID uuid = player.getUniqueId();

        double adbAdd = ToolStats.toolDataMap.get(uuid).adb();
        playerData.updateADP(player, EnumsLib.UpdateType.ADD, adbAdd);
        double adbProgress = playerData.getADP(uuid);
        EffectsUtil.sendADBProgressBarTitle(uuid, adbProgress / 100.0, adbAdd);
    }

    public void cleanupExpired() {
        long currentTime = System.currentTimeMillis();
        adpCooldowns.entrySet().removeIf(entry ->
                currentTime - entry.getValue() >= ADP_COOLDOWN);
    }
}
