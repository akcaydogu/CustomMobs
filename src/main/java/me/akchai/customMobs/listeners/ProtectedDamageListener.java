package me.akchai.customMobs.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import static me.akchai.customMobs.CustomMobs.prefix;

public class ProtectedDamageListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        try {
            if (event.getDamager() instanceof Player p) {
                // Check if the player is in the "void" world
                if (p.getWorld().getName().equalsIgnoreCase("void")) {
                    // Define the coordinates for the specific area in the "void" world
                    double minX = 17, maxX = 25; // Replace with your area's min and max X coordinates
                    double minY = 64, maxY = 80;   // Replace with your area's min and max Y coordinates
                    double minZ = -5, maxZ = 3; // Replace with your area's min and max Z coordinates

                    // Get the player's location
                    Location playerLocation = p.getLocation();

                    // Check if the player is within the specific area in the "void" world
                    if (playerLocation.getX() >= minX && playerLocation.getX() <= maxX &&
                            playerLocation.getY() >= minY && playerLocation.getY() <= maxY &&
                            playerLocation.getZ() >= minZ && playerLocation.getZ() <= maxZ) {
                        // Cancel the event if the player is in the area
                        event.setCancelled(true);
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&cBuradan vuramazsınız"));
                    }
                }
            }
        } catch (Exception e) {
            event.getEntity().sendMessage(e.getMessage());
        }
    }

}
