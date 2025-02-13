package asia.virtualmc.vArchaeology.handlers.item_interact;

import asia.virtualmc.vArchaeology.items.CustomEXPLamps;
import asia.virtualmc.vArchaeology.storage.PlayerData;
import asia.virtualmc.vLibrary.enums.EnumsLib;
import asia.virtualmc.vLibrary.guis.LampGUI;
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

public class EXPLamp implements ItemInteractHandler {
    private final PlayerData playerData;

    public EXPLamp(@NotNull PlayerData playerData) {
        this.playerData = playerData;
    }

    @Override
    public void onItemInteractHandler(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) {
            return;
        }

        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        if (!pdc.has(CustomEXPLamps.ITEM_KEY, PersistentDataType.INTEGER)) return;

        event.setCancelled(true);
        Player player = event.getPlayer();

        if (pdc.has(CustomEXPLamps.ITEM_KEY, PersistentDataType.INTEGER)) {
            int lampID = ItemsLib.getItemID(item, CustomEXPLamps.ITEM_KEY);
            int initialAmount = player.getInventory().getItemInMainHand().getAmount();
            int skillLevel = playerData.getCurrentLevel(player.getUniqueId());
            double initialXP = EXPItemLib.getLampOrStarXP(skillLevel, lampID, initialAmount);

            LampGUI.openEXPLampGUI(player, initialAmount, initialXP, this::processXPAction);
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
        if (!pdc.has(CustomEXPLamps.ITEM_KEY, PersistentDataType.INTEGER)) return;

        event.setCancelled(true);

        if (pdc.has(CustomEXPLamps.ITEM_KEY, PersistentDataType.INTEGER)) {
            int lampID = ItemsLib.getItemID(item, CustomEXPLamps.ITEM_KEY);
            int initialAmount = player.getInventory().getItemInMainHand().getAmount();
            int skillLevel = playerData.getCurrentLevel(player.getUniqueId());
            double initialXP = EXPItemLib.getLampOrStarXP(skillLevel, lampID, initialAmount);

            LampGUI.openEXPLampGUI(player, initialAmount, initialXP, this::processXPAction);
        }
    }

    private void processXPAction(@NotNull Player player, double initialXP) {
        ItemStack item = player.getInventory().getItemInMainHand();

        int lampID = ItemsLib.getItemID(item, CustomEXPLamps.ITEM_KEY);
        int skillLevel = playerData.getCurrentLevel(player.getUniqueId());
        int finalAmount = player.getInventory().getItemInMainHand().getAmount();
        double finalXP = EXPItemLib.getLampOrStarXP(skillLevel, lampID, finalAmount);

        try {
            if (finalXP == initialXP) {
                player.getInventory().removeItem(item);
                LampGUI.lampEXPEffects(player, finalXP);
                playerData.updateEXP(player, EnumsLib.UpdateType.ADD, finalXP);
            } else {
                player.sendMessage("§cYour inventory had changed. Please try again.");
            }
        } catch (Exception e) {
            player.sendMessage("§cThere was an error processing the lamp. Please contact the administrator.");
        }

        player.closeInventory();
    }
}
