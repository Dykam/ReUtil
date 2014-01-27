package nl.dykam.dev.reutil.commands;

import java.util.List;

public interface ArgumentParser<T> {
    public ParseResult<T> parse(String argument);
    public List<String> complete(String current);
}
