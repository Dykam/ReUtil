package nl.dykam.dev.reutil.commands;

import java.util.List;

/**
* Created by Dykam on 22-1-14.
*/
abstract class CommandParam<T> {
    private ArgumentParser<T> parser;

    CommandParam(ArgumentParser<T> parser) {
        this.parser = parser;
    }

    public ArgumentParser<T> getParser() {
        return parser;
    }

    public abstract ParseResult<T> parse(String value, ParseContext context);
    public abstract List<String> complete(String current);

    public abstract class Optional<T> extends CommandParam<T> {

        protected Optional(ArgumentParser<T> parser) {
            super(parser);
        }

        @Override
        public List<String> complete(String current) {
            return getParser().complete(current);
        }
    }

    public class DefaultValue<T> extends Optional<T> {
        private final T defaultValue;

        public DefaultValue(ArgumentParser<T> parser, T defaultValue) {
            super(parser);
            this.defaultValue = defaultValue;
        }

        @Override
        public ParseResult<T> parse(String value, ParseContext context) {
            ParseResult<T> parse = getParser().parse(value);
            return parse instanceof ParseResult.Success ? parse : ParseResult.success(defaultValue);
        }
    }

    public abstract class ContextValue<T> extends Optional<T> {
        protected ContextValue(ArgumentParser<T> parser) {
            super(parser);
        }

        @Override
        public ParseResult<T> parse(String value, ParseContext context) {
            ParseResult<T> parse = getParser().parse(value);
            if (parse instanceof ParseResult.Success) {
                return parse;
            }
            return getContextValue(context);
        }

        public abstract ParseResult<T> getContextValue(ParseContext context);
    }
}
