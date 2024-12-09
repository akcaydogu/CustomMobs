package me.akchai.customMobs.utils;


import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class TabCompleter  implements org.bukkit.command.TabCompleter {


    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {


        if (command.getName().equalsIgnoreCase("custommobs")) {
            if (args.length == 1) return List.of("reload", "spawn", "list","add", "remove");
        }


        return null;
    }
}
