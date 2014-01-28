package nl.dykam.dev.reutil.commands;

import org.bukkit.plugin.Plugin;

public interface ICustomCommandManager {
    void registerCommands(CommandHandler handler, Plugin plugin);

    <T> void registerArgumentType(Class<T> type, ArgumentParser<T> parser);
    <T> void registerArgumentType(String name, Class<T> type, ArgumentParser<T> parser);
}
