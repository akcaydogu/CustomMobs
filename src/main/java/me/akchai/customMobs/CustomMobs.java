package me.akchai.customMobs;

import me.akchai.customMobs.customitems.files.CustomItemsConfig;
import me.akchai.customMobs.files.CustomMobsConfig;
import me.akchai.customMobs.files.SpawnPointConfig;
import me.akchai.customMobs.listeners.DamageListener;
import me.akchai.customMobs.listeners.EntityDeathListener;
import me.akchai.customMobs.listeners.EntityDropListener;
import me.akchai.customMobs.listeners.ProtectedDamageListener;
import me.akchai.customMobs.rpglevel.commands.LevelCommand;
import me.akchai.customMobs.rpglevel.files.LevelConfig;
import me.akchai.customMobs.rpglevel.listeners.LevelListener;
import me.akchai.customMobs.rpglevel.listeners.PlayerJoinListener;
import me.akchai.customMobs.rpglevel.utils.LevelTabCompleter;
import me.akchai.customMobs.utils.AutoSpawner;
import me.akchai.firstPlugin.files.ItemConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class CustomMobs extends JavaPlugin {

    private static CustomMobs plugin;

    public static String prefix = "&8[&cCustomMobs&8]";

    @Override
    public void onEnable() {

        plugin = this;
        getLogger().info("Plugin enabled");

        this.saveDefaultConfig();


        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        //Levels config
        LevelConfig.setup();
        LevelConfig.getConfig().addDefault("level.1", 100);
        LevelConfig.getConfig().addDefault("level.2", 200);
        LevelConfig.getConfig().addDefault("level.3", 300);
        LevelConfig.getConfig().addDefault("level.4", 400);
        LevelConfig.getConfig().options().copyDefaults(true);
        LevelConfig.saveConfig();

        //CustomItems config
        List<String> list = new ArrayList<>();
        list.add("&cExampleSword");
        list.add("&cExampleLine");
        List<String> list2 = new ArrayList<>();
        list2.add("Sharpness-10");
        list2.add("Unbreaking-20");
        List<String> list3 = new ArrayList<>();
        list3.add("MAX_HEALTH-5");
        list3.add("ATTACK_DAMAGE-5");

        CustomItemsConfig.setup();
        CustomItemsConfig.getConfig().addDefault("items.ExampleSword.item", "DIAMOND_SWORD");
        CustomItemsConfig.getConfig().addDefault("items.ExampleSword.amount", 1);
        CustomItemsConfig.getConfig().addDefault("items.ExampleSword.name", "&aExampleSword");
        CustomItemsConfig.getConfig().addDefault("items.ExampleSword.lore", list);
        CustomItemsConfig.getConfig().addDefault("items.ExampleSword.enchantments", list2);
        CustomItemsConfig.getConfig().addDefault("items.ExampleSword.attributes.hideEnchant", true);
        CustomItemsConfig.getConfig().addDefault("items.ExampleSword.attributes.hideAttributes", false);
        CustomItemsConfig.getConfig().addDefault("items.ExampleSword.attributes.attribute", list3);
        CustomItemsConfig.getConfig().addDefault("items.ExampleSword.attributes.slot", "hand");
        CustomItemsConfig.getConfig().options().copyDefaults(true);
        CustomItemsConfig.saveConfig();


        //Mob config
        CustomMobsConfig.setup();
        CustomMobsConfig.getConfig().addDefault("mobs.ExampleMob.name", "ExampleMob");
        CustomMobsConfig.getConfig().addDefault("mobs.ExampleMob.entity", "ZOMBIE");
        CustomMobsConfig.getConfig().addDefault("mobs.ExampleMob.health", 20);
        CustomMobsConfig.getConfig().addDefault("mobs.ExampleMob.damage", 5);
        CustomMobsConfig.getConfig().addDefault("mobs.ExampleMob.speed", 0.2);
        CustomMobsConfig.getConfig().addDefault("mobs.ExampleMob.drops", new String[]{"sword 1 %5", "sword 1 %5"});
        CustomMobsConfig.getConfig().options().copyDefaults(true);
        CustomMobsConfig.saveConfig();

        SpawnPointConfig.setup();
        SpawnPointConfig.saveConfig();


        //Commands CustomMobs
        getCommand("custommobs").setExecutor(new me.akchai.customMobs.commands.CustomMobsCommand());
        getCommand("custommobs").setTabCompleter(new me.akchai.customMobs.utils.TabCompleter());

        //Listeners
        getServer().getPluginManager().registerEvents(new DamageListener(), this);
        getServer().getPluginManager().registerEvents(new EntityDeathListener(), this);
        getServer().getPluginManager().registerEvents(new EntityDropListener(), this);
        getServer().getPluginManager().registerEvents(new ProtectedDamageListener(), this);

        loadSpawnPoints();

        //Level System
        //Commands
        getCommand("level").setExecutor(new LevelCommand());
        getCommand("level").setTabCompleter(new LevelTabCompleter());

        //Listeners
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new LevelListener(), this);

    }

    public static CustomMobs getPlugin() {
        return plugin;
    }

    private void loadSpawnPoints() {
        if (SpawnPointConfig.getConfig().contains("spawnpoints")) {

            for (String key : SpawnPointConfig.getConfig().getConfigurationSection("spawnpoints").getKeys(false)) {
                String mobName = SpawnPointConfig.getConfig().getString("spawnpoints." + key + ".mob");
                int interval = SpawnPointConfig.getConfig().getInt("spawnpoints." + key + ".interval");


                String worldName = SpawnPointConfig.getConfig().getString("spawnpoints." + key + ".location.world");
                double x = SpawnPointConfig.getConfig().getDouble("spawnpoints." + key + ".location.x");
                double y = SpawnPointConfig.getConfig().getDouble("spawnpoints." + key + ".location.y");
                double z = SpawnPointConfig.getConfig().getDouble("spawnpoints." + key + ".location.z");

                World world = getServer().getWorld(worldName);
                if (world == null) {
                    getLogger().warning("Spawn point " + key + " için dünya bulunamadı: " + worldName);
                    continue;
                }

                Location location = new Location(world, x, y, z);
                new AutoSpawner(key).runTaskTimer(plugin, 0, interval * 20L); // interval saniye cinsinden.
            }
        }
    }

    @Override
    public void onDisable() {
        AutoSpawner.clearLastSpawnedEntities();
        Bukkit.getScheduler().cancelTasks(this);
        getLogger().info("Plugin disabled");
    }

}
