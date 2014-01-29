package nl.dykam.dev.reutil.commands;

import org.bukkit.entity.Player;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

class MethodParser {
    private final ArgumentParserRegistry registry;

    public MethodParser(ArgumentParserRegistry registry) {
        this.registry = registry;
    }

    public ParsedMethod parse(Method method) {
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
        ParsedMethodParam[] methodParams = new ParsedMethodParam[parameterTypes.length];
        for(int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            Annotation[] parameterAnnotation = parameterAnnotations[i];
            ParsedMethodParam methodParam = parseParam(parameterType, parameterAnnotation, defaultCanSee);
            methodParams[i] = methodParam;
            if(methodParam.isSender() && senderIndex < 0) {
                if(parsedAnOptional)
                    throw new IllegalStateException("@Sender must be the first optional argument");
                senderIndex = i;
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

        return new ParsedMethod(handle, name, annotation.aliases(), annotation.permission(), annotation.permissionMessage(), annotation.description(), senderIndex, methodParams);
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

        if(name == null)
            name = parser.getDefaultName();

        return new ParsedMethodParam(name, parser, optional, sender, canSee);
    }
}
