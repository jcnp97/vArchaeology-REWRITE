package asia.virtualmc.vArchaeology.global;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vLibrary.configs.DropsConfig;
import org.jetbrains.annotations.NotNull;

public class MaterialDrop {
    public static int[] dropWeight;
    public static int[] dropEXP;
    public static double[] dropPrice;

    public MaterialDrop(@NotNull GlobalManager globalManager) {
        Main plugin = globalManager.getMain();

        dropWeight = DropsConfig.readDropWeightsFile(plugin, GlobalManager.prefix);
        dropPrice = DropsConfig.readDropPriceFile(plugin, GlobalManager.prefix);
        dropEXP = DropsConfig.readDropEXPFile(plugin, GlobalManager.prefix);
    }
}
