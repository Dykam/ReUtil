package nl.dykam.dev.reutil.commands.parsing;

import nl.dykam.dev.reutil.commands.AutoCommand;
import nl.dykam.dev.reutil.commands.CommandExecuteContext;
import nl.dykam.dev.reutil.commands.CommandResult;
import nl.dykam.dev.reutil.commands.ParseResult;
import nl.dykam.dev.reutil.messenger.Messenger;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

public class ReUtilCommand extends Command implements CommandExecutor {
    private final ParsedMethod parsedMethod;
    private final AutoCommand annotation;
    private final Plugin plugin;

    public ReUtilCommand(ParsedMethod parsedMethod, AutoCommand annotation, Plugin plugin) {
        super(parsedMethod.getName(), parsedMethod.getDescription(), generateUsage(parsedMethod), Arrays.asList(parsedMethod.getAliases()));
        this.parsedMethod = parsedMethod;
        this.annotation = annotation;
        this.plugin = plugin;
        if(!"".equals(parsedMethod.getPermissionMessage()))
            setPermissionMessage(parsedMethod.getPermissionMessage());
        if(!"".equals(parsedMethod.getPermission()))
            setPermission(parsedMethod.getPermission());
    }

    private static String generateUsage(ParsedMethod parsed) {
        StringBuilder sb = new StringBuilder();
        sb.append('/').append(parsed.getName());
        for (ParsedMethodParam parsedMethodParam : parsed.getParams()) {
            sb.append(' ').append(parsedMethodParam.getUsingName());
        }

        return sb.toString();
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        CommandExecuteContext.Builder builder = new CommandExecuteContext.Builder();
        CommandExecuteContext context = builder.instance();
        builder.setArguments(strings);
        builder.setLabel(s);
        builder.setCommand(this);
        builder.setDescription(annotation);
        builder.setSender(commandSender);
        int argumentIndex = 0, resultIndex = 0;
        ParsedMethodParam[] params = parsedMethod.getParams();
        Object[] parsedParams = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            ParsedMethodParam methodParam = params[i];
            if (strings.length <= argumentIndex) {
                Messenger.get(plugin).failure(commandSender, "Not enough arguments");
                return false;
            }
            ParseResult<?> parsedParam = methodParam.getParser().parse(context, strings[argumentIndex]);
            if (parsedParam.isFailure()) {
                if (methodParam.isOptional()) {
                    continue;
                } else if(methodParam.isSender()) {
                    if(!(commandSender instanceof Player)) {
                        Messenger.get(plugin).failure(commandSender, "Execute in-game or provide a target player");
                        return false;
                    }
                    parsedParams[i] = commandSender;
                    builder.setTarget((Player)commandSender);
                    resultIndex++;
                } else if (parsedParam.getCommandResult() != null) {
                    parsedParam.getCommandResult().send(commandSender, plugin);
                } else {
                    Messenger.get(plugin).failure(commandSender, methodParam.getUsingName() + " at position " + argumentIndex + "not recognized");
                    return false;
                }
            }
            if (methodParam.isSender())
                builder.setTarget((Player) parsedParam.getValue());
            parsedParams[i] = parsedParam.getValue();
            resultIndex++;
            argumentIndex++;
        }

        try {
            Object result = parsedMethod.getMethod().invokeWithArguments(parsedParams);
            if(result instanceof Boolean)
                return (boolean)result;
            if(result instanceof CommandResult) {
                CommandResult commandResult = (CommandResult) result;
                commandResult.send(commandSender, plugin);
                return true;
            }
            if(result != null) {
                throw new IllegalStateException("This should never happen. Contact the developer");
            }
            return true;
        } catch (Throwable throwable) {
            throw new CommandException("Error occured while executing command: " + getName(), throwable);
        }
    }

    public void override(PluginCommand pluginCommand) {
        pluginCommand.setExecutor(this);
        pluginCommand.setAliases(getAliases());
        pluginCommand.setPermission(getPermission());
        pluginCommand.setPermissionMessage(getPermissionMessage());
        pluginCommand.setDescription(getDescription());
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        return execute(commandSender, s, strings);
    }
}
