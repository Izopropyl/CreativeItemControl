package net.kingidk.creativeItemControl.Listeners;

import net.kingidk.creativeItemControl.CreativeItemControl;
import net.kingidk.creativeItemControl.Handlers.EnchantmentHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

public class CreativeListener implements Listener {

    private final CreativeItemControl plugin;

    public CreativeListener(CreativeItemControl plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void  onCreativeInventory(InventoryCreativeEvent e) {
        if (!plugin.masterEnabled) {
            return;
        }
        if (e.getSlot() < 0) return;

        boolean inList = plugin.worlds.contains(e.getWhoClicked().getWorld().getName());
        if (plugin.worldsBlacklist == inList) {
        }

        if (e.getWhoClicked().hasPermission("cic.bypass")) return;


        // Setup Item Information
        ItemStack item = e.getCursor();
        Material itemType = item.getType();
        ItemMeta meta = item.getItemMeta();
        if (item == null || item.getType().isAir())  {
            return;
        }


        Player p = (Player) e.getWhoClicked();
        // Get default meta for item type
        ItemMeta def = new ItemStack(item.getType(), 1).getItemMeta();



        if (meta.equals(def)) return;

        if (!e.getWhoClicked().hasPermission("cic.bypass.enchantments")) {
            boolean attributeIssue = meta.getAttributeModifiers() != null;
            if (plugin.attributesEnabled && attributeIssue) {

                if (plugin.playerAlerts) {
                    p.sendMessage(Component.text("Items with attribute modifiers are not allowed here!", NamedTextColor.RED, TextDecoration.BOLD));
                }
                e.setCancelled(true);
                meta = item.getItemMeta();

                if (plugin.attributesAction.equals("REMOVE")) {
                    meta.setAttributeModifiers(null);
                    item.setItemMeta(meta);
                    p.getInventory().setItem(e.getSlot(), item);
                } else {
                    p.getInventory().setItem(e.getSlot(), null);
                    return;
                }
            }
        }



        if (!e.getWhoClicked().hasPermission("cic.bypass.attributes")) {
            if (plugin.enchantmentsEnabled
                    && !meta.getEnchants().equals(def.getEnchants())) {
                EnchantmentHandler handler = new EnchantmentHandler(plugin);
                switch (plugin.enchantmentsAction) {
                    case "DELETE": {
                        if (handler.shouldDelete(meta, item)) {
                            e.setCancelled(true);
                            p.getInventory().setItem(e.getSlot(), null);
                            if (plugin.playerAlerts) {
                                p.sendMessage(Component.text("Items with impossible enchantments are not allowed here!", NamedTextColor.RED, TextDecoration.BOLD));
                            }
                            return;
                        }
                        break;
                    }
                    case "LOWER", "REMOVE": {
                        e.setCancelled(true);
                        ItemMeta newMeta = handler.changeEnchants(meta, item);
                        item.setItemMeta(newMeta);
                        p.getInventory().setItem(e.getSlot(), item);
                        p.updateInventory();
                        if (plugin.playerAlerts) {
                            p.sendMessage(Component.text("Items with impossible enchantments are not allowed here!", NamedTextColor.RED, TextDecoration.BOLD));
                        }
                        }
                    case null, default:
                        break;
                }
            }
        }










    }
}
