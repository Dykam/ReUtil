package nl.dykam.dev.reutil.commands.parsing.parsers;

import nl.dykam.dev.reutil.commands.CommandExecuteContext;
import nl.dykam.dev.reutil.commands.CommandTabContext;
import nl.dykam.dev.reutil.commands.ParseResult;
import nl.dykam.dev.reutil.commands.parsing.ArgumentParser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class FilterArgumentParser<T> extends ArgumentParser<T[]> {
    private final ArgumentParser<? super T[]> superParser;
    private final Class<T> type;

    public FilterArgumentParser(ArgumentParser<? super T[]> superParser, Class<T> type, String defaultName, boolean requiresTarget) {
        super(defaultName, requiresTarget);
        this.superParser = superParser;
        this.type = type;
    }

    public FilterArgumentParser(ArgumentParser<? super T[]> superParser, Class<T> type, String defaultName) {
        super(defaultName);
        this.superParser = superParser;
        this.type = type;
    }

    public FilterArgumentParser(ArgumentParser<? super T[]> superParser, Class<T> type) {
        super(superParser.getDefaultName(), superParser.requiresTarget());
        this.superParser = superParser;
        this.type = type;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ParseResult<T[]> parse(CommandExecuteContext context, String argument) {
        ParseResult<? super T[]> parseResult = superParser.parse(context, argument);
        if(parseResult.isFailure())
            return ParseResult.failure();
        Object value = parseResult.getValue();
        if(value.getClass().isArray()) {
            Object[] values = (Object[]) value;
            List<T> result = new ArrayList<>();
            for (Object o : values) {
                if(type.isInstance(o))
                    result.add((T) o);
            }
            return ParseResult.success(result.toArray((T[]) Array.newInstance(type, result.size())));
        }
        return ParseResult.failure();
    }

    @Override
    public List<String> complete(CommandTabContext context, String current) {
        return superParser.complete(context, current);
    }
}
