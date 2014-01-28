package nl.dykam.dev.reutil.commands;

import java.util.List;

public interface ArgumentParser<T> {
    public ParseResult<T> parse(CommandExecuteContext context, String argument);
    public List<String> complete(CommandTabContext context, String current);
}
