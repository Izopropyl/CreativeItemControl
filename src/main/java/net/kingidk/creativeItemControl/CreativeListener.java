package net.kingidk.creativeItemControl;

import net.kingidk.creativeItemControl.Handlers.EnchantmentHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

public class    CreativeListener implements Listener {

    private final CreativeItemControl plugin;

    public CreativeListener(CreativeItemControl plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void  onCreativeInventory(InventoryCreativeEvent e) {
        if (!plugin.getConfig().getBoolean("toggles.master")) return;
        if (e.getSlot() < 0) return;

        List<String> worlds = plugin.getConfig().getStringList("config.worlds");
        boolean isBlacklist = plugin.getConfig().getBoolean("config.blacklist");
        boolean inList = worlds.contains(e.getWhoClicked().getWorld().getName());
        if (isBlacklist == inList) return;


        // Setup Item Information
        ItemStack item = e.getCursor();
        if (item == null || item.getType().isAir()) return;
        ItemMeta meta = item.getItemMeta();
        Player p = (Player) e.getWhoClicked();
        // Get default meta for item type
        ItemMeta def = new ItemStack(item.getType(), 1).getItemMeta();

        if (meta.equals(def)) return;

        boolean attributeIssue = meta.getAttributeModifiers() != null;
        if (plugin.getConfig().getBoolean("toggles.attributes") && attributeIssue) {
            p.sendMessage(Component.text("Items with attribute modifiers are not allowed here!", NamedTextColor.RED, TextDecoration.BOLD));
            e.setCancelled(true);
            meta = item.getItemMeta();

            if (Objects.requireNonNull(plugin.getConfig().getString("attributes.action")).equalsIgnoreCase("REMOVE")) {
                meta.setAttributeModifiers(null);
                item.setItemMeta(meta);
                p.getInventory().setItem(e.getSlot(), item);
            } else {
                p.getInventory().setItem(e.getSlot(), null);
                return;
            }
        }

        if (plugin.getConfig().getBoolean("toggles.enchantments")
                && !meta.getEnchants().equals(def.getEnchants())) {
            EnchantmentHandler handler = new EnchantmentHandler(plugin);
            switch (plugin.getConfig().getString("enchantments.action")) {
                case "DELETE": {
                    if (handler.shouldDelete(meta, item, p)) {
                        e.setCancelled(true);
                        p.getInventory().setItem(e.getSlot(), null);
                        p.sendMessage(Component.text("Items with impossible enchantments are not allowed here!", NamedTextColor.RED, TextDecoration.BOLD));
                        return;
                    }
                    break;
                }
                case "LOWER", "REMOVE": {
                    e.setCancelled(true);
                    ItemMeta newMeta = handler.changeEnchants(meta, item, p);
                    item.setItemMeta(newMeta);
                    p.getInventory().setItem(e.getSlot(), item);
                    p.updateInventory();
                    p.sendMessage(Component.text("Items with impossible enchantments are not allowed here!", NamedTextColor.RED, TextDecoration.BOLD));
                }
                case null, default:
                    break;
            }
        }







    }
}
