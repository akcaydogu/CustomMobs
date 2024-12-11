package me.akchai.customMobs.utils;


import me.akchai.customMobs.CustomMobs;
import me.akchai.customMobs.files.CustomMobsConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class TabCompleter  implements org.bukkit.command.TabCompleter {


    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {


        if (command.getName().equalsIgnoreCase("custommobs")) {
            if (args.length == 1) return List.of("reload", "spawn", "list","add", "remove", "spawnpoint", "spawnlist");
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("spawn") ) {
                    return new ArrayList<>(CustomMobsConfig.getConfig().getConfigurationSection("mobs").getKeys(false));
                }
            }
        }


        return null;
    }
}
