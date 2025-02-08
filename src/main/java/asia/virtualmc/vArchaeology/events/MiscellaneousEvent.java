package asia.virtualmc.vArchaeology.events;

import asia.virtualmc.vArchaeology.Main;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class MiscellaneousEvent implements Listener {
    private final Main plugin;
    private final NamespacedKey TOOL_KEY;

    public MiscellaneousEvent(@NotNull EventManager eventManager) {
        this.plugin = eventManager.getMain();
        this.TOOL_KEY = new NamespacedKey(plugin, "varch_tool");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPrepareItemEnchant(PrepareItemEnchantEvent event) {
        ItemStack tool = event.getItem();
        if (isCustomTool(tool)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        ItemStack result = event.getResult();
        if (isCustomTool(result)) {
            event.setResult(null);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Firework) {
            Firework firework = (Firework) event.getDamager();
            if (firework.hasMetadata("nodamage")) {
                event.setCancelled(true);
            }
        }
    }

    private boolean isCustomTool(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        return pdc.has(TOOL_KEY, PersistentDataType.INTEGER);
    }
}
