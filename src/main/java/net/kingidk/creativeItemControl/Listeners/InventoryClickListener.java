package net.kingidk.creativeItemControl.Listeners;

import net.kingidk.creativeItemControl.CreativeItemControl;
import net.kingidk.creativeItemControl.Handlers.AttributeHandler;
import net.kingidk.creativeItemControl.Handlers.ComponentHandler;
import net.kingidk.creativeItemControl.Handlers.EnchantmentHandler;
import net.kingidk.creativeItemControl.Handlers.PotionHandler;
import net.kingidk.creativeItemControl.ItemCheckContext;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryClickListener implements Listener {

    private final CreativeItemControl plugin;
    private final AttributeHandler attributeHandler;
    private final PotionHandler potionHandler;
    private final EnchantmentHandler enchantmentHandler;
    private final ComponentHandler componentHandler;


    public InventoryClickListener(CreativeItemControl plugin) {
        this.plugin = plugin;
        this.attributeHandler = new AttributeHandler(plugin);
        this.potionHandler = new PotionHandler(plugin);
        this.enchantmentHandler = new EnchantmentHandler(plugin);
        this.componentHandler = new ComponentHandler(plugin);
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!plugin.masterEnabled) return;
        if (e instanceof InventoryCreativeEvent) return;
        if (e.getSlot() < 0) return;
        if (!e.getWhoClicked().getGameMode().equals(GameMode.CREATIVE)) return;
        boolean inList = plugin.worlds.contains(e.getWhoClicked().getWorld().getName());
        if (plugin.worldsBlacklist == inList) return;

        if (e.getWhoClicked().hasPermission("cic.bypass")) return;







        ItemStack item = switch (e.getAction()) {
            case PICKUP_ALL, PICKUP_HALF, PICKUP_SOME, PICKUP_ONE, MOVE_TO_OTHER_INVENTORY,
                 HOTBAR_SWAP -> e.getCurrentItem();
            default -> null;
        };

        if (item == null || item.getType().isAir()) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        Player p = (Player) e.getWhoClicked();





        if (meta.equals(plugin.getDefaultMeta(item.getType()))) return;

        ItemCheckContext ctx = new ItemCheckContext(p, item, meta, e.getSlot());

        attributeHandler.check(ctx);
        potionHandler.check(ctx);
        enchantmentHandler.check(ctx);
        componentHandler.check(ctx);


        if (ctx.isCancelled()) {
            e.setCancelled(true);
        } else {
            e.getCurrentItem().setItemMeta(ctx.newItemMeta());
        }
    }
}
