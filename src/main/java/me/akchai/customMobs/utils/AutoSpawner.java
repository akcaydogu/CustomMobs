package me.akchai.customMobs.utils;


import me.akchai.customMobs.commands.CustomMobsCommand;
import me.akchai.customMobs.files.CustomMobsConfig;
import me.akchai.customMobs.files.SpawnPointConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

import static me.akchai.customMobs.CustomMobs.prefix;

public class AutoSpawner extends BukkitRunnable {

    private static final Map<String, Entity> lastSpawnedEntities = new HashMap<>();
    private final String spawnPointKey;

    public AutoSpawner(String spawnPointKey) {
        this.spawnPointKey = spawnPointKey;
    }

    @Override
    public void run() {
        ConfigurationSection spawnPoint = SpawnPointConfig.getConfig().getConfigurationSection("spawnpoints." + spawnPointKey);

        if (spawnPoint == null) {
            Bukkit.getLogger().warning("Spawn point configuration is missing for key: " + spawnPointKey);
            cancel();
            return;
        }

        Entity lastSpawned = lastSpawnedEntities.get(spawnPointKey);
        if (lastSpawned != null && !lastSpawned.isDead()) {
            // Last spawned mob is still alive; don't spawn a new one
            return;
        }

        String mobName = spawnPoint.getString("mob");
        int interval = spawnPoint.getInt("interval");

        World world = Bukkit.getWorld(spawnPoint.getString("location.world"));
        double x = spawnPoint.getDouble("location.x");
        double y = spawnPoint.getDouble("location.y");
        double z = spawnPoint.getDouble("location.z");
        Location loc = new Location(world, x, y, z);

        Player p = Bukkit.getPlayer("Akchai");
//        Player p = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
        if (p == null) {
            Bukkit.getLogger().warning("No online player available for spawning mobs.");
            return;
        }

        Entity entity = CustomMobsCommand.getInstance().spawnMob(p, mobName, loc);
        if (entity == null) {
            Bukkit.getLogger().warning("Failed to spawn mob: " + mobName + " at spawn point: " + spawnPointKey);
        } else {
            p.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', prefix + " " + entity.getCustomName().substring(0, entity.getCustomName().lastIndexOf(" ")) + " &aDoğdu"));
            lastSpawnedEntities.put(spawnPointKey, entity);
            Bukkit.getLogger().info("Successfully spawned mob: " + mobName + " at location: " + loc.toString());
        }
    }
    
    public static void clearLastSpawnedEntities() {
        lastSpawnedEntities.clear();
    }
}
