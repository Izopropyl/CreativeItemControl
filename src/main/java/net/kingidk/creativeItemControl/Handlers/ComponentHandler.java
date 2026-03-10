package net.kingidk.creativeItemControl.Handlers;

import io.papermc.paper.datacomponent.DataComponentType;
import net.kingidk.creativeItemControl.CreativeItemControl;
import net.kingidk.creativeItemControl.ItemCheckContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class ComponentHandler implements ItemCheck {
    private final CreativeItemControl plugin;
    public ComponentHandler(CreativeItemControl plugin) {
        this.plugin = plugin;
    }


    @Override
    public void check(ItemCheckContext ctx) {
        if (ctx.isCancelled()) return;
        if (!plugin.componentsEnabled) return;
        if (ctx.player.hasPermission("cic.bypass.components")) return;


        boolean found = false;
        for (DataComponentType type : plugin.resolvedComponents) {
            ItemStack defaultItem = plugin.getDefaultItem(ctx.item.getType());
            if (ctx.item.hasData(type)) {
                if (type instanceof DataComponentType.Valued<?> valued) {
                    if (Objects.equals(defaultItem.getData(valued), ctx.item.getData(valued))) continue;
                } else {
                    if (defaultItem.hasData(type)) continue;
                }
                ctx.cancel();
                found = true;
            }

        }

        if (found && plugin.playerAlerts) {
            ctx.player.sendMessage(Component.text("Items with custom components are not allowed here!", NamedTextColor.RED, TextDecoration.BOLD));
        }


    }
}
