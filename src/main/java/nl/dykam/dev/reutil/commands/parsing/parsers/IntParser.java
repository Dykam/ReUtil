package nl.dykam.dev.reutil.commands.parsing.parsers;

import nl.dykam.dev.reutil.commands.CommandExecuteContext;
import nl.dykam.dev.reutil.commands.parsing.ArgumentParser;
import nl.dykam.dev.reutil.commands.parsing.ParseResult;

public class IntParser extends ArgumentParser<Integer> {
    public IntParser() {
        super("number", false);
    }

    @Override
    public ParseResult<Integer> parse(CommandExecuteContext context, String argument, String name) {
        try {
            return ParseResult.success(Integer.parseInt(argument));
        } catch (NumberFormatException ex) {
            return ParseResult.failure(argument + " is not a valid " + name);
        }
    }
}
