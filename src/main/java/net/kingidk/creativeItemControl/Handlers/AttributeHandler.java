package net.kingidk.creativeItemControl.Handlers;

import net.kingidk.creativeItemControl.CreativeItemControl;
import net.kingidk.creativeItemControl.Enums.AttributeAction;
import net.kingidk.creativeItemControl.Util.ItemCheckContext;
import net.kingidk.creativeItemControl.Util.MessageUtil;


public class AttributeHandler implements ItemCheck{
    private final CreativeItemControl plugin;
    private final MessageUtil messageUtil;

    public AttributeHandler(CreativeItemControl plugin, MessageUtil messageUtil) {
        this.plugin = plugin;
        this.messageUtil = messageUtil;
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
                messageUtil.sendAlert(ctx.player, "alerts.attributes");
            }

        }




    }
}
