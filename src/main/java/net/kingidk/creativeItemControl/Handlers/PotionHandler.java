package net.kingidk.creativeItemControl.Handlers;

import net.kingidk.creativeItemControl.CreativeItemControl;
import net.kingidk.creativeItemControl.ItemCheckContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.inventory.meta.PotionMeta;

public class PotionHandler implements ItemCheck {
    private final CreativeItemControl plugin;

    public PotionHandler(CreativeItemControl plugin) {
        this.plugin = plugin;
    }


    @Override
    public void check(ItemCheckContext ctx) {
        if (!plugin.potionsEnabled) return;
        if (ctx.player.hasPermission("cic.bypass.potions")) return;

        switch (ctx.item.getType()) {
            case POTION, LINGERING_POTION, SPLASH_POTION -> {}
            default -> { return; }

        }

        PotionMeta potionMeta = (PotionMeta) ctx.meta;

        if (potionMeta.hasCustomEffects()) {
            ctx.player.sendMessage(Component.text("Custom potions are not allowed here!", NamedTextColor.RED, TextDecoration.BOLD));
            ctx.cancel();
        }
    }
}
