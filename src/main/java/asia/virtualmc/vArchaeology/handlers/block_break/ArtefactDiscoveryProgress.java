package asia.virtualmc.vArchaeology.handlers.block_break;

import asia.virtualmc.vArchaeology.global.GlobalManager;
import asia.virtualmc.vArchaeology.handlers.item_equip.ToolStats;
import asia.virtualmc.vArchaeology.handlers.player_join.TraitData;
import asia.virtualmc.vArchaeology.storage.PlayerData;
import asia.virtualmc.vLibrary.enums.EnumsLib;
import asia.virtualmc.vLibrary.interfaces.BlockBreakHandler;
import asia.virtualmc.vLibrary.utils.ConsoleMessageUtil;
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
    private final TraitData traitData;
    private final ToolStats toolStats;
    private static final long ADP_COOLDOWN = 60_000;
    private final Map<UUID, Long> adpCooldowns = new ConcurrentHashMap<>();
    private final Random random;

    public ArtefactDiscoveryProgress(@NotNull PlayerData playerData,
                                     @NotNull TraitData traitData,
                                     @NotNull ToolStats toolStats) {
        this.playerData = playerData;
        this.traitData = traitData;
        this.toolStats = toolStats;
        this.random = new Random();
    }

    @Override
    public void onBlockBreakHandler(BlockBreakEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        Long lastUsage = adpCooldowns.get(uuid);
        double randomDouble = random.nextDouble();

        // Add ADP - Dexterity Passive 3
        if (randomDouble < traitData.getTraitData(uuid).addADP() / 100) {
            addArtefactProgress(player, 0.1);
        }

        if (lastUsage != null && (currentTime - lastUsage) < ADP_COOLDOWN) {
            return;
        }

        adpCooldowns.put(uuid, currentTime);
        // Normal ADP
        double toolADB = toolStats.getToolStats(uuid).adb();
        addArtefactProgress(player, toolADB);

        // Double ADP trigger
        if (randomDouble < traitData.getTraitData(uuid).doubleADP() / 100) {
            addArtefactProgress(player, toolADB);
            EffectsUtil.sendPlayerMessage(player, "<green>Your Dexterity trait has doubled your Artefact Discovery progress.");
        }
    }

    private void addArtefactProgress(@NotNull Player player, double value) {
        UUID uuid = player.getUniqueId();

        playerData.updateADP(player, EnumsLib.UpdateType.ADD, value);
        double adProgress = playerData.getADP(uuid);
        EffectsUtil.sendADBProgressBarTitle(uuid, adProgress / 100.0, value);
    }

    public void cleanupExpired() {
        try {
            long currentTime = System.currentTimeMillis();
            adpCooldowns.entrySet().removeIf(entry ->
                    currentTime - entry.getValue() >= ADP_COOLDOWN);
        } catch (Exception e) {
            ConsoleMessageUtil.printSevere(GlobalManager.prefix +
                    "An error has occurred when cleaning ADP map: " + e.getMessage());
        }
    }
}
