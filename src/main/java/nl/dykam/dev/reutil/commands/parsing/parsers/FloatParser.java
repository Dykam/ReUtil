package nl.dykam.dev.reutil.commands.parsing.parsers;

import nl.dykam.dev.reutil.commands.CommandExecuteContext;
import nl.dykam.dev.reutil.commands.parsing.ArgumentParser;
import nl.dykam.dev.reutil.commands.parsing.ParseResult;

public class FloatParser extends ArgumentParser<Float> {
    public FloatParser() {
        super("number", false);
    }

    @Override
    public ParseResult<Float> parse(CommandExecuteContext context, String argument, String name) {
        try {
            return ParseResult.success(Float.parseFloat(argument));
        } catch (NumberFormatException ex) {
            return ParseResult.failure(argument + " is not a valid " + name);
        }
    }
}
