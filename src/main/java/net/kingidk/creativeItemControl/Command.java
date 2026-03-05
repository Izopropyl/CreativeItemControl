package net.kingidk.creativeItemControl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Command implements CommandExecutor {
    private final CreativeItemControl plugin;

    public Command(CreativeItemControl plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, org.bukkit.command.@NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            commandSender.sendMessage(Component.text("ComponentControl config reloaded!", NamedTextColor.GREEN));
            plugin.reloadConfig();
            plugin.loadConfigCache();
            return true;
        }

        return false;
    }
}
