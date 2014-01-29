package nl.dykam.dev.reutil.commands.parsing;

import nl.dykam.dev.reutil.ReUtilPlugin;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;

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
        ReUtilPlugin.getMessage().message(Bukkit.getConsoleSender(), "Requesting " + parameterType.getName());
        // Precedence: Named, Named in fallback, Unnamed, Unnamed in fallback
        Registration parser = null;
        parser = getNamedParser(parameterType, parserName);
        if(parser == null && fallback != null)
            parser = fallback.getNamedParser(parameterType, parserName);
        if(parser == null)
            parser = getUnnamedParser(parameterType);
        if(parser == null && fallback != null)
            parser = fallback.getUnnamedParser(parameterType);
        ReUtilPlugin.getMessage().message(Bukkit.getConsoleSender(), (parser == null ? "Failed:" : "Succeeded: ") + parameterType.getName());
        return parser == null ? null : parser.getParser();
    }

    private Registration getUnnamedParser(Class<?> parameterType) {
        return parsers.get(parameterType);
    }

    private Registration getNamedParser(Class<?> parameterType, String parserName) {
        Map<Class<?>, Registration> parsers = getNamedParsers(parserName);
        return parsers != null ? parsers.get(parameterType) : null;
    }

    private Map<Class<?>, Registration> getNamedParsers(String parserName) {
        if(!namedParsers.containsKey(parserName)) {
            namedParsers.put(parserName, new HashMap<Class<?>, Registration>());
        }
        return namedParsers.get(parserName);
    }

    public <T> void register(Class<T> type, ArgumentParser<T> parser) {
        register(type, parser, 0, parsers);
    }

    public <T> void register(String name, Class<T> type, ArgumentParser<T> parser) {
        register(type, parser, 0, parsers);
        Map<Class<?>, Registration> namedParsers = getNamedParsers(name);
        register(type, parser, 0, namedParsers);
    }
    @SuppressWarnings("unchecked")
    private <T> void register(Class<? super T> type, ArgumentParser<T> parser, int depth, Map<Class<?>, Registration> parsers) {
        int arrayDepth = 0;
        while (type.isArray()) {
            arrayDepth++;
            type = (Class<? super T>) type.getComponentType();
        }
        register(type, parser, depth, parsers, arrayDepth);
    }

    @SuppressWarnings("unchecked")
    private <T> void register(Class<?> type, ArgumentParser<T> parser, int depth, Map<Class<?>, Registration> parsers, int arrayDepth) {
        Class<?> actualType = reconstructArray(type, arrayDepth);
        Registration currentParser = parsers.get(actualType);
        if(currentParser != null && currentParser.depth <= depth)
            return;
        parsers.put(actualType, new Registration(parser, depth));
        ReUtilPlugin.getMessage().success(Bukkit.getConsoleSender(), "Registered (" + depth + ")" + actualType.getName());
        Class<?> superclass = type.getSuperclass();
        if(superclass != null)
            register(superclass, parser, depth + 1, parsers, arrayDepth);
        for (Class<?> interfaze : type.getInterfaces()) {
            register(interfaze, parser, depth + 1, parsers, arrayDepth);
        }
    }

    private Class<?> reconstructArray(Class<?> type, int arrayDepth) {
        if(arrayDepth == 0)
            return type;
        try {
            return Class.forName(StringUtils.repeat("[", arrayDepth) + "L" + type.getName() + ";");
        } catch (ClassNotFoundException e) {
            ReUtilPlugin.instance().getLogger().severe("This should never happen, contact the developer");
            e.printStackTrace();
        }
        return type;
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
