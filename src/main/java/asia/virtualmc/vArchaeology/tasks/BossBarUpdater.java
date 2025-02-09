package asia.virtualmc.vArchaeology.tasks;

import asia.virtualmc.vLibrary.utils.EXPDisplayUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BossBarUpdater {
    private final JavaPlugin plugin;
    private final Map<UUID, EXPData> expUpdates;
    private final String pluginName = "Archaeology";

    public BossBarUpdater(@NotNull TaskManager taskManager) {
        this.plugin = taskManager.getMain();
        this.expUpdates = new ConcurrentHashMap<>();
    }

    public void sendBossBarToAll() {
        expUpdates.forEach((uuid, data) -> {
            Player player = Bukkit.getPlayer(uuid);

            if (player != null && player.isOnline()) {
                EXPDisplayUtils.showEXPBossBar(plugin, player, pluginName, data.currentExp,
                        data.nextLevelExp, data.newExp, data.currentLevel);
            }
        });
        expUpdates.clear();
    }

    public void updateEXPMetrics(@NotNull UUID uuid,
                                 double currentExp,
                                 double newExp,
                                 int nextLevelEXP,
                                 int currentLevel) {

        expUpdates.merge(uuid, new EXPData(currentExp, newExp, nextLevelEXP, currentLevel), (existing, update) -> {
            existing.addExp(update.getNewExp());
            existing.updateCurrentExp(update.getCurrentExp());
            existing.updateNextLevelExp(update.getNextLevelExp());
            existing.updateCurrentLevel(update.getCurrentLevel());
            return existing;
        });
    }

    private static class EXPData {
        private double currentExp;
        private double newExp;
        private int nextLevelExp;
        private int currentLevel;

        public EXPData(double currentExp, double newExp, int nextLevelExp, int currentLevel) {
            this.currentExp = currentExp;
            this.newExp = newExp;
            this.nextLevelExp = nextLevelExp;
            this.currentLevel = currentLevel;
        }

        public double getNewExp() {
            return newExp;
        }

        public double getCurrentExp() {
            return currentExp;
        }

        public int getNextLevelExp() {
            return nextLevelExp;
        }

        public int getCurrentLevel() {
            return currentLevel;
        }

        public void addExp(double exp) {
            this.newExp += exp;
        }

        public void updateCurrentExp(double currentExp) {
            this.currentExp = currentExp;
        }

        public void updateNextLevelExp(int nextLevelExp) {
            this.nextLevelExp = nextLevelExp;
        }

        public void updateCurrentLevel(int currentLevel) {
            this.currentLevel = currentLevel;
        }
    }
}
