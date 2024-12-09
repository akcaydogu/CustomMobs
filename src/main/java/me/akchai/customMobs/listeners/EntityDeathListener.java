package me.akchai.customMobs.listeners;

import me.akchai.customMobs.CustomMobs;
import me.akchai.customMobs.files.CustomMobsConfig;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeathListener implements Listener {

    public String prefix = CustomMobs.prefix;

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e){
        LivingEntity entity = e.getEntity();
        if (entity.getCustomName() != null) {
            for (String mobName : CustomMobsConfig.getConfig().getConfigurationSection("mobs").getKeys(false)){
                String entityName = ChatColor.translateAlternateColorCodes('&', CustomMobsConfig.getConfig().getString("mobs."+mobName+".name"));
                if (entity.getCustomName().contains(entityName)) {
                    e.getDrops().clear();
                    break;
                }
            }
        }
    }

}
