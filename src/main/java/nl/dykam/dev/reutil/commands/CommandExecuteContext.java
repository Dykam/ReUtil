package nl.dykam.dev.reutil.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Represents the context of a command invocation
 */
public class CommandExecuteContext {
    private AutoCommand description;
    private Command command;

    private String[] arguments;
    private String label;
    private CommandSender sender;
    /**
     * The target of the command. Either the parameter marked with @Sender or the sender if it is a Player
     */
    private Player target;
    private List<Object> result;

    public CommandExecuteContext(AutoCommand description, Command command, String[] arguments, String label, CommandSender sender, List<Object> result) {
        this.description = description;
        this.command = command;
        this.arguments = arguments;
        this.label = label;
        this.sender = sender;
        this.result = result;
    }

    public AutoCommand getDescription() {
        return description;
    }

    public Command getCommand() {
        return command;
    }

    public String[] getArguments() {
        return arguments;
    }

    public String getLabel() {
        return label;
    }

    public boolean isSender(Player player) {
        return sender.equals(player);
    }

    public CommandSender getSender() {
        return sender;
    }

    public Player getTarget() {
        return target;
    }

    public boolean hasTarget() {
        return target != null;
    }

    public boolean isTarget(Player player) {
        return player.equals(target);
    }

    public List<Object> getResult() {
        return result;
    }

    public void setTarget(Player target) {
        this.target = target;
    }
}
