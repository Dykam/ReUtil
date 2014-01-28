package nl.dykam.dev.reutil.commands;

import nl.dykam.dev.reutil.messenger.Messenger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class CommandResult {
    private String message;
    private Type type;

    public CommandResult(String message, Type type) {
        this.message = message;
        this.type = type;
    }

    public void send(CommandSender receiver, Plugin plugin) {
        switch (type) {
            case RAW:
                Messenger.get().message(receiver, message);
                break;
            case FAILURE:
                Messenger.get(plugin).failure(receiver, message);
                break;
            case MESSAGE:
                Messenger.get(plugin).message(receiver, message);
                break;
            case SUCCESS:
                Messenger.get(plugin).success(receiver, message);
                break;
            case WARNING:
                Messenger.get(plugin).warn(receiver, message);
                break;
        }
    }

    public void send(CommandSender receiver) {
        switch (type) {
            case RAW:
                Messenger.get().message(receiver, message);
                break;
            case FAILURE:
                Messenger.get().failure(receiver, message);
                break;
            case MESSAGE:
                Messenger.get().message(receiver, message);
                break;
            case SUCCESS:
                Messenger.get().success(receiver, message);
                break;
            case WARNING:
                Messenger.get().warn(receiver, message);
                break;
        }
    }

    public String getMessage() {
        return message;
    }

    public Type getType() {
        return type;
    }

    public static CommandResult playerNotFound() {
        return warning("Player not found");
    }

    public static CommandResult playerNotFound(String player) {
        return warning("Player " + player + ChatColor.RESET + " not found");
    }

    public static CommandResult noPermission() {
        return failure("No permission");
    }

    public static CommandResult noPermission(String permission) {
        return failure("You don't have the permission " + permission);
    }

    public static CommandResult noConsole() {
        return failure("Either specify a target or execute ingame");
    }

    public static CommandResult message(String message) {
        return new CommandResult(message, Type.MESSAGE);
    }

    public static CommandResult raw(String message) {
        return new CommandResult(message, Type.RAW);
    }
    public static CommandResult success() {
        return success("Command successfully executed");
    }

    public static CommandResult success(String message) {
        return new CommandResult(message, Type.SUCCESS);
    }

    public static CommandResult warning(String message) {
        return new CommandResult(message, Type.WARNING);
    }

    public static CommandResult failure(String message) {
        return new CommandResult(message, Type.WARNING);
    }

    public static CommandResult silent() {
        return null;
    }

    private enum Type {
        RAW,
        MESSAGE,
        SUCCESS,
        WARNING,
        FAILURE,
    }
}
