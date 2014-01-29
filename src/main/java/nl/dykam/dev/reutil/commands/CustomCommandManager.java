package nl.dykam.dev.reutil.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.Map;

import static nl.dykam.dev.reutil.utils.Reflect.getAnnotatedMethods;

public class CustomCommandManager implements ICustomCommandManager {
    private final Plugin plugin;
    ArgumentParserRegistry registry;
    MethodParser parser;
    CustomCommandManager fallback;

    public CustomCommandManager(Plugin plugin) {
        this.plugin = plugin;
        registry = new ArgumentParserRegistry();
        parser = new MethodParser(registry);
    }

    @Override
    public void registerCommands(CommandHandler handler, Plugin plugin) {
        for (Map.Entry<Method, AutoCommand> entry : getAnnotatedMethods(handler, AutoCommand.class).entrySet()) {
            tryRegisterCommand(handler, entry.getKey(), entry.getValue(), plugin);
        }
    }

    @Override
    public <T> void registerArgumentType(Class<T> type, ArgumentParser<T> parser) {
        registry.register(type, parser);
    }

    @Override
    public <T> void registerArgumentType(String name, Class<T> type, ArgumentParser<T> parser) {
        registry.register(name, type, parser);
    }

    private void tryRegisterCommand(CommandHandler handler, Method method, AutoCommand annotation, Plugin plugin) {
        ParsedMethod parsed = parser.parse(method);
        ReUtilCommand command = new ReUtilCommand(parsed, annotation, plugin);
        PluginCommand pluginCommand = Bukkit.getPluginCommand(command.getName());
        if(pluginCommand.getPlugin().equals(plugin))
            command.override(pluginCommand);
    }

    public CustomCommandManager getFallback() {
        return fallback;
    }

    public void setFallback(CustomCommandManager fallback) {
        this.fallback = fallback;
        registry.setFallback(fallback.registry);
    }
}
