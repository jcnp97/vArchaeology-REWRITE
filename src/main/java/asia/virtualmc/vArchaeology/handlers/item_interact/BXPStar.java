package asia.virtualmc.vArchaeology.handlers.item_interact;

import asia.virtualmc.vArchaeology.items.CustomBXPStars;
import asia.virtualmc.vArchaeology.storage.PlayerData;
import asia.virtualmc.vLibrary.enums.EnumsLib;
import asia.virtualmc.vLibrary.guis.LampGUI;
import asia.virtualmc.vLibrary.guis.StarGUI;
import asia.virtualmc.vLibrary.interfaces.ItemInteractHandler;
import asia.virtualmc.vLibrary.items.EXPItemLib;
import asia.virtualmc.vLibrary.items.ItemsLib;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class BXPStar implements ItemInteractHandler {
    private final PlayerData playerData;

    public BXPStar(@NotNull PlayerData playerData) {
        this.playerData = playerData;
    }

    @Override
    public void onItemInteractHandler(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) {
            return;
        }

        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        if (!pdc.has(CustomBXPStars.getItemKey(), PersistentDataType.INTEGER)) return;

        event.setCancelled(true);
        Player player = event.getPlayer();

        if (pdc.has(CustomBXPStars.getItemKey(), PersistentDataType.INTEGER)) {
            int lampID = ItemsLib.getItemID(item, CustomBXPStars.getItemKey());
            int initialAmount = player.getInventory().getItemInMainHand().getAmount();
            int skillLevel = playerData.getCurrentLevel(player.getUniqueId());
            double initialXP = EXPItemLib.getLampOrStarXP(skillLevel, lampID, initialAmount);

            StarGUI.openBXPStarGUI(player, initialAmount, initialXP, this::processBXPAction);
        }
    }

    @Override
    public void onBlockPlaceHandler(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!item.hasItemMeta()) {
            return;
        }

        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        if (!pdc.has(CustomBXPStars.getItemKey(), PersistentDataType.INTEGER)) return;

        event.setCancelled(true);

        if (pdc.has(CustomBXPStars.getItemKey(), PersistentDataType.INTEGER)) {
            int lampID = ItemsLib.getItemID(item, CustomBXPStars.getItemKey());
            int initialAmount = player.getInventory().getItemInMainHand().getAmount();
            int skillLevel = playerData.getCurrentLevel(player.getUniqueId());
            double initialXP = EXPItemLib.getLampOrStarXP(skillLevel, lampID, initialAmount);

            StarGUI.openBXPStarGUI(player, initialAmount, initialXP, this::processBXPAction);
        }
    }

    private void processBXPAction(@NotNull Player player, double initialXP) {
        ItemStack item = player.getInventory().getItemInMainHand();

        int lampID = ItemsLib.getItemID(item, CustomBXPStars.getItemKey());
        int skillLevel = playerData.getCurrentLevel(player.getUniqueId());
        int finalAmount = player.getInventory().getItemInMainHand().getAmount();
        double finalXP = EXPItemLib.getLampOrStarXP(skillLevel, lampID, finalAmount);

        try {
            if (finalXP == initialXP) {
                player.getInventory().removeItem(item);
                LampGUI.lampEXPEffects(player, finalXP);
                playerData.updateBXP(player, EnumsLib.UpdateType.ADD, finalXP);
            } else {
                player.sendMessage("§cYour inventory had changed. Please try again.");
            }
        } catch (Exception e) {
            player.sendMessage("§cThere was an error processing the star. Please contact the administrator.");
        }

        player.closeInventory();
    }
}
