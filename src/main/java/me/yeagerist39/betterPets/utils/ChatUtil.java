package me.yeagerist39.betterPets.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class ChatUtil {

    public static String format(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static List<String> applyArgs(List<String> text, Map<String, String> args) { // (list, Map.of(Pair("key", "value"))
        for (int i = 0; i < text.size(); i++) {
            String line = text.get(i);

            for (String arg: args.keySet()) {
                line = line.replace(arg, args.get(arg));
            }

            text.set(i, format(line));
        }

        return text;
    }

    public static void sendMessage(CommandSender recipient, String message) {
        recipient.sendMessage(format(message));
    }

    public static void sendConfigMessage(CommandSender recipient, String configPath) {
        sendMessage(recipient, ConfigManager.instance.configs.get("messages.yml").getString(configPath));
    }

    public static void sendConfigMessage(CommandSender recipient, String configPath, Map<String, String> args) {
        String message = ConfigManager.instance.configs.get("messages.yml").getString(configPath);

        for (String key: args.keySet()) {
            message = message.replace(key, args.get(key));
        }

        sendMessage(recipient, message);
    }

    public static void sendTitle(Player recipient, String message, String subMsg) {
        recipient.sendTitle(message, subMsg, 10, 40, 10);
    }

    public static void sendConfigTitle(Player recipient, String messagePath, String subMessagePath) {
        if (ConfigManager.instance.configs.get("messages.yml").getString(subMessagePath) == null) {
            sendTitle(
                    recipient,
                    ConfigManager.instance.configs.get("messages.yml").getString(messagePath),
            ""
            );
        }
        else {
            sendTitle(
                    recipient,
                    ConfigManager.instance.configs.get("messages.yml").getString(messagePath),
                    ConfigManager.instance.configs.get("messages.yml").getString(subMessagePath)
            );
        }
    }

    public static void sendConfigTitle(Player recipient, String messagePath, String subMessagePath, Map<String, String> args) {
        String message = ConfigManager.instance.configs.get("messages.yml").getString(messagePath);
        String subMessage;

        if (!subMessagePath.equals("")) {
             subMessage = ConfigManager.instance.configs.get("messages.yml").getString(subMessagePath);
        }
        else {
            subMessage = "";
        }

        assert message != null;

        for (String key: args.keySet()) {
            message = message.replace(key, args.get(key));
        }

        if (subMessage != null) {
            for (String key: args.keySet()) {
                subMessage = subMessage.replace(key, args.get(key));
            }
        }

        if (subMessage == null) {
            sendTitle(
                    recipient,
                   format(message),
                    ""
            );
        }
        else {
            sendTitle(
                    recipient,
                    format(message),
                    format(subMessage)
            );
        }
    }
}
