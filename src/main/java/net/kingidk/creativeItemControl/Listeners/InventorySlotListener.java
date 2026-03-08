package net.kingidk.creativeItemControl.Listeners;

import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import net.kingidk.creativeItemControl.CreativeItemControl;
import net.kingidk.creativeItemControl.Handlers.AttributeHandler;
import net.kingidk.creativeItemControl.Handlers.EnchantmentHandler;
import net.kingidk.creativeItemControl.Handlers.PotionHandler;
import net.kingidk.creativeItemControl.ItemCheckContext;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventorySlotListener implements Listener {
    private final CreativeItemControl plugin;
    private final AttributeHandler attributeHandler;
    private final PotionHandler potionHandler;
    private final EnchantmentHandler enchantmentHandler;

    public InventorySlotListener(CreativeItemControl plugin) {
        this.plugin = plugin;
        this.attributeHandler = new AttributeHandler(plugin);
        this.potionHandler = new PotionHandler(plugin);
        this.enchantmentHandler = new EnchantmentHandler(plugin);
    }
    @EventHandler
    public void onInventorySlotChange(PlayerInventorySlotChangeEvent e) {
        if (!plugin.masterEnabled) return;
        if (e.getSlot() < 0) return;
        if (!e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;

        boolean inList = plugin.worlds.contains(e.getPlayer().getWorld().getName());
        if (plugin.worldsBlacklist == inList) return;

        if (e.getPlayer().hasPermission("cic.bypass")) return;


        ItemStack item = e.getNewItemStack();
        if (item == null || item.getType().isAir()) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        Player p = e.getPlayer();



        if (meta.equals(plugin.getDefaultMeta(item.getType()))) return;

        ItemCheckContext ctx = new ItemCheckContext(p, item, meta, e.getSlot());

        attributeHandler.check(ctx);
        potionHandler.check(ctx);
        enchantmentHandler.check(ctx);

        if (ctx.isCancelled()) {
            p.getInventory().setItem(e.getSlot(), null);
        } else {
            item.setItemMeta(ctx.newItemMeta());
            p.getInventory().setItem(e.getSlot(), item);
        }

    }


}
