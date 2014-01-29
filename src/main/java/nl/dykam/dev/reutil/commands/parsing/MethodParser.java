package nl.dykam.dev.reutil.commands.parsing;

import nl.dykam.dev.reutil.commands.*;
import org.bukkit.entity.Player;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

public class MethodParser {
    private final ArgumentParserRegistry registry;

    public MethodParser(ArgumentParserRegistry registry) {
        this.registry = registry;
    }

    public ParsedMethod parse(CommandHandler handler, Method method) {
        AutoCommand annotation = method.getAnnotation(AutoCommand.class);
        String name = annotation.name().equals("") ? method.getName() : annotation.name();
        boolean defaultCanSee = method.isAnnotationPresent(CanSee.class) && method.getAnnotation(CanSee.class).value();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Class<?>[] parameterTypes = method.getParameterTypes();

        if (!void.class.equals(method.getReturnType()) && !Boolean.class.equals(method.getReturnType()) && !boolean.class.equals(method.getReturnType()) && !CommandResult.class.isAssignableFrom(method.getReturnType())) {
            throw new IllegalStateException("An AutoCommand must return either void, a boolean or a CommandResult");
        }

        int senderIndex = -1;
        boolean parsedAnOptional = false; // Used to check if sender is the first optional parameter.
        boolean requiresContext = parameterTypes[0].equals(CommandExecuteContext.class);
        int offset = requiresContext ? 1 : 0;
        ParsedMethodParam[] methodParams = new ParsedMethodParam[parameterTypes.length - offset];
        for(int i = offset; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            Annotation[] parameterAnnotation = parameterAnnotations[i];
            ParsedMethodParam methodParam = parseParam(parameterType, parameterAnnotation, defaultCanSee);
            methodParams[i - offset] = methodParam;
            if(methodParam.isSender() && senderIndex < 0) {
                if(parsedAnOptional)
                    throw new IllegalStateException("@Sender must be the first optional argument");
                senderIndex = i - offset;
            }
            parsedAnOptional |= methodParam.isOptional();
        }

        if(!method.isAccessible())
            method.setAccessible(true);

        MethodHandle handle;
        try {
            handle = MethodHandles.lookup().unreflect(method);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to extract handle from method", e);
        }

        return new ParsedMethod(handler, handle, name, annotation.aliases(), annotation.permission(), annotation.permissionMessage(), annotation.description(), senderIndex, methodParams, requiresContext);
    }

    /**
     * Parsers a parameter. It uses a bunch of fallbacks internally to allow easy usage
     * @param parameterType The type of the parameter
     * @param parameterAnnotation The annotations of the parameter
     * @param canSee Whether canSee is in effect. Only effective when parameterType is a Player
     * @return
     */
    private ParsedMethodParam parseParam(Class<?> parameterType, Annotation[] parameterAnnotation, boolean canSee) {
        String name = null;
        String parserName = null;
        boolean optional = false;
        boolean sender = false;
        for (Annotation annotation : parameterAnnotation) {
            if(annotation instanceof CanSee) {
                if(!Player.class.isAssignableFrom(parameterType))
                    throw new IllegalStateException("CanSee can only be applied to parameters of type Player");
                canSee = ((CanSee) annotation).value();
            } else if(annotation instanceof Name) {
                name = ((Name) annotation).value();
            } else if(annotation instanceof Parse) {
                parserName = ((Parse) annotation).value();
            } else if(annotation instanceof Sender) {
                if(!Player.class.isAssignableFrom(parameterType))
                    throw new IllegalStateException("Sender can only be applied to parameters of type Player");
                sender = true;
            } else if(annotation instanceof Optional) {
                optional = true;
            }
        }

        if(parserName == null)
            parserName = name;

        ArgumentParser<?> parser = registry.getParser(parameterType, parserName);
        if(parser == null)
            throw new IllegalStateException("No ArgumentParser found for " + parameterType.getSimpleName());

        if(name == null) {
            name = sender ? "target" : parser.getDefaultName();
        }

        return new ParsedMethodParam(name, parser, optional, sender, canSee);
    }
}
