package nl.dykam.dev.reutil.message;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Message {
    private final Plugin plugin;
    private String tag;

    private ChatColor color;

    private String prefix;
    public Message(Plugin plugin, ChatColor color) {
        this(plugin, createTag(plugin), color);
    }

    public Message(Plugin plugin, String tag, ChatColor color) {
        this.plugin = plugin;
        this.tag = tag;
        this.color = color;
        updatePrefix();
    }

    public ChatColor getColor() {
        return color;
    }

    public void setColor(ChatColor color) {
        this.color = color;
        updatePrefix();
    }

    private void updatePrefix() {
        prefix = color + "[" + tag + "]" + ChatColor.GRAY + " - " + ChatColor.RESET;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
        updatePrefix();
    }

    public void message(CommandSender sender, String message) {
        sender.sendMessage(prefix + message);
    }

    public void message(CommandSender sender, ChatColor color, String message) {
        sender.sendMessage(prefix + colorize(color, message));
    }

    public void warn(CommandSender sender, String message) {
        message(sender, ChatColor.GOLD, message);
    }
    public void failure(CommandSender sender, String message) {
        message(sender, ChatColor.RED, message);
    }
    public void success(CommandSender sender, String message) {
        message(sender, ChatColor.DARK_GREEN, message);
    }

    private static String colorize(ChatColor color, String message) {
        return color + message.replace(ChatColor.RESET.toString(), color.toString());
    }

    private static final Pattern IMPORTANT = Pattern.compile("[A-Z0-9]");

    /**
     * Tries to, somewhat intelligently, construct a tag for the plugin.
     * @param plugin The Plugin
     * @return       The tag
     */
    private static String createTag(Plugin plugin) {
        String name = plugin.getName();
        if(name.length() <= 4)
            return name;
        Matcher matcher = IMPORTANT.matcher(name);
        int count = 0;
        String important = "";
        while (matcher.find()) {
            count++;
            important += matcher.group();
        }
        if(count <= 4 && count > 1) {
            return important;
        }
        return name.substring(0, 4);
    }

    public static class Broadcast {
        public static void message(String message) {
            Bukkit.broadcastMessage(ChatColor.AQUA + "[PVP]" + ChatColor.GRAY + " - " + ChatColor.RESET + message);
        }

        public static void message(ChatColor color, String message) {
            Bukkit.broadcastMessage(ChatColor.AQUA + "[PVP]" + ChatColor.GRAY + " - " + ChatColor.RESET + colorize(color, message));
        }
        public static void warn(String message) {
            message(ChatColor.GOLD, message);
        }
        public static void failure(String message) {
            message(ChatColor.RED, message);
        }
        public static void success(String message) {
            message(ChatColor.DARK_GREEN, message);
        }
    }
}
