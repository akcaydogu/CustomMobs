package me.akchai.customMobs.listeners;

import me.akchai.customMobs.CustomMobs;
import me.akchai.customMobs.files.CustomMobsConfig;


import me.akchai.firstPlugin.commands.ItemListCommand;


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class EntityDropListener implements Listener {

    public String prefix = CustomMobs.prefix;

    private void loadDropTable(EntityDeathEvent e, Map<ItemStack, Double> dropTable, boolean conti){
        for (String mobID : CustomMobsConfig.getConfig().getConfigurationSection("mobs").getKeys(false)){
            if (ChatColor.translateAlternateColorCodes('&', e.getEntity().getCustomName()).contains(ChatColor.translateAlternateColorCodes('&', CustomMobsConfig.getConfig().getString("mobs."+mobID+".name"))))
            {
                e.getDrops().clear();
                try{
                    for (String drop : CustomMobsConfig.getConfig().getStringList("mobs."+mobID+".drops")){
                        String dropID = drop.split(" ")[0];
                        int dropAmount = Integer.parseInt(drop.split(" ")[1].split("%")[0]);
                        String chance = drop.substring(drop.indexOf("%")+1, drop.lastIndexOf(" "));
                        conti = Boolean.parseBoolean(drop.substring(drop.lastIndexOf(" ")+1));
                        if (ItemListCommand.getItem(e.getEntity().getKiller(), dropID) != null) {
                            ItemStack item = ItemListCommand.getItem(e.getEntity().getKiller(), dropID);
                            item.setAmount(dropAmount);
                            dropTable.put(item, Double.parseDouble(chance));

                        } else if (Material.matchMaterial(dropID) != null) {
                            ItemStack item = new ItemStack(Material.matchMaterial(dropID), dropAmount);
                            dropTable.put(item, Double.parseDouble(chance));
                        } else if(Enchantment.getByName(dropID) != null){
                            ItemStack item = new ItemStack(Material.ENCHANTED_BOOK, dropAmount);
                            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
                            meta.addStoredEnchant(Enchantment.getByName(dropID), 1, true);
                            item.setItemMeta(meta);
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
        if (e.getEntity().getKiller() instanceof Player p && e.getEntity().getCustomName() != null){
            Map<ItemStack, Double> dropTable = new HashMap<>();
            boolean conti = true;

            loadDropTable(e, dropTable, conti);

            if (dropTable.isEmpty()) {
                return;
            }

            for (Map.Entry<ItemStack, Double> entry : dropTable.entrySet()) {
                double chance = entry.getValue();
                ItemStack item = entry.getKey();
                double random = new Random().nextDouble()*100;
                if(p.isOp()){
                    p.sendMessage(random + " " + item.getItemMeta().getDisplayName()+ " " + chance);
                }
                if (random <= chance) {
                    e.getDrops().add(item);
                    p.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', prefix+"&c"+p.getDisplayName() + " " + item.getAmount() + " adet " + (item.hasItemMeta() ? item.getItemMeta().getDisplayName() : item.getType().toString().toLowerCase()) + " düşürdü."));
                    if (!conti) {
                        break;
                    }
                }
            }
        }
    }


}
