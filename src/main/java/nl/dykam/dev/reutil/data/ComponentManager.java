package nl.dykam.dev.reutil.data;

import nl.dykam.dev.reutil.ReUtil;
import nl.dykam.dev.reutil.ReUtilPlugin;
import nl.dykam.dev.reutil.data.annotations.Defaults;
import nl.dykam.dev.reutil.data.annotations.SaveMoment;
import nl.dykam.dev.reutil.events.AutoEventHandler;
import nl.dykam.dev.reutil.events.Bind;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.IllegalClassException;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class ComponentManager {
    private final Plugin plugin;
    private final ComponentConfig componentConfig;
    private final HandleMap handles = new HandleMap();
    private BukkitTask saveIntervalRunner;

    public ComponentManager(Plugin plugin) {
        this.plugin = plugin;
        componentConfig = new ComponentConfig(plugin, new ConfigCallback());
        setTimeout(componentConfig.getInterval());
    }

    public <T extends Component> ComponentHandle<T> get(Class<T> type) {
        ComponentHandle<T> handle = handles.get(type);
        if(handle != null)
            return handle;
        handle = getGlobal().handles.get(type);
        if(handle != null)
            return handle;

        return register(type);
    }

    private <T extends Component> ComponentHandle<T> register(Class<T> type) {
        Defaults defaults = ComponentInfo.getDefaults(type);
        ComponentManager context = defaults.global() ? getGlobal() : this;

        ComponentHandle<T> handle = new ComponentHandle<>(type, context);
        context.handles.put(type, handle);
        return handle;
    }

    public <T extends Component> T get(Object object, Class<T> type) {
        return get(type).get(object);
    }

    public ComponentConfig config() {
        return componentConfig;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public void ensure(Object object, Class<? extends Component> type) {
        get(type).ensure(object);
    }

    private static Map<Plugin, ComponentManager> componentsCache = new HashMap<>();

    static {
        componentsCache.put(ReUtilPlugin.instance(), new ComponentManager(ReUtilPlugin.instance()));
        ReUtil.registerEvents(new Listeners(), ReUtilPlugin.instance());
    }

    public static ComponentManager getGlobal() {
        return componentsCache.get(ReUtilPlugin.instance());
    }

    public static ComponentManager get(Plugin plugin) {
        if(!componentsCache.containsKey(plugin))
            componentsCache.put(plugin, new ComponentManager(plugin));
        return componentsCache.get(plugin);
    }

    private static class Listeners implements Listener {
        @AutoEventHandler
        private void onPluginDisable(PluginDisableEvent event, @Bind("plugin") Plugin plugin) {
            for (ComponentHandle<?> handle : get(plugin).handles.values()) {
                if(ArrayUtils.contains(handle.getSaveMoments(), SaveMoment.PluginUnload))
                    handle.saveAll();
            }
        }
    }

    private class ConfigCallback implements ComponentConfig.Callback {
        @Override
        public void call(String key, Object value, ComponentConfig config) {
            switch (key) {
                case "interval":
                    setTimeout((int) value);
            }
        }
    }

    private void setTimeout(int interval) {
        if(saveIntervalRunner != null)
            saveIntervalRunner.cancel();
        saveIntervalRunner = Bukkit.getScheduler().runTaskTimer(getPlugin(), new Runnable() {
            @Override
            public void run() {
                for (ComponentHandle<?> handle : handles.values()) {
                    if(ArrayUtils.contains(handle.getSaveMoments(), SaveMoment.Interval))
                        handle.saveAll();
                }
            }
        }, interval, interval);
    }

    private static class HandleMap extends HashMap<Class<? extends Component>, ComponentHandle<?>> {
        @SuppressWarnings("unchecked")
        public <T extends Component> ComponentHandle<T> get(Class<T> key) {
            ComponentHandle<?> handle = super.get(key);
            return handle == null ? null : (ComponentHandle<T>) handle;
        }

        @Override
        public ComponentHandle<?> put(Class<? extends Component> key, ComponentHandle<?> value) {
            if(!value.getType().equals(key))
                throw new IllegalClassException(key, value);
            return super.put(key, value);
        }
    }
}
