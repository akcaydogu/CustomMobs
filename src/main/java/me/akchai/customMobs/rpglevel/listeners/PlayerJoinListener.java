package me.akchai.customMobs.rpglevel.listeners;

import me.akchai.customMobs.CustomMobs;
import me.akchai.customMobs.rpglevel.files.LevelConfig;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        PersistentDataContainer playerData = player.getPersistentDataContainer();

        int xpForNext = LevelConfig.getConfig().getInt("level.1");

        if (!(playerData.has(new NamespacedKey(CustomMobs.getPlugin(), "level"), PersistentDataType.INTEGER))); {
            playerData.set(new NamespacedKey(CustomMobs.getPlugin(), "level"), PersistentDataType.INTEGER, 1);
            playerData.set(new NamespacedKey(CustomMobs.getPlugin(), "xp"), PersistentDataType.INTEGER, 0);
            playerData.set(new NamespacedKey(CustomMobs.getPlugin(), "xpfornext"), PersistentDataType.INTEGER, xpForNext);
        }

    }

}
