package net.kingidk.creativeItemControl.Handlers;

import net.kingidk.creativeItemControl.CreativeItemControl;
import net.kingidk.creativeItemControl.Enums.AttributeAction;
import net.kingidk.creativeItemControl.ItemCheckContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;


public class AttributeHandler implements ItemCheck{
    private final CreativeItemControl plugin;

    public AttributeHandler(CreativeItemControl plugin) {
        this.plugin = plugin;
    }


    @Override
    public void check(ItemCheckContext ctx) {
        if (ctx.isCancelled()) return;
        if (!plugin.attributesEnabled) return;
        if (ctx.player.hasPermission("cic.bypass.attributes")) return;

        boolean attributeIssue = ctx.meta.getAttributeModifiers() != null;
        if (attributeIssue) {
            if (plugin.attributesAction.equals(AttributeAction.REMOVE)) {
                ctx.meta.setAttributeModifiers(null);
            } else {
                ctx.cancel();
            }

            if (plugin.playerAlerts) {
                ctx.player.sendMessage(Component.text("Items with attribute modifiers are not allowed here!", NamedTextColor.RED, TextDecoration.BOLD));
            }

        }




    }
}
