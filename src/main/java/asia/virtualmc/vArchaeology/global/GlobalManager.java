package asia.virtualmc.vArchaeology.global;

import asia.virtualmc.vArchaeology.Main;
import org.jetbrains.annotations.NotNull;

public class GlobalManager {
    private final Main plugin;
    public static final String prefix = "[vArchaeology] ";
    public static final String severePrefix = "§c[vArchaeology] ";
    public static final String coloredPrefix = "§x§0§8§F§B§9§3[§x§1§A§F§8§9§0v§x§2§B§F§5§8§EA§x§3§D§F§2§8§Br§x§4§F§F§0§8§8c§x§6§0§E§D§8§6h§x§7§2§E§A§8§3a§x§8§4§E§7§8§1e§x§9§5§E§4§7§Eo§x§A§7§E§1§7§Bl§x§B§8§D§E§7§9o§x§C§A§D§C§7§6g§x§D§C§D§9§7§3y§x§E§D§D§6§7§1] §a";
    //public static final String coloredPrefix = "<gradient:#68FFA8:FFF676>[Archaeology]</gradient> <green>";


    public GlobalManager(@NotNull Main plugin) {
        this.plugin = plugin;
        TalentTreeValues talentTreeValues = new TalentTreeValues(this);
        MaterialDrop materialDrop = new MaterialDrop(this);
    }

    public Main getMain() {
        return plugin;
    }
}
