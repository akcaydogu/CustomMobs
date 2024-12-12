package me.akchai.customMobs.listeners;

import me.akchai.customMobs.CustomMobs;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class LootSharingListener implements Listener {


    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {

        //*Share loot by damage percents
        //? Store damages in entity persistent data storage
        //! (if player doesnt hit in 5 minutes will gonna remove persistent data storage)
        //? Example
        //? 1000 HP MOB 400DAMAGE P1 600DAMAGE P2
        //? If the drop is greater than 1 split loot by percents
        //? If the drop amount is 1 give by percent chance

        if (e.getDamager() instanceof Player p && !(e.getEntity() instanceof Player)) {

            LivingEntity entity = (LivingEntity) e.getEntity();

            String name = p.getDisplayName();

            double damage = e.getDamage();

            if (entity.getPersistentDataContainer().has(getNameSpaceKey(name), PersistentDataType.DOUBLE)) {
                PersistentDataContainer container = entity.getPersistentDataContainer();
                double data = container.get(getNameSpaceKey(name), PersistentDataType.DOUBLE);
                container.set(getNameSpaceKey(name), PersistentDataType.DOUBLE, data+damage);
                return;
            }

            PersistentDataContainer container = entity.getPersistentDataContainer();
            container.set(getNameSpaceKey(name), PersistentDataType.DOUBLE, damage);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {

        if (!(e.getEntity() instanceof Player p)) {



        }

    }

    private NamespacedKey getNameSpaceKey(String name) {

        NamespacedKey key = new NamespacedKey(CustomMobs.getPlugin(), "player-" + name);

        return key;
    }

}
