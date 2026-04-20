package net.kingidk.creativeItemControl.Util;

import net.kingidk.creativeItemControl.CreativeItemControl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class Command implements CommandExecutor, TabCompleter {
    private final CreativeItemControl plugin;
    private final MessageUtil messageUtil;

    public Command(CreativeItemControl plugin, MessageUtil messageUtil) {
        this.plugin = plugin;
        this.messageUtil = messageUtil;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        // No perms at all
        boolean isAdmin = sender.hasPermission("cic.admin");
        if (!isAdmin && !sender.hasPermission("cic.give")) {
            messageUtil.sendSender(sender, "commands.noperm");
            return true;
        }

        // Give
        if ((args.length == 2 || args.length == 3) && args[0].equalsIgnoreCase("give")) {
            ItemStack item = plugin.getExcludedItem(args[1]);

            if (item == null) {
                messageUtil.sendSender(sender, "commands.giveinvalid", "{id}", args[1]);
                return true;
            }
            Player target;
            if (args.length == 3) {
                if (!isAdmin) {
                    messageUtil.sendSender(sender, "command.giveothers");
                    return true;
                }
                target = Bukkit.getPlayerExact(args[2]);
                if (target == null) {
                    messageUtil.sendSender(sender, "commands.playernotfound", "{player}", args[2]);
                    return true;
                }
            } else if (sender instanceof Player p) {
                target = p;
            } else {
                messageUtil.sendSender(sender, "command.specifyplayer");
                return true;
            }
            boolean inList = plugin.worlds.contains(target.getWorld().getName());

            if (!isAdmin && plugin.worldsBlacklist == inList) {
                messageUtil.sendSender(sender, "command.invalidworld");
                return true;
            }
            if (!isAdmin && plugin.giveCooldownSeconds > 0 && plugin.isOnGiveCooldown(target.getUniqueId(), args[1])) {
                long remaining = plugin.getGiveCooldownRemaining(target.getUniqueId(), args[1]);
                messageUtil.sendSender(sender, "commands.cooldown", "{time}", String.valueOf(remaining));
                return true;
            }


            target.getInventory().addItem(item.clone());
            plugin.recordGive(target.getUniqueId(), args[1]);
            messageUtil.sendSender(sender, "commands.give", "{id}", args[1], "{player}", target.getName());
            return true;
        }


        if (!isAdmin) {
            if (args.length == 1 && args[0].equalsIgnoreCase("give")) {
                sender.sendMessage(Component.text("You must select an item! Available Items: " + plugin.getExcludedItems().keySet()));
                return true;
            }

            messageUtil.sendSender(sender, "commands.noperm");
            return true;
        }


        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            plugin.loadConfigCache();
            messageUtil.sendSender(sender, "commands.reload");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            Map<String, ItemStack> items = plugin.getExcludedItems();
            if (items.isEmpty()) {
                messageUtil.sendSender(sender, "commands.listempty");
            } else {
                sender.sendMessage(Component.text("Excluded items:", NamedTextColor.GOLD));
                items.forEach((id, item) ->
                        sender.sendMessage(Component.text("  " + id + " - " + item.getType(), NamedTextColor.WHITE))
                );
                return true;
            }
            return true;
        }

        // Exclude
        if (args.length == 2 && args[0].equalsIgnoreCase("exclude")) {
            if (!(sender instanceof Player p)) {
                sender.sendMessage(Component.text("Only players can use this command.", NamedTextColor.RED));
                return true;
            }
            ItemStack held = p.getInventory().getItemInMainHand();
            if (held.getType().isAir()) {
                sender.sendMessage (Component.text("You must be holding an item!", NamedTextColor.RED));
                return true;
            }
            plugin.storeExcludedItem(args[1], held);
            messageUtil.sendSender(sender, "commands.exclude", "{type}", String.valueOf(held.getType()), "{id}", args[1]);
            return true;
        }

        // Remove
        if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            if (plugin.getExcludedItem(args[1]) == null) {
                sender.sendMessage(Component.text("No excluded item found with id \"" + args[1] + "\".", NamedTextColor.RED));
                messageUtil.sendSender(sender, "commands.giveinvalid", "{id}", args[1]);
                return true;
            }
            plugin.removeExcludedItem(args[1]);
            messageUtil.sendSender(sender, "commands.remove", "{id}", args[1]);
            return true;
        }



        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!sender.hasPermission("cic.admin") && !sender.hasPermission("cic.give")) return List.of();

        if (!sender.hasPermission("cic.admin")) {
            if (args.length == 1) return List.of("give");
            if (args.length == 2 && args[0].equalsIgnoreCase("give")) return List.copyOf(plugin.getExcludedItems().keySet());
            return List.of();
        }


        if (args.length == 1) {
            return List.of("reload", "list", "exclude", "remove", "give");
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("give"))) {
            return List.copyOf(plugin.getExcludedItems().keySet());
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }
        return List.of();
    }


}
