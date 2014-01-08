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
    private static StubPlugin stubPlugin = new StubPlugin();

    public static Plugin getPlugin() {
        return stubPlugin;
    }

    public static void registerEvents(Listener listener, Plugin plugin) {
        Set<Method> methods = new HashSet<>();
        methods.addAll(Arrays.asList(listener.getClass().getDeclaredMethods()));
        for (Method method : methods) {
            tryRegisterEvent(listener, method, plugin);
        }
        if(plugin != stubPlugin)
            Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    private static void tryRegisterEvent(Listener listener, Method method, Plugin plugin) {
        boolean ignoreCancelled;
        EventPriority priority;
        EventHandlerAnalyzer eventHandlerAnalyzer = new EventHandlerAnalyzer(method, plugin).invoke();
        if (!eventHandlerAnalyzer.isEvent()) return;
        priority = eventHandlerAnalyzer.getPriority();
        ignoreCancelled = eventHandlerAnalyzer.isIgnoreCancelled();

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
        } else if(parameterTypes.length == 1 && plugin != stubPlugin) {
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
        Bukkit.getPluginManager().registerEvent(eventType, listener, priority, executor, plugin, ignoreCancelled);
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

    public static void registerPersistentEvents(Listener listener) {
        registerEvents(listener, stubPlugin);
    }

    private static class EventHandlerAnalyzer {
        private boolean isEvent;
        private Method method;
        private Plugin plugin;
        private boolean ignoreCancelled;
        private EventPriority priority;

        public EventHandlerAnalyzer(Method method, Plugin plugin) {
            this.method = method;
            this.plugin = plugin;
        }

        boolean isEvent() {
            return isEvent;
        }

        public boolean isIgnoreCancelled() {
            return ignoreCancelled;
        }

        public EventPriority getPriority() {
            return priority;
        }

        public EventHandlerAnalyzer invoke() {
            AutoEventHandler handler;
            EventHandler alternative;

            if((handler = method.getAnnotation(AutoEventHandler.class)) != null) {
                ignoreCancelled = handler.ignoreCancelled();
                priority = handler.priority();
            } else if(plugin == stubPlugin && (alternative = method.getAnnotation(EventHandler.class)) != null) {
                ignoreCancelled = alternative.ignoreCancelled();
                priority = alternative.priority();
            } else {
                isEvent = false;
                return this;
            }
            isEvent = true;
            return this;
        }
    }
}
