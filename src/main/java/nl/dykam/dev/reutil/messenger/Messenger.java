package nl.dykam.dev.reutil.messenger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Messenger {
    private static Map<Plugin, Messenger> defaults;
    /**
     * Colors suitable for plugin tags
     */
    private static ChatColor[] tagColors = {
            ChatColor.DARK_BLUE, ChatColor.DARK_GREEN, ChatColor.DARK_AQUA, ChatColor.DARK_RED,
            ChatColor.DARK_PURPLE, ChatColor.GOLD, ChatColor.BLUE, ChatColor.GREEN,
            ChatColor.AQUA, ChatColor.RED, ChatColor.LIGHT_PURPLE, ChatColor.YELLOW
    };

    private final Plugin plugin;
    private final Broadcast broadcast;

    private String tag;
    private ChatColor color;
    private String prefix;

    public Messenger(Plugin plugin) {
        this(plugin, createColor(plugin));
    }

    public Messenger(Plugin plugin, ChatColor color) {
        this(plugin, createTag(plugin), color);
    }

    public Messenger(Plugin plugin, String tag, ChatColor color) {
        this.plugin = plugin;
        this.tag = tag;
        this.color = color;
        broadcast = new Broadcast();
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
        if (tag != null) {
            ChatColor color = this.color != null ? this.color : ChatColor.DARK_GRAY;
            prefix = color + "[" + tag + "]" + ChatColor.GRAY + " - " + ChatColor.RESET;
        } else {
            prefix = "";
        }
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

    public Broadcast broadcast() {
        return broadcast;
    }

    public void broadcast(String message) {
        broadcast.message(message);
    }

    static {
        defaults = new WeakHashMap<>();
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
        if(plugin == null)
            return null;
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

    /**
     * Create a color based on the plugin's name
     * @param plugin
     * @return
     */
    private static ChatColor createColor(Plugin plugin) {
        if(plugin == null)
            return ChatColor.WHITE;
        int val = 0;
        for (byte b : plugin.getName().getBytes(StandardCharsets.UTF_8)) {
            val += b;
        }
        return tagColors[val % tagColors.length];
    }

    public static Messenger get(Plugin plugin) {
        Messenger message = defaults.get(plugin);
        if(message == null)
            defaults.put(plugin, message = new Messenger(plugin, ChatColor.WHITE));
        return message;
    }
    public static Messenger get() {
        return get(null);
    }

    public class Broadcast {
        private Broadcast() {

        }
        public void message(String message) {
            Bukkit.broadcastMessage(prefix + message);
        }

        public void message(ChatColor color, String message) {
            Bukkit.broadcastMessage(prefix + colorize(color, message));
        }

        public void warn(String message) {
            message(ChatColor.GOLD, message);
        }
        public void failure(String message) {
            message(ChatColor.RED, message);
        }
        public void success(String message) {
            message(ChatColor.DARK_GREEN, message);
        }
    }
}
