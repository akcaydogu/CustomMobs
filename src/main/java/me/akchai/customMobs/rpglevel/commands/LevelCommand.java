package me.akchai.customMobs.rpglevel.commands;

import me.akchai.customMobs.CustomMobs;
import me.akchai.customMobs.rpglevel.files.LevelConfig;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.units.qual.C;

import static me.akchai.customMobs.CustomMobs.prefix;

public class LevelCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (commandSender instanceof Player p) {
            if (p.hasPermission("custommobs.level")) {

                if (args.length == 0) {
                    showLevel(p);
                    return true;
                }
                switch (args[0]) {
                    case "set":
                        setLevel(p, args);
                }



            } else {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&cYou don't have permission to use this command!"));
            }

        }
        return true;
    }

    public void setLevel(Player p, String[] args) {
        if (args.length == 3) {
            switch (args[1]) {
                case "xp":
                    try {
                        int xpForNext = p.getPersistentDataContainer().get(new NamespacedKey(CustomMobs.getPlugin(), "xpfornext"), PersistentDataType.INTEGER);
                        int newXp = Integer.parseInt(args[2]);
                        if (xpForNext > newXp) {
                            p.getPersistentDataContainer().set(new NamespacedKey(CustomMobs.getPlugin(), "xp"), PersistentDataType.INTEGER, newXp);
                            p.setExp(setProgress(p, newXp, xpForNext));
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&aXP set to &d" + newXp));
                        } else {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou cant set xp more than xp for next level!"));
                        }
                    } catch (NumberFormatException e) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&cYou have entered an invalid number!"));
                    }
                    break;
                case "level":
                    try {
                        int level = Integer.parseInt(args[2]);
                        int xpForNext = LevelConfig.getConfig().getInt("level." + level);
                        p.getPersistentDataContainer().set(new NamespacedKey(CustomMobs.getPlugin(), "xpfornext"), PersistentDataType.INTEGER, xpForNext);
                        p.getPersistentDataContainer().set(new NamespacedKey(CustomMobs.getPlugin(), "level"), PersistentDataType.INTEGER, level);
                        p.getPersistentDataContainer().set(new NamespacedKey(CustomMobs.getPlugin(), "xp"), PersistentDataType.INTEGER, 0);
                        int xp = p.getPersistentDataContainer().get(new NamespacedKey(CustomMobs.getPlugin(), "xp"), PersistentDataType.INTEGER);
                        p.setExp(setProgress(p, xp, xpForNext));
                        p.setLevel(level);
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&aLevel set to &d" + level + "!"));
                    } catch (NumberFormatException e) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&cYou have entered an invalid number!"));
                    }
            }
        }
    }

    private float setProgress(Player p, int xp, int xpForNext){

        float progress = (float) xp / xpForNext;
        progress = Math.min(1.0f, Math.max(0.0f, progress));

        return progress;
    }

    private void showLevel(Player p) {
        int level = p.getPersistentDataContainer().get(new NamespacedKey(CustomMobs.getPlugin(), "level"), PersistentDataType.INTEGER);
        int xp = p.getPersistentDataContainer().get(new NamespacedKey(CustomMobs.getPlugin(), "xp"), PersistentDataType.INTEGER);
        int xpForNext = p.getPersistentDataContainer().get(new NamespacedKey(CustomMobs.getPlugin(), "xpfornext"), PersistentDataType.INTEGER);

        p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&cLevel : " + level));
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&aXP : " + xp + "/" + xpForNext));
    }
}
