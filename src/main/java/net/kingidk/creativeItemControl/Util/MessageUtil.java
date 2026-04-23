package net.kingidk.creativeItemControl.Util;

import net.kingidk.creativeItemControl.CreativeItemControl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class MessageUtil {
    private final CreativeItemControl plugin;
    private final Map<UUID, Long> alertCooldowns = new HashMap<>();

    public MessageUtil(CreativeItemControl plugin) {
        this.plugin = plugin;
    }

    public void sendAlert(Player player, String id) {
       long now = System.currentTimeMillis();
       UUID uuid = player.getUniqueId();

       if (now - alertCooldowns.getOrDefault(uuid, 0L) < plugin.config.alertCooldown) {
           return;
       }

       alertCooldowns.put(uuid, now);
        send(player, id, "{player}", player.getName());
    }

    public void send(CommandSender sender, String id, String... replacements) {
        String message = plugin.getConfig().getString("messages." + id);
        if (message == null) {
            plugin.getLogger().warning("Message " + id + " has not been defined");
            return;
        }
        for (int i = 0; i + 1 < replacements.length; i += 2 ) {
            message = message.replace(replacements[i],  replacements[i + 1]);
        }
        Component component = MiniMessage.miniMessage().deserialize(convertLegacyToMiniMessage(message));
        sender.sendMessage(component);
    }

    private static String convertLegacyToMiniMessage(String input) {
        return input
                .replace("&0", "<black>").replace("&1", "<dark_blue>")
                .replace("&2", "<dark_green>").replace("&3", "<dark_aqua>")
                .replace("&4", "<dark_red>").replace("&5", "<dark_purple>")
                .replace("&6", "<gold>").replace("&7", "<gray>")
                .replace("&8", "<dark_gray>").replace("&9", "<blue>")
                .replace("&a", "<green>").replace("&b", "<aqua>")
                .replace("&c", "<red>").replace("&d", "<light_purple>")
                .replace("&e", "<yellow>").replace("&f", "<white>")
                .replace("&l", "<bold>").replace("&o", "<italic>")
                .replace("&n", "<underlined>").replace("&m", "<strikethrough>")
                .replace("&k", "<obfuscated>").replace("&r", "<reset>");
    }

}
