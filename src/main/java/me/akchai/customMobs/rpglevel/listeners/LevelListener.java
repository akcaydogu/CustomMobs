package me.akchai.customMobs.rpglevel.listeners;

import me.akchai.customMobs.CustomMobs;
import me.akchai.customMobs.rpglevel.files.LevelConfig;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;

public class LevelListener implements Listener {

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {

        if (e.getEntity().getKiller() instanceof Player p && !(e.getEntity() instanceof Player)) {
            if ((e.getEntity().getPersistentDataContainer().has(new NamespacedKey(CustomMobs.getPlugin(), "xp"), PersistentDataType.INTEGER))) {
                e.setDroppedExp(0);

                //Get persistent xp of mob, player and xp needed for next level
                int xp = e.getEntity().getPersistentDataContainer().get(new NamespacedKey(CustomMobs.getPlugin(), "xp"), PersistentDataType.INTEGER);
                int playerXP = p.getPersistentDataContainer().get(new NamespacedKey(CustomMobs.getPlugin(), "xp"), PersistentDataType.INTEGER);
                int xpForNext = p.getPersistentDataContainer().get(new NamespacedKey(CustomMobs.getPlugin(), "xpfornext"), PersistentDataType.INTEGER);
                int newXP = xp + playerXP;

                int level = p.getPersistentDataContainer().get(new NamespacedKey(CustomMobs.getPlugin(), "level"), PersistentDataType.INTEGER);

                p.getPersistentDataContainer().set(new NamespacedKey(CustomMobs.getPlugin(), "xp"), PersistentDataType.INTEGER, newXP);

                int updatedXP = newXP;

                if (p.getPersistentDataContainer().get(new NamespacedKey(CustomMobs.getPlugin(), "xp"), PersistentDataType.INTEGER) >= xpForNext) {
                    p.getPersistentDataContainer().set(new NamespacedKey(CustomMobs.getPlugin(), "xp"), PersistentDataType.INTEGER, newXP-xpForNext);
                    updatedXP = newXP-xpForNext;
                    p.getPersistentDataContainer().set(new NamespacedKey(CustomMobs.getPlugin(), "level"), PersistentDataType.INTEGER, level+1);
                    level = p.getPersistentDataContainer().get(new NamespacedKey(CustomMobs.getPlugin(), "level"), PersistentDataType.INTEGER);
                    if (LevelConfig.getConfig().get("level." + level) != null) {
                        xpForNext = LevelConfig.getConfig().getInt("level." + level);
                    } else {
                        xpForNext *= 2;
                    }
                    p.getPersistentDataContainer().set(new NamespacedKey(CustomMobs.getPlugin(), "xpfornext"), PersistentDataType.INTEGER, xpForNext);
                }
                p.setLevel(level);
                p.setExp(updateProgress(updatedXP, xpForNext));
            }
        }
    }
    public float updateProgress(int xp, int xpForNext) {

        float progress = (float) xp / xpForNext;
        progress = Math.min(1.0f, Math.max(0.0f, progress));
        return progress;
    }



}
