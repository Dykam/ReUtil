package nl.dykam.dev.reutil.commands;

import nl.dykam.dev.reutil.commands.parsers.IntParser;
import nl.dykam.dev.reutil.commands.parsers.StrictPlayerArgumentParser;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CommandManager {
    static CustomCommandManager global;
    static {
        global = new CustomCommandManager();
        global.registerArgumentType(Player.class, new StrictPlayerArgumentParser());
        global.registerArgumentType(int.class, new IntParser());
    }
    private CommandManager() {

    }
    public static void registerCommands(CommandHandler handler, Plugin plugin) {
        global.registerCommands(handler, plugin);
    }

    public static <T> void registerArgumentType(Class<T> type, ArgumentParser<T> parser) {
        global.registerArgumentType(type, parser);
    }
}
