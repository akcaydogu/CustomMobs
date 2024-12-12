package me.akchai.customMobs.rpgparty.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PartyCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {


        if (commandSender instanceof Player p) {
            if (args.length == 0) {
                partyArgError(p);
            }
            if (args.length == 1) {
                switch (args[0]) {
                    case "list":
                        showPartyList(p);
                        break;
                    case "remove":

                        break;
                    default:
                        partyAddPlayer(p, args),
                }
            }
        }



        return true;
    }

    private void partyAddPlayer(Player p, String[] args) {
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target != null) {
            p.sendMessage(target.getDisplayName() + " added to party");

        } else {
            p.sendMessage(target.getDisplayName() + " is not online");
        }
    }

    private void showPartyList(Player p) {

    }

    private void partyArgError(Player p) {
        StringBuilder error = new StringBuilder();
        error.append("/party <player> sends an invite to a player\n");
        error.append("/party list shows player list of your party\n");
        error.append("/party remove <player> removes a player from your party\n");
        p.sendMessage(error.toString());
    }

}
