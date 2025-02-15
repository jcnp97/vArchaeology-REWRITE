package asia.virtualmc.vArchaeology.handlers.block_break;

import asia.virtualmc.vArchaeology.items.CustomCrafting;
import asia.virtualmc.vArchaeology.storage.PlayerData;
import asia.virtualmc.vLibrary.interfaces.BlockBreakHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.UUID;

public class CraftingProvider implements BlockBreakHandler {
    private final CustomCrafting customCrafting;
    private final PlayerData playerData;
    private final Random random;

    public CraftingProvider(@NotNull CustomCrafting customCrafting,
                            @NotNull PlayerData playerData) {
        this.playerData = playerData;
        this.customCrafting = customCrafting;
        this.random = new Random();
    }

    @Override
    public void onBlockBreakHandler(BlockBreakEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (playerData.getCurrentLevel(uuid) <= 98) return;

        Block block = event.getBlock();
        Material blockMaterial = block.getType();
        Location location = event.getBlock().getLocation();

        if (random.nextInt(1, 3) == 2) {
            switch (blockMaterial) {
                case Material.SAND -> customCrafting.dropItem(
                        player, location, "mots_blueprint_1");
                case Material.RED_SAND -> customCrafting.dropItem(
                        player, location, "mots_blueprint_2");
                case Material.SOUL_SAND -> customCrafting.dropItem(
                        player, location, "mots_blueprint_3");
                case Material.DIRT -> customCrafting.dropItem(
                        player, location, "mots_blueprint_4");
                case Material.GRAVEL -> customCrafting.dropItem(
                        player, location, "mots_blueprint_5");
            }
        }
    }
}
