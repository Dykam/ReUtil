package nl.dykam.dev.reutil;

import nl.dykam.dev.reutil.commands.CommandHandler;
import nl.dykam.dev.reutil.commands.CommandManager;
import nl.dykam.dev.reutil.events.EventManager;
import org.bukkit.event.*;
import org.bukkit.plugin.Plugin;

public class ReUtil {
    public static void registerEvents(Listener listener, Plugin plugin) {
        EventManager.registerEvents(listener, plugin);
    }

    public static void registerCommands(CommandHandler command, Plugin plugin) {
        CommandManager.getGlobal().registerCommands(command, plugin);
    }

    public static void register(Object carrier, Plugin plugin) {
        if(carrier instanceof Listener)
            registerEvents((Listener)carrier, plugin);
        if(carrier instanceof CommandHandler)
            registerCommands((CommandHandler)carrier, plugin);
    }
}
