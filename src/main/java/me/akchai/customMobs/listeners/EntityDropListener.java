package me.akchai.customMobs.listeners;

import me.akchai.customMobs.CustomMobs;
import me.akchai.customMobs.files.CustomMobsConfig;
import me.akchai.firstPlugin.commands.ItemListCommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class EntityDropListener implements Listener {

    public String prefix = CustomMobs.prefix;

    private void loadDropTable(EntityDeathEvent e, Map<ItemStack, Double> dropTable){
        for (String mobID : CustomMobsConfig.getConfig().getConfigurationSection("mobs").getKeys(false)){
            if (e.getEntity().getCustomName().contains(ChatColor.translateAlternateColorCodes('&', CustomMobsConfig.getConfig().getString("mobs."+mobID+".name"))))
            {
                e.getDrops().clear();
                try{
                    for (String drop : CustomMobsConfig.getConfig().getStringList("mobs."+mobID+".drops")){
                        String dropID = drop.split(" ")[0];
                        int dropAmount = Integer.parseInt(drop.split(" ")[1].split("%")[0]);
                        String chance = drop.substring(drop.indexOf("%")+1);
                        if (ItemListCommand.getItem(e.getEntity().getKiller(), dropID) != null) {
                            ItemStack item = ItemListCommand.getItem(e.getEntity().getKiller(), dropID);
                            item.setAmount(dropAmount);
                            dropTable.put(item, Double.parseDouble(chance));
                        } else if (Material.matchMaterial(dropID) != null) {
                            ItemStack item = new ItemStack(Material.matchMaterial(dropID), dropAmount);
                            dropTable.put(item, Double.parseDouble(chance));
                        }
                    }
                } catch (Exception ex){
                    e.getEntity().getKiller().sendMessage(ex.getMessage());
                }
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e){
        if (e.getEntity().getKiller() instanceof Player p){
            Map<ItemStack, Double> dropTable = new HashMap<>();

            loadDropTable(e, dropTable);

            if (dropTable.isEmpty()) {
                return;
            }

            for (Map.Entry<ItemStack, Double> entry : dropTable.entrySet()) {
                double chance = entry.getValue();
                ItemStack item = entry.getKey();
                double random = new Random().nextDouble()*100;
                if (random <= chance) {
                    e.getDrops().add(item);
                    p.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', prefix+"&c"+p.getDisplayName() + " " + item.getAmount() + " adet " + (item.hasItemMeta() ? item.getItemMeta().getDisplayName() : item.getType().toString().toLowerCase()) + " düşürdü."));
                    break;
                }
            }
        }
    }


}
