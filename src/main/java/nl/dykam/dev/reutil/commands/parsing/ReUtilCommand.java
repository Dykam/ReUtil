package nl.dykam.dev.reutil.commands.parsing;

import nl.dykam.dev.reutil.commands.AutoCommand;
import nl.dykam.dev.reutil.commands.CommandExecuteContext;
import nl.dykam.dev.reutil.messenger.Messenger;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;

public class ReUtilCommand extends Command implements CommandExecutor {
    private final ParsedMethod parsedMethod;
    private final AutoCommand annotation;
    private final Plugin plugin;
    private ArgumentParserList argumentParserList;

    public ReUtilCommand(ParsedMethod parsedMethod, AutoCommand annotation, Plugin plugin) {
        super(parsedMethod.getName(), parsedMethod.getDescription(), generateUsage(parsedMethod), Arrays.asList(parsedMethod.getAliases()));
        this.parsedMethod = parsedMethod;
        this.annotation = annotation;
        this.plugin = plugin;
        if(!"".equals(parsedMethod.getPermissionMessage()))
            setPermissionMessage(parsedMethod.getPermissionMessage());
        if(!"".equals(parsedMethod.getPermission()))
            setPermission(parsedMethod.getPermission());
        argumentParserList = ArgumentParserList.fromParsedMethod(parsedMethod);
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
        int offset = parsedMethod.isRequiresContext() ? 2 : 1;
        List<Object> parsedParams = Arrays.asList(new Object[parsedMethod.getParams().length + offset]);
        CommandExecuteContext context = new CommandExecuteContext(annotation, this, strings, s, commandSender, parsedParams.subList(offset, parsedParams.size()));

        parsedParams.set(0, parsedMethod.getHandler());
        if (parsedMethod.isRequiresContext()) {
            parsedParams.set(1, context);
        }

        ArgumentParserList list = getList();
        ExecuteResult parseResult = list.parse(context);
        if (parseResult.isFailure()) {
            String message = parseResult.getMessage();
            if (message == null) message = "Failed to execute /" + s + " because one or more arguments where invalid";
            Messenger.get(plugin).failure(commandSender, message);
            return false;
        }

        try {
            Object result = parsedMethod.getMethod().invokeWithArguments(parsedParams);
            if (result instanceof Boolean)
                return (boolean) result;
            if (result != null) {
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
        pluginCommand.setUsage(getUsage());
        pluginCommand.setDescription(getDescription());
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        return execute(commandSender, s, strings);
    }

    public ArgumentParserList getList() {
        return argumentParserList;
    }
}
