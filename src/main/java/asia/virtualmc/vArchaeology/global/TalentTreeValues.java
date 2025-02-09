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

        wisdomEffects = TalentTreeConfig.getWisdomEffects(plugin, Main.prefix);
        charismaEffects = TalentTreeConfig.getCharismaEffects(plugin, Main.prefix);
        karmaEffects = TalentTreeConfig.getKarmaEffects(plugin, Main.prefix);
        dexterityEffects = TalentTreeConfig.getDexterityEffects(plugin, Main.prefix);
    }
}
