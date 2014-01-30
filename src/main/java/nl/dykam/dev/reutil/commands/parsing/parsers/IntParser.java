package nl.dykam.dev.reutil.commands.parsing.parsers;

import nl.dykam.dev.reutil.commands.CommandExecuteContext;
import nl.dykam.dev.reutil.commands.CommandTabContext;
import nl.dykam.dev.reutil.commands.parsing.ArgumentParser;
import nl.dykam.dev.reutil.commands.parsing.ParseResult;

import java.util.Collections;
import java.util.List;

public class IntParser extends ArgumentParser<Integer> {
    public IntParser() {
        super("number", false);
    }

    @Override
    public ParseResult<Integer> parse(CommandExecuteContext context, String argument) {
        try {
            return ParseResult.success(Integer.parseInt(argument));
        } catch (NumberFormatException ex) {
            return ParseResult.failure();
        }
    }

    @Override
    public List<String> complete(CommandTabContext context, String current) {
        return Collections.emptyList();
    }
}