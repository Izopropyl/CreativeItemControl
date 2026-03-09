package net.kingidk.creativeItemControl.Handlers;

import io.papermc.paper.datacomponent.DataComponentType;
import net.kingidk.creativeItemControl.CreativeItemControl;
import net.kingidk.creativeItemControl.ItemCheckContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
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
        for (String name : plugin.components) {
            NamespacedKey key = NamespacedKey.fromString(name);
            if (key == null) continue;
            DataComponentType type = Registry.DATA_COMPONENT_TYPE.get(key);
            if (type == null) continue;

            ItemStack defaultItem = plugin.getDefaultItem(ctx.item.getType());

            if (ctx.item.hasData(type)) {
                if (type instanceof DataComponentType.Valued<?> valued) {
                    Object defaultData = defaultItem.getData(valued);
                    Object itemData = ctx.item.getData(valued);
                    if (Objects.equals(defaultData, itemData)) continue;
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
