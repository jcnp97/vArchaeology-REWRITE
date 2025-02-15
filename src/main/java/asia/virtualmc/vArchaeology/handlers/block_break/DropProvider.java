package asia.virtualmc.vArchaeology.handlers.block_break;

import asia.virtualmc.vArchaeology.core.DropTable;
import asia.virtualmc.vArchaeology.exp.BlockBreakEXP;
import asia.virtualmc.vArchaeology.exp.MaterialGetEXP;
import asia.virtualmc.vArchaeology.handlers.item_equip.ToolStats;
import asia.virtualmc.vArchaeology.handlers.player_join.TraitData;
import asia.virtualmc.vArchaeology.items.CustomCharms;
import asia.virtualmc.vArchaeology.items.CustomDrops;
import asia.virtualmc.vArchaeology.items.ItemManager;
import asia.virtualmc.vArchaeology.storage.CollectionLog;
import asia.virtualmc.vArchaeology.storage.PlayerData;
import asia.virtualmc.vArchaeology.storage.StorageManager;
import asia.virtualmc.vLibrary.configs.MaterialBlockConfig;
import asia.virtualmc.vLibrary.enums.EnumsLib;
import asia.virtualmc.vLibrary.interfaces.BlockBreakHandler;
import asia.virtualmc.vLibrary.items.ItemsLib;
import asia.virtualmc.vLibrary.utils.EffectsUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class DropProvider implements BlockBreakHandler {
    private final CustomDrops customDrops;
    private final DropTable dropTable;
    private final TraitData traitData;
    private final ToolStats toolStats;
    private final PlayerData playerData;
    private final CollectionLog collectionLog;
    private final MaterialGetEXP materialGetEXP;
    private final BlockBreakEXP blockBreakEXP;
    private final Random random;
    public static Map<Material, Integer> archBlocks;

    public DropProvider(@NotNull CustomDrops customDrops,
                        @NotNull TraitData traitData,
                        @NotNull ToolStats toolStats,
                        @NotNull DropTable dropTable,
                        @NotNull StorageManager storageManager,
                        @NotNull MaterialGetEXP materialGetEXP,
                        @NotNull BlockBreakEXP blockBreakEXP) {
        this.customDrops = customDrops;
        this.traitData = traitData;
        this.toolStats = toolStats;
        this.dropTable = dropTable;
        this.playerData = storageManager.getPlayerData();
        this.collectionLog = storageManager.getCollectionLog();
        this.materialGetEXP = materialGetEXP;
        this.blockBreakEXP = blockBreakEXP;
        this.random = new Random();

        archBlocks = MaterialBlockConfig.loadArchBlocks(storageManager.getMain());
    }

    @Override
    public void onBlockBreakHandler(BlockBreakEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        Block block = event.getBlock();
        Material material = block.getType();
        Integer blockBaseEXP = archBlocks.get(material);
        if (blockBaseEXP == null) {
            return;
        }

        double blockEXP = blockBreakEXP.getTotalBlockBreakEXP(uuid, blockBaseEXP);

        if (random.nextDouble() < toolStats.getToolStats(uuid).gather() / 100) {
            if (!dropTable.hasDropTable(uuid)) {
                dropTable.loadDropTable(uuid, playerData.getCurrentLevel(uuid));
            }

            Location location = event.getBlock().getLocation();
            giveDropToPlayer(player, uuid, location, blockEXP, true);
        } else {
            playerData.updateEXP(player, EnumsLib.UpdateType.ADD, blockEXP, true);
        }
    }

    private void giveDropToPlayer(@NotNull Player player,
                                  @NotNull UUID uuid,
                                  @NotNull Location location,
                                  double blockEXP,
                                  boolean canTriggerAgain) {

        int dropTable = generateDropType(player, uuid);
        double randomNumber = random.nextDouble();

        // Karma - Next Tier Passive
        if ((randomNumber < (traitData.getTraitData(uuid).nextTier() / 100.0))
                && canTriggerAgain && (dropTable < 7)) {
            dropTable++;
            EffectsUtil.sendPlayerMessage(player, "<green>Your Dexterity trait has increased your drop tier by 1.");
        }

        // Enhanced T99 Passive
        if ((randomNumber < 0.25) && toolStats.getToolStats(uuid).enhancedT99()
                && (dropTable < 7)) {
            dropTable++;
            EffectsUtil.sendPlayerMessage(player, "<green>Your Mattock has increased your drop tier by 1.");
        }

        // Item Drop
        customDrops.dropItem(player, dropTable, location);
        // Experience
        double finalXP = materialGetEXP.getTotalMaterialGetEXP(uuid, dropTable) + blockEXP;
        playerData.updateEXP(player, EnumsLib.UpdateType.ADD, finalXP, true);

        // Statistics Increment
        collectionLog.incrementData(uuid, dropTable);

        // Karma - Extra Drop Passive
        if (randomNumber < traitData.getTraitData(uuid).extraRoll() && canTriggerAgain) {
            giveDropToPlayer(player, uuid, location, 0, false);
        }
    }

    private int generateDropType(@NotNull Player player, @NotNull UUID uuid) {
        ItemStack offHandItem = player.getInventory().getItemInOffHand();
        int charmID = ItemsLib.getItemID(offHandItem, CustomCharms.getItemKey());

        if (charmID > 0) {
            offHandItem.setAmount(offHandItem.getAmount() - 1);
            return charmID;
        }

        return dropTable.rollDropTable(uuid);
    }
}
