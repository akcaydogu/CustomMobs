package me.akchai.customMobs.listeners;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

public class DamageListener implements Listener {

    public double DamageCalculator(LivingEntity targetEntity, Entity attackerEntity, boolean flag){
        double damage = 0;

        if (attackerEntity instanceof LivingEntity damagerEntity) {
            damage = damagerEntity.getAttribute(Attribute.ATTACK_DAMAGE).getValue();
        } else if (attackerEntity instanceof Projectile damagerPlayer) {
            if (damagerPlayer.getShooter() instanceof LivingEntity damagerEntity) {
                damage = damagerEntity.getAttribute(Attribute.ATTACK_DAMAGE).getValue();
            }
        }

//                double damage = sourceEntity.getAttribute(Attribute.ATTACK_DAMAGE).getValue();
        double armor = targetEntity.getAttribute(Attribute.ARMOR).getValue();
        double toughness = targetEntity.getAttribute(Attribute.ARMOR_TOUGHNESS).getValue();

        double withArmorReduction = damage * (1 - Math.min(20, Math.max(armor / 5, armor - damage / (2 + toughness / 4))) / 25);

        if (flag) {
            withArmorReduction = withArmorReduction*1.5;
        }

        if (attackerEntity instanceof Player p) {
            if (p.isOp()) {
                p.sendMessage("Damage: " + withArmorReduction);
                p.sendMessage("Armor: " + armor);
                p.sendMessage("Toughness: " + toughness);
                p.sendMessage("Health" + targetEntity.getHealth());
            }
        } else if (targetEntity instanceof Player p) {
            if (p.isOp()) {
                p.sendMessage("Damage: " + withArmorReduction);
                p.sendMessage("Armor: " + armor);
                p.sendMessage("Toughness: " + toughness);
                p.sendMessage("Health" + targetEntity.getHealth());
            }
        }

        return withArmorReduction;
    }
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        try {
            LivingEntity targetEntity = (LivingEntity) event.getEntity();
            Entity attackerEntity = event.getDamager();
            boolean flag = false;
            if (!(attackerEntity instanceof Projectile)) {
                LivingEntity attacker = (LivingEntity) attackerEntity;
                flag = attackerEntity.getFallDistance() > 0.0F && !attackerEntity.isOnGround() && !attacker.hasPotionEffect(PotionEffectType.BLINDNESS) && attackerEntity.getVehicle() == null;
            }


            double damage = DamageCalculator(targetEntity, attackerEntity, flag);

            // Set the damage directly, bypassing Minecraft's armor calculations
            event.setDamage(EntityDamageByEntityEvent.DamageModifier.BASE, damage);

            // Remove armor reduction by setting the armor modifier to 0
            if (event.isApplicable(EntityDamageByEntityEvent.DamageModifier.ARMOR)) {
                event.setDamage(EntityDamageByEntityEvent.DamageModifier.ARMOR, 0);
            }

            if (!(targetEntity instanceof Player)) {
                updateHealth(targetEntity, event.getDamage());
            }


//            event.setDamage(DamageCalculator(entity, attackerEntity));
        } catch (Exception e) {
            event.getEntity().sendMessage(e.getMessage());
        }
    }

    private void updateHealth(LivingEntity entity, double damage){
        double health = entity.getHealth();
        float maxHealth = (float) entity.getMaxHealth();

        if ( health - damage < 0) {
            health = 0;
        } else {
            health -= damage;
        }


        String formattedHealth = String.format("%.1f", health);  // 1 decimal place for health


        entity.setCustomName(ChatColor.translateAlternateColorCodes('&', entity.getCustomName().split(" ")[0] + " &d" + formattedHealth + "/" + maxHealth));
    }

}
