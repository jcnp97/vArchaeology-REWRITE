package asia.virtualmc.vArchaeology.handlers.blockbreak;

import asia.virtualmc.vArchaeology.core.DropTable;
import asia.virtualmc.vArchaeology.items.CustomMaterials;
import asia.virtualmc.vArchaeology.items.CustomTools;
import asia.virtualmc.vArchaeology.items.ItemManager;
import asia.virtualmc.vArchaeology.storage.PlayerData;
import asia.virtualmc.vArchaeology.storage.StorageManager;
import asia.virtualmc.vLibrary.enums.EnumsLib;
import asia.virtualmc.vLibrary.interfaces.BlockBreakHandler;
import asia.virtualmc.vLibrary.items.ToolsLib;
import asia.virtualmc.vLibrary.utils.EffectsUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.UUID;

public class ToolPassiveEffect implements BlockBreakHandler {
    private final PlayerData playerData;
    private final Random random;
    private final DropTable dropTable;
    private final CustomMaterials customMaterials;

    public ToolPassiveEffect(@NotNull StorageManager storageManager,
                             @NotNull DropTable dropTable,
                             @NotNull ItemManager itemManager) {
        this.playerData = storageManager.getPlayerData();
        this.dropTable = dropTable;
        this.customMaterials = itemManager.getCustomMaterials();
        this.random = new Random();
    }

    @Override
    public void onBlockBreakHandler(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();

        if (!ToolsLib.compareTool(mainHandItem, CustomTools.TOOL_KEY, 10)) return;
        UUID uuid = player.getUniqueId();

        if (random.nextInt(100) < 1) {
            playerData.updateADP(player, EnumsLib.UpdateType.ADD, 1.0);
            EffectsUtil.sendADBProgressBarTitle(uuid, playerData.getADP(uuid) / 100.0, 1.0);
            EffectsUtil.sendPlayerMessage(player, "<green>Your Chronal Focus from your tool has been activated.");
        }

        if (random.nextInt(10) < 1) {
            int drop = dropTable.rollDropTable(uuid);
            customMaterials.dropMaterialNaturally(player, location, drop);
            EffectsUtil.sendPlayerMessage(player, "<green>Your Time-Space Convergence from your tool has been activated.");
        }
    }
}
