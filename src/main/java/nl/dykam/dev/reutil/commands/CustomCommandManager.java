package nl.dykam.dev.reutil.commands;

import org.apache.commons.lang.NotImplementedException;
import org.bukkit.plugin.Plugin;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static nl.dykam.dev.reutil.utils.Reflect.getAnnotatedMethods;

public class CustomCommandManager implements ICustomCommandManager {
    Parsers parsers = new Parsers();
    @Override
    public void registerCommands(CommandHandler handler, Plugin plugin) {
        for (Map.Entry<Method, AutoCommand> entry : getAnnotatedMethods(handler, AutoCommand.class).entrySet()) {
            tryRegisterCommand(handler, entry.getKey(), entry.getValue(), plugin);
        }
    }

    @Override
    public <T> void registerArgumentType(Class<T> type, ArgumentParser<T> parser) {
        parsers.putSafe(type, parser);
    }

    @Override
    public <T> void registerArgumentType(String name, Class<T> type, ArgumentParser<T> parser) {
        throw new NotImplementedException();
    }

    private void tryRegisterCommand(CommandHandler handler, Method method, AutoCommand annotation, Plugin plugin) {
        String name = annotation.name().equals("") ? method.getName() : annotation.name();
        CommandParam[] commandParams = parseMethod(method);
    }

    private CommandParam[] parseMethod(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Annotation[][] parametersAnnotations = method.getParameterAnnotations();
        CommandParam[] commandParams = new CommandParam[parameterTypes.length];
        for(int i = 0; i < commandParams.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            Annotation[] parameterAnnotations = parametersAnnotations[i];
            commandParams[i] = parseParam(parameterType, parameterAnnotations);
        }
        return commandParams;
    }

    private CommandParam parseParam(Class<?> parameterType, Annotation[] parameterAnnotations) {
        return null;
    }

    private static class Parsers extends HashMap<Class<?>, ArgumentParser<?>> {
        @SuppressWarnings("unchecked")
        public <T> ArgumentParser<T> putSafe(Class<T> key, ArgumentParser<T> value) {
            return (ArgumentParser<T>) super.put(key, value);
        }
        @SuppressWarnings("unchecked")
        public <T> ArgumentParser<T> put(Class<T> key, ArgumentParser<T> value) {
            throw new UnsupportedOperationException("Use Parsers.putSafe(,)");
        }

        @SuppressWarnings("unchecked")
        public <T> ArgumentParser<T> get(Class<T> key) {
            return (ArgumentParser<T>) super.get(key);
        }
    }
}
