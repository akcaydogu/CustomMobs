package me.akchai.customMobs.rpglevel.commands;

import me.akchai.customMobs.CustomMobs;
import me.akchai.customMobs.rpglevel.files.LevelConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataType;

import static me.akchai.customMobs.CustomMobs.prefix;

public class LevelCommand implements CommandExecutor {

    private final NamespacedKey xpKey = new NamespacedKey(CustomMobs.getPlugin(), "xp");
    private final NamespacedKey levelKey = new NamespacedKey(CustomMobs.getPlugin(), "level");
    private final NamespacedKey xpForNextKey = new NamespacedKey(CustomMobs.getPlugin(), "xpfornext");


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
                        setCommand(p, args);
                        return true;
                    case "add":
                        addCommand(p, args);
                        return true;
                    case "remove":
                        removeCommand(p, args);
                        return true;
                    case "reload":
                        LevelConfig.reloadConfig();
                        return true;
                    case "list":
                        showLevelList(p);
                        return true;
                }
            } else {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&cYou don't have permission to use this command!"));
            }

        }
        return true;
    }

    //Check remove functions
    private void removeCommand(Player p, String[] args) {
        if (args.length == 3 || args.length == 4) {
            switch (args[1]) {
                case "level":
                    removeLevel(p, args);
                    break;
                case "xp":
                    removeXP(p, args);
                    break;
            }
        }
    }

    private void removeLevel(Player p, String[] args) {
        try {
            int minusLevel = Integer.parseInt(args[2]);
            if (minusLevel <= 0) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&cLevel must be greater than 0!"));
                return;
            }
            if (args.length == 4) {
                Player target = Bukkit.getPlayerExact(args[3]);
                if (target != null) {
                    int level = target.getPersistentDataContainer().get(levelKey, PersistentDataType.INTEGER);
                    if (level-minusLevel < 0) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&cYou can not set player's level below zero!"));
                        return;
                    }
                    level -= minusLevel;
                    target.getPersistentDataContainer().set(levelKey, PersistentDataType.INTEGER, level);
                    target.getPersistentDataContainer().set(xpKey, PersistentDataType.INTEGER, 0);
                    int nextForLevel = LevelConfig.getConfig().getInt("level." + level);
                    target.getPersistentDataContainer().set(xpForNextKey, PersistentDataType.INTEGER, nextForLevel);

                    target.setLevel(level);
                    target.setExp(setProgress(0, nextForLevel));

                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + minusLevel + "&aLevel removed from " + target.getDisplayName() + "!"));
                } else {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&cThat player is not online!"));
                }
            } else if (args.length == 3) {
                int level = p.getPersistentDataContainer().get(levelKey, PersistentDataType.INTEGER);
                if (level-minusLevel < 0) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&cYou can not set player's level below zero!"));
                    return;
                }

                level -= minusLevel;
                p.getPersistentDataContainer().set(levelKey, PersistentDataType.INTEGER, level);
                int nextForLevel = LevelConfig.getConfig().getInt("level." + level);

                p.getPersistentDataContainer().set(xpKey, PersistentDataType.INTEGER, 0);
                p.getPersistentDataContainer().set(xpForNextKey, PersistentDataType.INTEGER, nextForLevel);

                p.setLevel(level);
                p.setExp(setProgress(0, nextForLevel));

                p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + minusLevel + "&aLevel removed from " + p.getDisplayName() + "!"));
            }

        } catch (NumberFormatException e) {
            p.sendMessage(ChatColor.RED + "Invalid number!");
        }
    }

    private void removeXP(Player p, String[] args) {
        try {
            int minusXP= Integer.parseInt(args[2]);

            if (args.length == 4) {
                Player target = Bukkit.getPlayerExact(args[3]);
                if (target != null) {
                    int level = target.getPersistentDataContainer().get(levelKey, PersistentDataType.INTEGER);
                    int xp = target.getPersistentDataContainer().get(xpKey, PersistentDataType.INTEGER);

                    int xpForNext = target.getPersistentDataContainer().get(xpForNextKey, PersistentDataType.INTEGER);

                    if (minusXP <= xp) {
                        xp -= minusXP;
                    }

                    while(minusXP > xp) {
                        level--;
                        xpForNext = LevelConfig.getConfig().getInt("level." + level);
                        xp += xpForNext;
                        xp -= minusXP;
                    }

                    target.getPersistentDataContainer().set(xpKey, PersistentDataType.INTEGER, xp);
                    target.getPersistentDataContainer().set(levelKey, PersistentDataType.INTEGER, level);
                    target.setLevel(level);
                    target.setExp(setProgress(xp, xpForNext));
                } else {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&cThat player is not online!"));
                }
            } else if (args.length == 3) {
                int level = p.getPersistentDataContainer().get(levelKey, PersistentDataType.INTEGER);
                int xp = p.getPersistentDataContainer().get(xpKey, PersistentDataType.INTEGER);

                int xpForNext = p.getPersistentDataContainer().get(xpForNextKey, PersistentDataType.INTEGER);

                if (minusXP <= xp) {
                    xp -= minusXP;
                }

                while(minusXP > xp) {
                    level--;
                    xpForNext = LevelConfig.getConfig().getInt("level." + level);
                    xp += xpForNext;
                    xp -= minusXP;
                }

                p.getPersistentDataContainer().set(xpKey, PersistentDataType.INTEGER, xp);
                p.getPersistentDataContainer().set(levelKey, PersistentDataType.INTEGER, level);
                p.setLevel(level);
                p.setExp(setProgress(xp, xpForNext));
            }

        } catch (NumberFormatException e) {
            p.sendMessage(ChatColor.RED + "Invalid number!");
        }
    }

    private void addCommand(Player p, String[] args) {
        if (args.length == 3 || args.length == 4) {
            switch (args[1]) {
                case "level":
                    addLevel(p, args);
                    break;
                case "xp":
                    addXP(p, args);
                    break;
            }
        } else {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + " &cCorrect usage /level add <xp/level> <amount> <player>"));
        }
    }

    private void addLevel(Player p, String[] args) {
        try {

            int addLevel = Integer.parseInt(args[2]);
            if (args.length == 4) {
                Player target = Bukkit.getPlayerExact(args[3]);

                if (target == null) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&cYou have entered an invalid/offline player!"));
                    return;
                }

                int level = target.getPersistentDataContainer().get(levelKey, PersistentDataType.INTEGER);
                level += addLevel;

                target.getPersistentDataContainer().set(levelKey, PersistentDataType.INTEGER, level);
                int xpForNextV = LevelConfig.getConfig().getInt("level." + level);
                target.getPersistentDataContainer().set(xpForNextKey, PersistentDataType.INTEGER, xpForNextV);

                int xp = target.getPersistentDataContainer().get(xpKey, PersistentDataType.INTEGER);

                target.setExp(setProgress(xp, xpForNextV));
                target.setLevel(level);

                target.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + addLevel + "&aLevel added to " + target.getDisplayName() + " new level : " + level + "!"));
            } else {
                int level = p.getPersistentDataContainer().get(levelKey, PersistentDataType.INTEGER);
                level += addLevel;

                p.getPersistentDataContainer().set(levelKey, PersistentDataType.INTEGER, level);
                int xpForNextV = LevelConfig.getConfig().getInt("level." + level);
                p.getPersistentDataContainer().set(xpForNextKey, PersistentDataType.INTEGER, xpForNextV);

                int xp = p.getPersistentDataContainer().get(xpKey, PersistentDataType.INTEGER);

                p.setExp(setProgress(xp, xpForNextV));
                p.setLevel(level);

                p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + addLevel + "&aLevel added new level : " + level + "!"));
            }
        } catch (NumberFormatException e) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&cYou have entered an invalid number!"));
        }
    }

    private void addXP(Player p, String[] args) {
        try {
            if (args.length == 4) {
                Player target = Bukkit.getPlayerExact(args[3]);

                if (target == null) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&cYou have entered an invalid/offline player!"));
                    return;
                }

                int addXp = Integer.parseInt(args[2]);
                int level = target.getPersistentDataContainer().get(levelKey, PersistentDataType.INTEGER);
                int xp = target.getPersistentDataContainer().get(xpKey, PersistentDataType.INTEGER);

                xp += addXp;
                int xpForNext = target.getPersistentDataContainer().get(xpForNextKey, PersistentDataType.INTEGER);

                while(xp > xpForNext) {
                    xp -= xpForNext;
                    level++;
                    xpForNext = LevelConfig.getConfig().getInt("level." + level);
                }

                target.getPersistentDataContainer().set(xpKey, PersistentDataType.INTEGER, xp);
                target.getPersistentDataContainer().set(xpForNextKey, PersistentDataType.INTEGER, xpForNext);
                target.getPersistentDataContainer().set(levelKey, PersistentDataType.INTEGER, level);

                target.setLevel(level);
                target.setExp(setProgress(xp, xpForNext));

                target.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix +" &d"+ addXp + "&aXP added to " + target.getDisplayName() + " new level : " + level + "!"));
            } else {
                int addXp = Integer.parseInt(args[2]);
                int level = p.getPersistentDataContainer().get(levelKey, PersistentDataType.INTEGER);
                int xp = p.getPersistentDataContainer().get(xpKey, PersistentDataType.INTEGER);

                xp += addXp;
                int xpForNext = p.getPersistentDataContainer().get(xpForNextKey, PersistentDataType.INTEGER);

                while(xp > xpForNext) {
                    xp -= xpForNext;
                    level++;
                    xpForNext = LevelConfig.getConfig().getInt("level." + level);
                }

                p.getPersistentDataContainer().set(xpKey, PersistentDataType.INTEGER, xp);
                p.getPersistentDataContainer().set(xpForNextKey, PersistentDataType.INTEGER, xpForNext);
                p.getPersistentDataContainer().set(levelKey, PersistentDataType.INTEGER, level);

                p.setLevel(level);
                p.setExp(setProgress(xp, xpForNext));
            }



        } catch (NumberFormatException e) {

        }
    }

    private void setCommand(Player p, String[] args) {
        if (args.length == 3 || args.length == 4) {
            switch (args[1]) {
                case "xp":
                    setXP(p, args);
                    break;
                case "level":
                    setLevel(p, args);
                    break;
            }
        } else {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + " &cCorrect usage /level set <xp/level> <amount> <player>"));
        }
    }

    private void setXP(Player p, String[] args) {
        try {

            if (args.length == 4) {
                Player target = Bukkit.getPlayerExact(args[3]);
                if (target == null) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&cYou have entered an invalid/offline player!"));
                    return;
                }
                int xpForNext = target.getPersistentDataContainer().get(xpForNextKey, PersistentDataType.INTEGER);
                int newXp = Integer.parseInt(args[2]);
                if (xpForNext > newXp) {
                    target.getPersistentDataContainer().set(xpKey, PersistentDataType.INTEGER, newXp);
                    target.setExp(setProgress(newXp, xpForNext));
                    target.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&aXP of " + target.getDisplayName() + " set to &d" + newXp));
                } else {
                    target.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou cant set xp more than xp for next level!"));
                }
            } else {
                int xpForNext = p.getPersistentDataContainer().get(xpForNextKey, PersistentDataType.INTEGER);
                int newXp = Integer.parseInt(args[2]);
                if (xpForNext > newXp) {
                    p.getPersistentDataContainer().set(xpKey, PersistentDataType.INTEGER, newXp);
                    p.setExp(setProgress(newXp, xpForNext));
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&aXP set to &d" + newXp));
                } else {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou cant set xp more than xp for next level!"));
                }
            }



        } catch (NumberFormatException e) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&cYou have entered an invalid number!"));
        }
    }

    private void setLevel(Player p, String[] args) {
        try {

            if (args.length == 4) {
                Player target = Bukkit.getPlayerExact(args[3]);
                if (target == null) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&cYou have entered an invalid/offline player!"));
                    return;
                }
                int level = Integer.parseInt(args[2]);
                int xpForNext = LevelConfig.getConfig().getInt("level." + level);
                target.getPersistentDataContainer().set(new NamespacedKey(CustomMobs.getPlugin(), "xpfornext"), PersistentDataType.INTEGER, xpForNext);
                target.getPersistentDataContainer().set(new NamespacedKey(CustomMobs.getPlugin(), "level"), PersistentDataType.INTEGER, level);
                target.getPersistentDataContainer().set(new NamespacedKey(CustomMobs.getPlugin(), "xp"), PersistentDataType.INTEGER, 0);
                int xp = p.getPersistentDataContainer().get(new NamespacedKey(CustomMobs.getPlugin(), "xp"), PersistentDataType.INTEGER);
                target.setExp(setProgress(xp, xpForNext));
                target.setLevel(level);
                target.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&aLevel of " + target.getDisplayName() + " set to &d" + level + "!"));
            } else {
                int level = Integer.parseInt(args[2]);
                int xpForNext = LevelConfig.getConfig().getInt("level." + level);
                p.getPersistentDataContainer().set(new NamespacedKey(CustomMobs.getPlugin(), "xpfornext"), PersistentDataType.INTEGER, xpForNext);
                p.getPersistentDataContainer().set(new NamespacedKey(CustomMobs.getPlugin(), "level"), PersistentDataType.INTEGER, level);
                p.getPersistentDataContainer().set(new NamespacedKey(CustomMobs.getPlugin(), "xp"), PersistentDataType.INTEGER, 0);
                int xp = p.getPersistentDataContainer().get(new NamespacedKey(CustomMobs.getPlugin(), "xp"), PersistentDataType.INTEGER);
                p.setExp(setProgress(xp, xpForNext));
                p.setLevel(level);
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&aLevel set to &d" + level + "!"));
            }
        } catch (NumberFormatException e) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&cYou have entered an invalid number!"));
        }
    }

    private float setProgress(int xp, int xpForNext){

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

    private void showLevelList(Player p) {

        for (String x : LevelConfig.getConfig().getConfigurationSection("level").getKeys(false)){
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&aLevel " + x + " : " + LevelConfig.getConfig().getConfigurationSection("level").get(x)));
        }

    }
}

