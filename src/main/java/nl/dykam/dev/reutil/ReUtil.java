package nl.dykam.dev.reutil;

import nl.dykam.dev.reutil.commands.CommandManager;
import nl.dykam.dev.reutil.events.EventManager;
import org.bukkit.event.*;
import org.bukkit.plugin.Plugin;

public class ReUtil {
    public static void registerEvents(Listener listener, Plugin plugin) {
        EventManager.registerEvents(listener, plugin);
    }

    }

    }
}
