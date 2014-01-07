package nl.dykam.dev.reutil;

import nl.dykam.dev.reutil.events.AutoEventHandler;
import nl.dykam.dev.reutil.events.Bind;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReUtil {
    private static Plugin reUtilPlugin;

    private static Plugin getReUtilPlugin() {
        if(reUtilPlugin == null) {
            reUtilPlugin = new ReUtilPlugin();
            Bukkit.getPluginManager().enablePlugin(reUtilPlugin);
        }
        return reUtilPlugin;
    }

    public static void registerEvents(Listener listener, Plugin plugin) {
        for (Method method : listener.getClass().getMethods()) {
            tryRegisterEvent(listener, method, plugin);
        }
        registerEvents(listener, plugin);
    }

    private static void tryRegisterEvent(Listener listener, Method method, Plugin plugin) {
        AutoEventHandler handler;
        if((handler = method.getAnnotation(AutoEventHandler.class)) == null)
            return;
        if(method.isSynthetic())
            return;

        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        final Class<?>[] parameterTypes = method.getParameterTypes();
        if(parameterTypes.length == 0 || Event.class.isAssignableFrom(parameterTypes[0])) {
            Bukkit.getLogger().severe(plugin.getDescription().getFullName() + " attempted to register an invalid AutoEventHandler method signature \"" + method.toGenericString() + "\" in " + listener.getClass());
            return;
        } else if(parameterTypes.length == 1) {
            Bukkit.getLogger().warning(plugin.getDescription().getFullName() + " attempted to register an EventHandler method signature \"" + method.toGenericString() + "\" as AutoEventHandler in " + listener.getClass());
        }

        Class<? extends Event> eventType = parameterTypes[0].asSubclass(Event.class);

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
                Object[] params = new Object[handles.length + 1];
                for(int i = 0; i < handles.length; i++) {
                    try {
                        Object param = handles[i].invokeExact();
                        if(!parameterTypes[i].isInstance(param))
                            return; // Incorrect type, ignore event
                        params[i + 1] = param;
                    } catch (Throwable throwable) {
                        throw new EventException(throwable);
                    }
                }
                try {
                    mainHandle.invokeExact(params);
                } catch (Throwable throwable) {
                    throw new EventException(throwable);
                }
            }
        };
        Bukkit.getPluginManager().registerEvent(eventType, listener, handler.priority(), executor, plugin, handler.ignoreCancelled());
    }

    private static MethodHandle getMethod(Class<? extends Event> eventType, Class<?> type, String bindName) {
        for(String name : getPossibleGetNames(bindName)) {
            Method method;
            try {
                method = eventType.getMethod(name);
                // Check inheritance both ways. The second form is guarded with a run-time check if the type is assignable.
                if(!type.isAssignableFrom(method.getReturnType()) && !method.getReturnType().isAssignableFrom(type))
                    continue;
                return MethodHandles.lookup().unreflect(method);
            } catch (NoSuchMethodException | IllegalAccessException e) {
                // Method does not exist or is inaccessible, try next
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
        registerEvents(listener, getReUtilPlugin());
    }
}
