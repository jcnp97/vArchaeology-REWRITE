package asia.virtualmc.vArchaeology.global;

import asia.virtualmc.vArchaeology.Main;
import asia.virtualmc.vLibrary.configs.TalentTreeConfig;
import org.jetbrains.annotations.NotNull;

public class TalentTreeValues {
    private final Main plugin;
    public static double[] wisdomEffects;
    public static double[] charismaEffects;
    public static double[] karmaEffects;
    public static double[] dexterityEffects;

    public TalentTreeValues(@NotNull GlobalManager globalManager) {
        this.plugin = globalManager.getMain();

        wisdomEffects = TalentTreeConfig.getWisdomEffects(plugin, GlobalManager.prefix);
        charismaEffects = TalentTreeConfig.getCharismaEffects(plugin, GlobalManager.prefix);
        karmaEffects = TalentTreeConfig.getKarmaEffects(plugin, GlobalManager.prefix);
        dexterityEffects = TalentTreeConfig.getDexterityEffects(plugin, GlobalManager.prefix);
    }
}
