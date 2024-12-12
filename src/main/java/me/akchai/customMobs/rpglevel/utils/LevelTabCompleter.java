package me.akchai.customMobs.rpglevel.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LevelTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {

        List<String> playerNames = new ArrayList<>();
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            playerNames.add(player.getName()); // Use getName() for player usernames
        }

        // Handle first argument: "reload", "add", or "set"
        if (args.length == 1) {
            return List.of("reload", "add", "set");
        }

        // Handle second argument: "level" or "xp" for "set" or "add"
        if (args.length == 2 && (args[0].equals("set") || args[0].equals("add") || args[0].equals("remove"))) {
            return List.of("level", "xp");
        }

        // Handle third argument: Set fixed value "10" for "set" or "add"
        if (args.length == 3 && (args[0].equals("set") || args[0].equals("add") || args[0].equals("remove"))) {
            return List.of("10");
        }

        // Handle fourth argument: List of online players for "add"
        if (args.length == 4 && (args[0].equals("add") || args[0].equals("set") || args[0].equals("remove"))) {
            return playerNames; // Return pre-fetched player names
        }

        return List.of();
    }
}
