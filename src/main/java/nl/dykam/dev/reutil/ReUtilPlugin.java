package nl.dykam.dev.reutil;

import nl.dykam.dev.reutil.message.Message;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class ReUtilPlugin extends JavaPlugin {
    private static ReUtilPlugin instance;
    private static Message message;

    public static Message getMessage() {
        return message;
    }

    @Override
    public void onEnable() {
        instance = this;
        message = new Message(this, ChatColor.AQUA);
    }

    public static ReUtilPlugin instance() {
        return instance;
    }
}
