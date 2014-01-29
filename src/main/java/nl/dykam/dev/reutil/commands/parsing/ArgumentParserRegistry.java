package nl.dykam.dev.reutil.commands.parsing;

import nl.dykam.dev.reutil.commands.ArgumentParser;

import java.util.HashMap;
import java.util.Map;

public class ArgumentParserRegistry {
    private ArgumentParserRegistry fallback;
    private Map<String, Map<Class<?>, Registration>> namedParsers;
    private Map<Class<?>, Registration> parsers;

    public ArgumentParserRegistry() {
        namedParsers = new HashMap<>();
        parsers = new HashMap<>();
    }

    public ArgumentParser<?> getParser(Class<?> parameterType, String parserName) {
        // Precedence: Named, Named in fallback, Unnamed, Unnamed in fallback
        Registration parser = null;
        parser = getNamedParser(parameterType, parserName);
        if(parser == null && fallback != null)
            parser = fallback.getNamedParser(parameterType, parserName);
        if(parser == null)
            parser = getUnnamedParser(parameterType);
        if(parser == null && fallback != null)
            parser = fallback.getUnnamedParser(parameterType);
        return parser.getParser();
    }

    private Registration getUnnamedParser(Class<?> parameterType) {
        return parsers.get(parameterType);
    }

    private Registration getNamedParser(Class<?> parameterType, String parserName) {
        Map<Class<?>, Registration> parsers = namedParsers.get(parserName);
        return parsers != null ? parsers.get(parameterType) : null;
    }

    public <T> void register(Class<T> type, ArgumentParser<T> parser) {
        register(type, parser, 0, parsers);
    }

    public <T> void register(String name, Class<T> type, ArgumentParser<T> parser) {
        register(type, parser, 0, parsers);
        Map<Class<?>, Registration> namedParsers = this.namedParsers.get(name);
        if(namedParsers != null)
            register(type, parser, 0, namedParsers);
    }

    @SuppressWarnings("unchecked")
    private <T> void register(Class<? super T> type, ArgumentParser<T> parser, int depth, Map<Class<?>, Registration> parsers) {
        Registration currentParser = parsers.get(type);
        if(currentParser.depth <= depth)
            return;
        parsers.put(type, new Registration(parser, depth));
        Class<? super T> superclass = type.getSuperclass();
        if(superclass != null)
            register(superclass, parser, depth + 1, parsers);
        for (Class<?> interfaze : type.getInterfaces()) {
            register((Class<? super T>) interfaze, parser, depth + 1, parsers);
        }
    }

    public ArgumentParserRegistry getFallback() {
        return fallback;
    }

    public void setFallback(ArgumentParserRegistry fallback) {
        this.fallback = fallback;
    }

    private class Registration {
        ArgumentParser<?> parser;
        int depth;

        private Registration(ArgumentParser<?> parser, int depth) {

            this.parser = parser;
            this.depth = depth;
        }

        public ArgumentParser<?> getParser() {
            return parser;
        }

        public int getDepth() {
            return depth;
        }
    }
}
