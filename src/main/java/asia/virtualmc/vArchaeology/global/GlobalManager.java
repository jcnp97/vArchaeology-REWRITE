package asia.virtualmc.vArchaeology.global;

import asia.virtualmc.vArchaeology.Main;
import org.jetbrains.annotations.NotNull;

public class GlobalManager {
    private final Main plugin;
    private final TalentTreeValues talentTreeValues;

    public GlobalManager(@NotNull Main plugin) {
        this.plugin = plugin;
        this.talentTreeValues = new TalentTreeValues(this);
    }

    public Main getMain() {
        return plugin;
    }
}
