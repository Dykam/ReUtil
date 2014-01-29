package nl.dykam.dev.reutil.commands.parsers;

import nl.dykam.dev.reutil.commands.ArgumentParser;
import nl.dykam.dev.reutil.commands.CommandExecuteContext;
import nl.dykam.dev.reutil.commands.CommandTabContext;
import nl.dykam.dev.reutil.commands.ParseResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StrictPlayerArgumentParser extends ArgumentParser<Player> {
    public StrictPlayerArgumentParser() {
        super("player", false);
    }

    @Override
    public ParseResult<Player> parse(CommandExecuteContext context, String argument) {
        return ParseResult.notNull(Bukkit.getPlayerExact(argument));
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
