package nl.dykam.dev.reutil.commands;

import nl.dykam.dev.reutil.ReUtilPlugin;
import nl.dykam.dev.reutil.commands.parsing.*;
import nl.dykam.dev.reutil.commands.parsing.parsers.IntParser;
import nl.dykam.dev.reutil.commands.parsing.parsers.StrictPlayerArgumentParser;
import nl.dykam.dev.reutil.data.ComponentHandle;
import nl.dykam.dev.reutil.data.ComponentManager;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.Map;

import static nl.dykam.dev.reutil.utils.Reflect.getAnnotatedMethods;

public class CommandManager {
    private final Plugin plugin;

    ArgumentParserRegistry registry;
    MethodParser parser;
    CommandManager fallback;
    public CommandManager(Plugin plugin) {
        this.plugin = plugin;
        registry = new ArgumentParserRegistry();
        parser = new MethodParser(registry);
    }

    public void registerCommands(CommandHandler handler, Plugin plugin) {
        for (Map.Entry<Method, AutoCommand> entry : getAnnotatedMethods(handler, AutoCommand.class).entrySet()) {
            tryRegisterCommand(handler, entry.getKey(), entry.getValue(), plugin);
        }
    }

    public <T> void registerArgumentType(Class<T> type, ArgumentParser<T> parser) {
        registry.register(type, parser);
    }

    public <T> void registerArgumentType(String name, Class<T> type, ArgumentParser<T> parser) {
        registry.register(name, type, parser);
    }

    private void tryRegisterCommand(CommandHandler handler, Method method, AutoCommand annotation, Plugin plugin) {
        ReUtilCommand command;
        PluginCommand pluginCommand;
        try {
            ParsedMethod parsed = parser.parse(handler, method);
            command = new ReUtilCommand(parsed, annotation, plugin);
            pluginCommand = Bukkit.getPluginCommand(command.getName());
        } catch (Throwable throwable) {
            ReUtilPlugin.instance().getLogger().severe("A command failed to register: ");
            throwable.printStackTrace();
            return;
        }
        if(pluginCommand != null && pluginCommand.getPlugin().equals(plugin))
            command.override(pluginCommand);
        else {
            ReUtilPlugin.instance().getLogger().severe("A command failed to register because no the command is not defined in plugin.yml: ");
            ReUtilPlugin.instance().getLogger().severe("\t/" + command.getName() + " in " + plugin.getName());
        }

    }

    public CommandManager getFallback() {
        return fallback;
    }

    public void setFallback(CommandManager fallback) {
        this.fallback = fallback;
        registry.setFallback(fallback.registry);
    }


    /** STATIC **/

    static ComponentHandle<Plugin, PluginCommandManagerRegistration> registrations;
    static CommandManager global;

    static {
        global = new CommandManager(ReUtilPlugin.instance());
        global.registerArgumentType(Player.class, new StrictPlayerArgumentParser());
        global.registerArgumentType(int.class, new IntParser());
        registrations = ComponentManager.get(ReUtilPlugin.instance()).get(PluginCommandManagerRegistration.class);
    }

    public static CommandManager get(Plugin plugin) {
        return registrations.get(plugin).getCommandManager();
    }
    public static CommandManager getGlobal() {
        return global;
    }
}
