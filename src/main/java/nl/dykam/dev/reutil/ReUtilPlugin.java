package nl.dykam.dev.reutil;

import nl.dykam.dev.reutil.messenger.Messenger;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class ReUtilPlugin extends JavaPlugin {
    private static ReUtilPlugin instance;
    private static Messenger message;

    public static Messenger getMessage() {
        return message;
    }

    @Override
    public void onEnable() {
        instance = this;
        message = new Messenger(this, ChatColor.AQUA);
    }

    public static ReUtilPlugin instance() {
        return instance;
    }
}
