package nl.dykam.dev.reutil.commands.parsing.parsers;

import nl.dykam.dev.reutil.commands.CommandExecuteContext;
import nl.dykam.dev.reutil.commands.CommandTabContext;
import nl.dykam.dev.reutil.commands.parsing.ArgumentParser;
import nl.dykam.dev.reutil.commands.parsing.ParseResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BooleanParser extends ArgumentParser<Boolean> {
    public BooleanParser() {
        super("true|false");
    }

    @Override
    public ParseResult<Boolean> parse(CommandExecuteContext context, String argument, String name) {
        switch (argument.toLowerCase()) {
            case "true":
                return ParseResult.success(true);
            case "false":
                return ParseResult.success(false);
        }
        return ParseResult.failure(name + " has to be either true or false");
    }

    @Override
    public List<String> complete(CommandTabContext context, String current) {
        String lowerCase = current.toLowerCase();
        if ("true".startsWith(lowerCase)) return Arrays.asList("true");
        if ("false".startsWith(lowerCase)) return Arrays.asList("false");
        return Collections.emptyList();
    }
}
