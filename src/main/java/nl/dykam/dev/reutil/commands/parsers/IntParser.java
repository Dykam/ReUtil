package nl.dykam.dev.reutil.commands.parsers;

import nl.dykam.dev.reutil.commands.ArgumentParser;
import nl.dykam.dev.reutil.commands.ParseResult;

import java.util.Collections;
import java.util.List;

public class IntParser implements ArgumentParser<Integer> {
    @Override
    public ParseResult<Integer> parse(String argument) {
        try {
            return ParseResult.success(Integer.parseInt(argument));
        } catch (NumberFormatException ex) {
            return ParseResult.failure();
        }
    }

    @Override
    public List<String> complete(String current) {
        return Collections.emptyList();
    }
}
