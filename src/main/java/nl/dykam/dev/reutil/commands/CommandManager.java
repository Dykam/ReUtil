package nl.dykam.dev.reutil.commands;

import nl.dykam.dev.reutil.ReUtilPlugin;
import nl.dykam.dev.reutil.commands.parsers.IntParser;
import nl.dykam.dev.reutil.commands.parsers.StrictPlayerArgumentParser;
import nl.dykam.dev.reutil.data.ComponentHandle;
import nl.dykam.dev.reutil.data.ComponentManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CommandManager {
    static ComponentHandle<Plugin, PluginCommandManagerRegistration> registrations;
    static CustomCommandManager global;

    static {
        global = new CustomCommandManager(ReUtilPlugin.instance());
        global.registerArgumentType(Player.class, new StrictPlayerArgumentParser());
        global.registerArgumentType(int.class, new IntParser());
        registrations = ComponentManager.get(ReUtilPlugin.instance()).get(PluginCommandManagerRegistration.class);
    }
    private CommandManager() {

    }
    public static void registerCommands(CommandHandler handler, Plugin plugin) {
        global.registerCommands(handler, plugin);
    }

    public static <T> void registerArgumentType(Class<T> type, ArgumentParser<T> parser) {
        global.registerArgumentType(type, parser);
    }
    public static CustomCommandManager get(Plugin plugin) {
        return registrations.get(plugin).getCommandManager();
    }
    public static CustomCommandManager getGlobal() {
        return global;
    }
}
