package asia.virtualmc.vArchaeology.global;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vLibrary.configs.TraitConfig;
import org.jetbrains.annotations.NotNull;

public class TraitValues {
    private final Main plugin;
    public static double[] wisdomEffects;
    public static double[] charismaEffects;
    public static double[] karmaEffects;
    public static double[] dexterityEffects;

    public TraitValues(@NotNull GlobalManager globalManager) {
        this.plugin = globalManager.getMain();

        wisdomEffects = TraitConfig.getWisdomEffects(plugin, GlobalManager.prefix);
        charismaEffects = TraitConfig.getCharismaEffects(plugin, GlobalManager.prefix);
        karmaEffects = TraitConfig.getKarmaEffects(plugin, GlobalManager.prefix);
        dexterityEffects = TraitConfig.getDexterityEffects(plugin, GlobalManager.prefix);
    }
}
