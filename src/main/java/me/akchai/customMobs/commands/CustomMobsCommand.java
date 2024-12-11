package me.akchai.customMobs.commands;

import me.akchai.customMobs.CustomMobs;
import me.akchai.customMobs.files.CustomMobsConfig;
import me.akchai.customMobs.files.SpawnPointConfig;
import me.akchai.customMobs.utils.AutoSpawner;

import me.akchai.firstPlugin.commands.ItemListCommand;

import net.md_5.bungee.api.chat.*;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;


import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CustomMobsCommand implements CommandExecutor {


    private static CustomMobsCommand instance;

    public CustomMobsCommand() {
        instance = this; // Set the static instance
    }

    public static CustomMobsCommand getInstance() {
        return instance;
    }

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
                    break;
                case "spawnpoint":
                    spawnPoint(commandSender, args);
                    break;
                case "spawnlist":
                    spawnPointList(commandSender);
                    break;
                case "teleport":
                    teleport(commandSender, args);
                    break;
            }
        }

        return true;
    }
    public void teleport(CommandSender sender,String[] args){
        String worldName = args[1];
        double x = 0, y = 0, z = 0;
        try {
            x = Double.parseDouble(args[2]);
            y = Double.parseDouble(args[3]);
            z = Double.parseDouble(args[4]);
        } catch (NumberFormatException e) {
            sender.sendMessage(prefix + "Invalid coordinates.");
        }

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            sender.sendMessage(prefix + "World not found: " + worldName);
        }

        Location loc = new Location(world, x, y, z);
        Player p = (Player) sender;
        p.teleport(loc);
    }

    public void spawnPointList(CommandSender sender){
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
            return;
        }

        if (SpawnPointConfig.getConfig().contains("spawnpoints")) {
            for (String key : SpawnPointConfig.getConfig().getConfigurationSection("spawnpoints").getKeys(false)) {
                String mob = SpawnPointConfig.getConfig().getString("spawnpoints." + key + ".mob");
                String worldName = SpawnPointConfig.getConfig().getString("spawnpoints." + key + ".location.world");
                double x = SpawnPointConfig.getConfig().getDouble("spawnpoints." + key + ".location.x");
                double y = SpawnPointConfig.getConfig().getDouble("spawnpoints." + key + ".location.y");
                double z = SpawnPointConfig.getConfig().getDouble("spawnpoints." + key + ".location.z");

                // Create the clickable teleport text
                TextComponent message = new TextComponent(ChatColor.translateAlternateColorCodes('&', prefix + "Key: " + key + ", Mob: " + mob));
                message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                        "/custommobs teleport " + worldName + " " + x + " " + y + " " + z));
                message.setHoverEvent(new net.md_5.bungee.api.chat.HoverEvent(
                        net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder("Click to teleport to " + worldName + " (" + x + ", " + y + ", " + z + ")").create()
                ));

                sender.spigot().sendMessage(message); // Use Spigot API to send clickable message
            }
        } else {
            sender.sendMessage(prefix + "No spawn points found.");
        }
    }

    public void spawnPoint(CommandSender sender, String[] args) {
        if (args.length == 4) { // "mobName", "interval", ve "location" bilgisi gerekiyor.
            if (!(sender instanceof Player)) {
                sender.sendMessage("Bu komut sadece oyuncular tarafından kullanılabilir.");
                return;
            }

            Player player = (Player) sender;
            String mobName = args[1];
            int interval = 0;

            try {
                interval = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                player.sendMessage("Geçerli bir doğma süresi belirtin (saniye).");
            }

            Location loc = player.getLocation(); // Oyuncunun mevcut konumu.
            UUID uuid = UUID.randomUUID();
            String spawnPointKey = "spawnpoints." + uuid; // Benzersiz anahtar oluştur.
            String uuid2 = uuid.toString();

            SpawnPointConfig.getConfig().set(spawnPointKey + ".mob", mobName);
            SpawnPointConfig.getConfig().set(spawnPointKey + ".interval", interval);
            SpawnPointConfig.getConfig().set(spawnPointKey + ".location.world", loc.getWorld().getName());
            SpawnPointConfig.getConfig().set(spawnPointKey + ".location.x", loc.getX());
            SpawnPointConfig.getConfig().set(spawnPointKey + ".location.y", loc.getY());
            SpawnPointConfig.getConfig().set(spawnPointKey + ".location.z", loc.getZ());
            SpawnPointConfig.saveConfig();

            new AutoSpawner(uuid2).runTaskTimer(CustomMobs.getPlugin(), 0, interval * 20L);

            sender.sendMessage("Spawn point added: " + spawnPointKey + " for mob " + mobName + " at " + loc.toString());
        }
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
            } else {
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


    public Entity spawnMob(Player p, String mobName, Location loc){
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
                        livingEntity.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(CustomMobsConfig.getConfig().getDouble("mobs."+mobName+".speed"));
                        livingEntity.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(CustomMobsConfig.getConfig().getDouble("mobs."+mobName+".damage"));
                        livingEntity.setCustomName(ChatColor.translateAlternateColorCodes('&', CustomMobsConfig.getConfig().getString("mobs."+mobName+".name")+" &d" + (int) livingEntity.getHealth() + "/" + (int) livingEntity.getMaxHealth()));
                        if (CustomMobsConfig.getConfig().get("mobs."+mobName+".xp") != null) {
                            livingEntity.getPersistentDataContainer().set(new NamespacedKey(CustomMobs.getPlugin(), "xp"), PersistentDataType.INTEGER, CustomMobsConfig.getConfig().getInt("mobs."+mobName+".xp"));
                        }

                        return spawnEntity;

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
        return null;
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
