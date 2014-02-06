package nl.dykam.dev.reutil.commands.parsing.parsers;

import nl.dykam.dev.reutil.commands.CommandExecuteContext;
import nl.dykam.dev.reutil.commands.parsing.ArgumentParser;
import nl.dykam.dev.reutil.commands.parsing.ArgumentParserTree;
import nl.dykam.dev.reutil.commands.parsing.ParseResult;

public class DoubleParser extends ArgumentParser<Double> {
    public DoubleParser() {
        super("number", false, ArgumentParserTree.Type.LIMITED_SYNTAX);
    }

    @Override
    public ParseResult<Double> parse(CommandExecuteContext context, String argument, String name) {
        try {
            return ParseResult.success(Double.parseDouble(argument));
        } catch (NumberFormatException ex) {
            return ParseResult.failure(argument + " is not a valid " + name);
        }
    }
}
