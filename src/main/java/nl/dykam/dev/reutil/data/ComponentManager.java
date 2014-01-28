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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class ComponentManager {
    private ComponentManager parent;
    private final Plugin plugin;
    private final ComponentConfig componentConfig;
    private final HandleMap handles = new HandleMap();
    private BukkitTask saveIntervalRunner;
    private ObjectInfo objectInfo;
    private static ObjectInfo globalObjectInfo = new ObjectInfo();

    private ComponentManager(ComponentManager parent, Plugin plugin) {
        this.parent = parent;
        this.plugin = plugin;
        componentConfig = new ComponentConfig(plugin, new ConfigCallback());
        setTimeout(componentConfig.getInterval());
    }

    public <O, T extends Component<O>> ComponentHandle<O, T> get(Class<T> type) {
        ComponentHandle<O, T> handle = handles.get(type);
        if(handle != null)
            return handle;
        if(parent != null) {
            handle = parent.handles.get(type);
            if(handle != null)
                return handle;
        }

        return register(type);
    }

    private <O, T extends Component<O>> ComponentHandle<O, T> register(Class<T> type) {
        Defaults defaults = ComponentInfo.getDefaults(type);
        ComponentManager context = defaults.global() ? getGlobal() : this;

        ComponentHandle<O, T> handle = new ComponentHandle<>(type, context);
        context.handles.put(type, handle);
        return handle;
    }

    public <O, T extends Component<O>> T get(O object, Class<T> type) {
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
        componentsCache.put(ReUtilPlugin.instance(), new ComponentManager(null, ReUtilPlugin.instance()));
        ReUtil.registerEvents(new Listeners(), ReUtilPlugin.instance());
    }

    public static ComponentManager getGlobal() {
        return componentsCache.get(ReUtilPlugin.instance());
    }

    public static ComponentManager get(Plugin plugin) {
        if(!componentsCache.containsKey(plugin))
            componentsCache.put(plugin, new ComponentManager(getGlobal(), plugin));
        return componentsCache.get(plugin);
    }

    public ObjectInfo getObjectInfo() {
        return objectInfo != null ? objectInfo : parent != null ? parent.getObjectInfo() : globalObjectInfo;
    }

    public void setObjectInfo(ObjectInfo objectInfo) {
        this.objectInfo = objectInfo;
    }

    private static class Listeners implements Listener {
        @AutoEventHandler
        private void onPluginDisable(PluginDisableEvent event, @Bind("plugin") Plugin plugin) {
            for (ComponentHandle<?, ?> handle : get(plugin).handles.values()) {
                if(ArrayUtils.contains(handle.getSaveMoments(), SaveMoment.PluginUnload))
                    handle.saveAll();
            }
        }

        @SuppressWarnings("unchecked")
        @AutoEventHandler(priority = EventPriority.MONITOR)
        private void onEntityDeath(EntityDeathEvent event, @Bind("entity") LivingEntity entity) {
            if(!getGlobal().getObjectInfo().isPersistentObject(entity))
                return;
            for (ComponentManager componentManager : componentsCache.values()) {
                for (ComponentHandle componentHandle : componentManager.handles.values()) {
                    if(componentHandle.getObjectType().isInstance(entity))
                        componentHandle.remove(entity);
                }
            }
        }

        @SuppressWarnings("unchecked")
        @AutoEventHandler(priority = EventPriority.MONITOR)
        private void onPlayerQuit(PlayerQuitEvent event, @Bind("player") Player player) {
            for (ComponentManager componentManager : componentsCache.values()) {
                for (ComponentHandle componentHandle : componentManager.handles.values()) {
                    if(componentHandle.getObjectType().isInstance(player)) {
                        componentHandle.save(player);
                    }
                }
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
                for (ComponentHandle<?, ?> handle : handles.values()) {
                    if(ArrayUtils.contains(handle.getSaveMoments(), SaveMoment.Interval))
                        handle.saveAll();
                }
            }
        }, interval, interval);
    }

    private static class HandleMap extends HashMap<Class<? extends Component<?>>, ComponentHandle<?, ?>> {
        @SuppressWarnings("unchecked")
        public <O, T extends Component<O>> ComponentHandle<O, T> get(Class<T> key) {
            ComponentHandle<?, ? extends Component<?>> handle = super.get(key);
            return handle == null ? null : (ComponentHandle<O, T>) handle;
        }

        @Override
        public ComponentHandle<?, ? extends Component<?>> put(Class<? extends Component<?>> key, ComponentHandle<?, ? extends Component<?>> value) {
            if(!value.getType().equals(key))
                throw new IllegalClassException(key, value);
            return super.put(key, value);
        }
    }
}
