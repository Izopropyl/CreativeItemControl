package net.kingidk.creativeItemControl.Handlers;

import net.kingidk.creativeItemControl.CreativeItemControl;
import net.kingidk.creativeItemControl.Util.ItemCheckContext;
import net.kingidk.creativeItemControl.Util.MessageUtil;
import org.bukkit.inventory.meta.PotionMeta;

public class PotionHandler implements ItemCheck {
    private final CreativeItemControl plugin;
    private final MessageUtil messageUtil;

    public PotionHandler(CreativeItemControl plugin, MessageUtil messageUtil) {
        this.plugin = plugin;
        this.messageUtil = messageUtil;
    }


    @Override
    public void check(ItemCheckContext ctx) {
        if (ctx.isCancelled()) return;
        if (!plugin.config.potionsEnabled) return;
        if (ctx.player.hasPermission("cic.bypass.potions")) return;

        switch (ctx.item.getType()) {
            case POTION, LINGERING_POTION, SPLASH_POTION -> {}
            default -> { return; }

        }

        PotionMeta potionMeta = (PotionMeta) ctx.meta;

        if (potionMeta.hasCustomEffects()) {
            if (plugin.config.playerAlerts) {
                messageUtil.sendAlert(ctx.player, "alerts.potions");
            }
            ctx.cancel();
        }
    }
}
