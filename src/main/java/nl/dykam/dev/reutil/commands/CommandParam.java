package nl.dykam.dev.reutil.commands;

import nl.dykam.dev.reutil.commands.parsing.ArgumentParser;
import nl.dykam.dev.reutil.commands.parsing.ParseResult;

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

    public abstract ParseResult<T> parse(CommandExecuteContext context, String value);
    public abstract List<String> complete(CommandTabContext context, String current);

    public abstract class Optional<T> extends CommandParam<T> {
        protected Optional(ArgumentParser<T> parser) {
            super(parser);
        }

        @Override
        public List<String> complete(CommandTabContext context, String current) {
            return getParser().complete(context, current);
        }
    }

    public class DefaultValue<T> extends Optional<T> {
        private final T defaultValue;

        public DefaultValue(ArgumentParser<T> parser, T defaultValue) {
            super(parser);
            this.defaultValue = defaultValue;
        }

        @Override
        public ParseResult<T> parse(CommandExecuteContext context, String value) {
            ParseResult<T> parse = getParser().parse(context, value);
            return parse.isSuccess() ? parse : ParseResult.success(defaultValue);
        }
    }

    public abstract class ContextValue<T> extends Optional<T> {
        protected ContextValue(ArgumentParser<T> parser) {
            super(parser);
        }

        @Override
        public ParseResult<T> parse(CommandExecuteContext context, String value) {
            ParseResult<T> parse = getParser().parse(context, value);
            if (parse.isSuccess()) {
                return parse;
            }
            return getContextValue(context);
        }

        public abstract ParseResult<T> getContextValue(CommandExecuteContext context);
    }
}
