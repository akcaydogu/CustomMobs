package me.akchai.customMobs.rpglevel.utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LevelTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {

        if (commandSender instanceof Player p) {
            if (args.length == 1) return List.of("reload","add","set");
            if (args.length == 2 && args[0].equals("set")) {
                return List.of("level","xp");
            } else if (args.length == 2 && args[0].equals("add")) {
                return List.of("level","xp");
            }
            if (args.length == 3 && (args[0].equals("add") || args[0].equals("set"))) {
                return List.of("10");
            }
        }

        return List.of();
    }
}
