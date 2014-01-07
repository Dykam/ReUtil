package nl.dykam.dev.reutil;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;


public class ReUtil {
    private static Plugin reUtilPlugin;

    private static Plugin getReUtilPlugin() {
        if(reUtilPlugin == null) {
            reUtilPlugin = new ReUtilPlugin();
            Bukkit.getPluginManager().enablePlugin(reUtilPlugin);
        }
        return reUtilPlugin;
    }

    public static void registerEvents(Listener listener, Plugin plugin) {

    }

    public static void registerPersistentEvents(Listener listener) {
        registerEvents(listener, getReUtilPlugin());
    }
}
