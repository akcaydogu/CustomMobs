package me.akchai.customMobs.rpglevel.files;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class LevelConfig {

    private static File file;
    private static FileConfiguration config;

    public static void setup(){

        file = new File(Bukkit.getServer().getPluginManager().getPlugin("CustomMobs").getDataFolder(), "level/levels.yml");

        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration getConfig(){
        return config;
    }

    public static void saveConfig(){
        try {
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void reloadConfig(){
        config = YamlConfiguration.loadConfiguration(file);
    }


}
