package me.akchai.customMobs;

import me.akchai.customMobs.files.CustomMobsConfig;
import me.akchai.customMobs.listeners.DamageListener;
import me.akchai.customMobs.listeners.EntityDeathListener;
import me.akchai.customMobs.listeners.EntityDropListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class CustomMobs extends JavaPlugin {

    public static String prefix = "&8[&cCustomMobs&8] ";

    @Override
    public void onEnable() {
        getLogger().info("Plugin enabled");

        this.saveDefaultConfig();

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        CustomMobsConfig.setup();
        CustomMobsConfig.getConfig().addDefault("mobs.ExampleMob.name", "ExampleMob");
        CustomMobsConfig.getConfig().addDefault("mobs.ExampleMob.entity", "ZOMBIE");
        CustomMobsConfig.getConfig().addDefault("mobs.ExampleMob.health", 20);
        CustomMobsConfig.getConfig().addDefault("mobs.ExampleMob.damage", 5);
        CustomMobsConfig.getConfig().addDefault("mobs.ExampleMob.speed", 0.2);
        CustomMobsConfig.getConfig().addDefault("mobs.ExampleMob.drops", new String[]{"sword 1 %5", "sword 1 %5"});
        CustomMobsConfig.getConfig().options().copyDefaults(true);
        CustomMobsConfig.saveConfig();


        getCommand("custommobs").setExecutor(new me.akchai.customMobs.commands.CustomMobsCommand());
        getCommand("custommobs").setTabCompleter(new me.akchai.customMobs.utils.TabCompleter());

        getServer().getPluginManager().registerEvents(new DamageListener(), this);

        getServer().getPluginManager().registerEvents(new EntityDeathListener(), this);

        getServer().getPluginManager().registerEvents(new EntityDropListener(), this);
    }

}
