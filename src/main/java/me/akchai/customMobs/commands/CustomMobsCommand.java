package me.akchai.customMobs.commands;

import me.akchai.customMobs.CustomMobs;
import me.akchai.customMobs.files.CustomMobsConfig;
import me.akchai.firstPlugin.commands.ItemListCommand;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_21_R2.entity.CraftPlayer;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.ItemList;


import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CustomMobsCommand implements CommandExecutor {


    public String prefix = CustomMobs.prefix;
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (commandSender instanceof Player p) {
            if (args.length == 0) {
                sendHelpMessage(p);
                return true;
            }
            switch (args[0]) {
                case "reload":
                    CustomMobsConfig.reloadConfig();
                    sendReloadMessage(p);
                    break;
                case "spawn":
                    spawnMob(p, args[1], p.getLocation());
                    break;
                case "list":
                    sendListMessage(p);
            }
        }

        return true;
    }

    private void setEquipmentItem(LivingEntity entity, String mobName, String slot, Player player, Material defaultMaterial) {
        String path = "mobs." + mobName + ".equipment." + slot;
        if (CustomMobsConfig.getConfig().contains(path)) {
            ItemStack item = ItemListCommand.getItem(player, CustomMobsConfig.getConfig().getString(path));
            if (item != null) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && meta.hasAttributeModifiers()) {
                    if (meta.getAttributeModifiers().containsKey(Attribute.MAX_HEALTH)){
                        meta.removeAttributeModifier(Attribute.MAX_HEALTH);
                        item.setItemMeta(meta);
                    }
                }
            }

            if (item == null) {
                Material material = Material.matchMaterial(CustomMobsConfig.getConfig().getString(path));
                if (material != null) {
                    item = new ItemStack(material);
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',prefix + "&cThere is no valid item for " + "\"" + path + "\""));
                    return;
                }
            }

            switch (slot) {
                case "hand" -> entity.getEquipment().setItemInMainHand(item);
                case "helmet" -> entity.getEquipment().setHelmet(item);
                case "chestplate" -> entity.getEquipment().setChestplate(item);
                case "leggings" -> entity.getEquipment().setLeggings(item);
                case "boots" -> entity.getEquipment().setBoots(item);
            }

        }
    }

    public void equipCustomMob(LivingEntity entity, String mobName, Player player) {
        setEquipmentItem(entity, mobName, "hand", player, Material.WOODEN_SWORD);
        setEquipmentItem(entity, mobName, "helmet", player, Material.LEATHER_HELMET);
        setEquipmentItem(entity, mobName, "chestplate", player, Material.LEATHER_CHESTPLATE);
        setEquipmentItem(entity, mobName, "leggings", player, Material.LEATHER_LEGGINGS);
        setEquipmentItem(entity, mobName, "boots", player, Material.LEATHER_BOOTS);
    }

    public void spawnMob(Player p, String mobName, Location loc){
        if (p.hasPermission("custommobs.spawn")) {
            String entityTypeName = CustomMobsConfig.getConfig().getString("mobs."+mobName+".entity");
            if (CustomMobsConfig.getConfig().contains("mobs."+mobName)) {
                try {
                    EntityType entityType = EntityType.valueOf(entityTypeName.toUpperCase());
                    Entity spawnEntity = loc.getWorld().spawnEntity(loc, entityType);

                    if (spawnEntity instanceof LivingEntity livingEntity) {
                        livingEntity.getAttribute(Attribute.MAX_HEALTH).setBaseValue(CustomMobsConfig.getConfig().getDouble("mobs."+mobName+".health"));
                        livingEntity.setHealth(CustomMobsConfig.getConfig().getDouble("mobs."+mobName+".health"));
                        equipCustomMob(livingEntity, mobName, p);
                        p.sendMessage(livingEntity.getHealth() + "");
                        p.sendMessage(livingEntity.getAttribute(Attribute.MAX_HEALTH).getBaseValue() + "");
                        p.sendMessage(livingEntity.getMaxHealth() + "");
                        livingEntity.setCustomNameVisible(true);
                        livingEntity.setRemoveWhenFarAway(false);
                        livingEntity.setCanPickupItems(false);
                        livingEntity.setCustomNameVisible(true);
                        livingEntity.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(CustomMobsConfig.getConfig().getDouble("mobs."+mobName+".damage"));
                        livingEntity.setCustomName(ChatColor.translateAlternateColorCodes('&', CustomMobsConfig.getConfig().getString("mobs."+mobName+".name")+" &d" + (int) livingEntity.getHealth() + "/" + (int) livingEntity.getMaxHealth()));
                    }
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&',prefix + "&aSpawned a " + entityTypeName));
                } catch (IllegalArgumentException e) {
                    p.sendMessage(e.getMessage());
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&cInvalid entity type: " + entityTypeName));
                }
            } else {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&',prefix + "&cThe mob \"&d" + mobName + "\" &cdoes not exist in config."));
            }
        } else {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&cYou do not have permission for this command"));
        }
    }

    private void sendHelpMessage(Player p) {
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+"&c/custommobs <reload|spawn|list|add|remove>"));
    }
    private void sendReloadMessage(Player p) {
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+"&aConfig reloaded."));
    }
    private void sendListMessage(Player p) {
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8------- " + prefix + "&8-------"));
        AtomicInteger i = new AtomicInteger(1);
        CustomMobsConfig.getConfig().getConfigurationSection("mobs").getKeys(false).forEach(key -> {
            TextComponent test = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&d"+i+". &a"+key));
            List<BaseComponent> components = CustomMobsConfig.getConfig()
                    .getConfigurationSection("mobs." + key)
                    .getValues(false)
                    .entrySet()
                    .stream()
                    .map(entry -> new TextComponent(ChatColor.translateAlternateColorCodes('&', "&c"+Character.toUpperCase(entry.getKey().charAt(0)) + entry.getKey().substring(1) + "&d: &a" + entry.getValue() + "\n")))
                    .collect(Collectors.toList());
            BaseComponent[] hoverText = components.toArray(new BaseComponent[0]);
            test.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));
            p.spigot().sendMessage(test);
            i.getAndIncrement();
        });
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8------- " + prefix + "&8-------"));
    }
}
