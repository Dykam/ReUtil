package nl.dykam.dev.reutil;

import nl.dykam.dev.reutil.events.AutoEventHandler;
import nl.dykam.dev.reutil.events.Bind;
import org.bukkit.Bukkit;
import org.bukkit.event.*;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.*;

public class ReUtil {
    public static void registerEvents(Listener listener, Plugin plugin) {
        Set<Method> methods = new HashSet<>();
        methods.addAll(Arrays.asList(listener.getClass().getDeclaredMethods()));
        for (Method method : methods) {
            tryRegisterEvent(listener, method, plugin);
        }
    }

    private static void tryRegisterEvent(Listener listener, Method method, Plugin plugin) {
        AutoEventHandler eventHandler = method.getAnnotation(AutoEventHandler.class);
        if (eventHandler == null) return;

        if(method.isSynthetic()) {
            return;
        }
        if(!method.isAccessible()) {
            method.setAccessible(true);
        }

        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        final Class<?>[] parameterTypes = method.getParameterTypes();
        if(parameterTypes.length == 0 || !Event.class.isAssignableFrom(parameterTypes[0])) {
            Bukkit.getLogger().severe(plugin.getDescription().getFullName() + " attempted to register an invalid AutoEventHandler method signature \"" + method.toGenericString() + "\" in " + listener.getClass());
            return;
        } else if(parameterTypes.length == 1) {
            Bukkit.getLogger().warning(plugin.getDescription().getFullName() + " attempted to register an EventHandler method signature \"" + method.toGenericString() + "\" as AutoEventHandler in " + listener.getClass());
        }

        final Class<? extends Event> eventType = parameterTypes[0].asSubclass(Event.class);
        final Class<? extends Listener> listenerType = listener.getClass().asSubclass(Listener.class);

        final MethodHandle mainHandle;
        try {
            mainHandle = MethodHandles.lookup().unreflect(method);
        } catch (IllegalAccessException e) {
            return;
        }

        List<MethodHandle> handleList = new ArrayList<>();
        for(int i = 1; i < parameterTypes.length; i++) {
            Class<?> type = parameterTypes[i];
            Bind bind = null;
            for (Annotation annotation : parameterAnnotations[i]) {
                if(annotation instanceof Bind) {
                    bind = (Bind) annotation;
                    break;
                }
            }
            if(bind == null) {
                Bukkit.getLogger().severe(plugin.getDescription().getFullName() + " attempted to register an invalid AutoEventHandler method signature \"" + method.toGenericString() + "\" missing in " + listener.getClass()
                        + ": missing @Bind on parameter " + i);
                return;
            }

            MethodHandle handle = getMethod(eventType, type, bind.value());
            if(handle == null) {
                Bukkit.getLogger().severe(plugin.getDescription().getFullName() + " attempted to register an invalid AutoEventHandler method signature \"" + method.toGenericString() + "\" missing in " + listener.getClass()
                        + ": no equivalent getter found for @Bind " + bind.value());
                return;

            }
            handleList.add(handle);
        }
        final MethodHandle[] handles = handleList.toArray(new MethodHandle[handleList.size()]);

        EventExecutor executor = new EventExecutor() {
            @Override
            public void execute(Listener listener, Event event) throws EventException {
                if (!eventType.isAssignableFrom(event.getClass())) {
                    return;
                }
                Object[] params = new Object[handles.length + 2];
                params[0] = listener;
                params[1] = event;
                for(int i = 0; i < handles.length; i++) {
                    try {
                        Object param = handles[i].invokeWithArguments(event);
                        if(!parameterTypes[i + 1].isInstance(param))
                            return; // Incorrect type, ignore event
                        params[i + 2] = param;
                    } catch (Throwable throwable) {
                        throw new EventException(throwable);
                    }
                }
                try {
                    mainHandle.invokeWithArguments(params);
                } catch (Throwable throwable) {
                    throw new EventException(throwable);
                }
            }
        };
        Bukkit.getPluginManager().registerEvent(eventType, listener, eventHandler.priority(), executor, plugin, eventHandler.ignoreCancelled());
    }

    private static MethodHandle getMethod(Class<? extends Event> eventType, Class<?> type, String bindName) {
        for(String name : getPossibleGetNames(bindName)) {
            Method method;
            try {
                method = eventType.getMethod(name);
                // Check inheritance both ways. The second form isEvent guarded with a run-time check if the type isEvent assignable.
                if(!type.isAssignableFrom(method.getReturnType()) && !method.getReturnType().isAssignableFrom(type))
                    continue;
                return MethodHandles.lookup().unreflect(method);
            } catch (NoSuchMethodException | IllegalAccessException e) {
                // Method does not exist or isEvent inaccessible, try next
            }
        }
        return null;
    }

    private static String[] getPossibleGetNames(String name) {
        return new String[] {
            name,
            "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1),
        };
    }
}
