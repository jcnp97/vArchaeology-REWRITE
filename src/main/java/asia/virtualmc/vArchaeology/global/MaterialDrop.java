package asia.virtualmc.vArchaeology.global;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vLibrary.configs.DropPriceConfig;
import asia.virtualmc.vLibrary.configs.DropWeightConfig;
import org.jetbrains.annotations.NotNull;

public class MaterialDrop {
    private final Main plugin;
    public static int[] dropWeight;
    public static double[] dropPrice;

    public MaterialDrop(@NotNull GlobalManager globalManager) {
        this.plugin = globalManager.getMain();

        dropWeight = DropWeightConfig.readDropWeightsFile(plugin, Main.prefix);
        dropPrice = DropPriceConfig.readDropPriceFile(plugin, Main.prefix);
    }
}
