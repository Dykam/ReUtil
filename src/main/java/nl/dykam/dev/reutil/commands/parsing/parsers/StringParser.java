package nl.dykam.dev.reutil.commands.parsing.parsers;


import nl.dykam.dev.reutil.commands.CommandExecuteContext;
import nl.dykam.dev.reutil.commands.parsing.ArgumentParser;
import nl.dykam.dev.reutil.commands.parsing.ArgumentParserTree;
import nl.dykam.dev.reutil.commands.parsing.ParseResult;

public class StringParser extends ArgumentParser<String> {
    public StringParser() {
        super("text", ArgumentParserTree.Type.FREE_SYNTAX);
    }
    @Override
    public ParseResult<String> parse(CommandExecuteContext context, String argument, String name) {
        return ParseResult.success(argument);
    }
}
