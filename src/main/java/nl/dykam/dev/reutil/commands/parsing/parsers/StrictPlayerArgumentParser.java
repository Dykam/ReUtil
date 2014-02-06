package nl.dykam.dev.reutil.commands.parsing.parsers;

import nl.dykam.dev.reutil.commands.CommandExecuteContext;
import nl.dykam.dev.reutil.commands.CommandTabContext;
import nl.dykam.dev.reutil.commands.parsing.ArgumentParser;
import nl.dykam.dev.reutil.commands.parsing.ArgumentParserTree;
import nl.dykam.dev.reutil.commands.parsing.ParseResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StrictPlayerArgumentParser extends ArgumentParser<Player> {
    public StrictPlayerArgumentParser() {
        super("player", false, ArgumentParserTree.Type.FREE_SYNTAX);
    }

    @Override
    public ParseResult<Player> parse(CommandExecuteContext context, String argument, String name) {
        Player player = Bukkit.getPlayerExact(argument);
        if(player != null && context.getSender() instanceof Player) {
            Player sender = (Player) context.getSender();
            if(!sender.canSee(player))
                player = null;
        }
        if(player == null)
            return ParseResult.failure("Player " + argument + " not found!");
        return ParseResult.success(player);
    }

    @Override
    public List<String> complete(CommandTabContext context, String current) {
        Player[] onlinePlayers = Bukkit.getOnlinePlayers();
        List<String> matching = new ArrayList<>();
        for (Player onlinePlayer : onlinePlayers) {
            if(onlinePlayer.getName().startsWith(current))
                matching.add(onlinePlayer.getName());
        }
        return Collections.unmodifiableList(matching);
    }
}
