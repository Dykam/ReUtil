package nl.dykam.dev.reutil.data;

import nl.dykam.dev.reutil.ReUtil;
import nl.dykam.dev.reutil.ReUtilPlugin;
import nl.dykam.dev.reutil.data.annotations.Instantiation;
import nl.dykam.dev.reutil.data.annotations.SaveMoment;
import nl.dykam.dev.reutil.events.AutoEventHandler;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class Components {
    private final Plugin plugin;
    private final ComponentStorage storage = new MapComponentStorage();
    private final ComponentConfig componentConfig;

    public Components(Plugin plugin) {
        this.plugin = plugin;
        componentConfig = new ComponentConfig(plugin);
    }

    public <T extends Component> T get(Object object, Class<T> type) {
        T component = storage.get(object, type);
        if(component == null)
            component = getGlobal().storage.get(object, type);
        if(component == null)
            component = ComponentLoader.loadComponent(object, this, type);
        if(component == null && ComponentInfo.getDefaults(type).instantiation() != Instantiation.Manual)
            component = constructAndAdd(this, object, type);
        return component;
    }

    public <T extends Component> T ensure(Object object, Class<T> type) {
        T component = get(object, type);
        if(component == null)
            component = constructAndAdd(this,  object, type);
        return component;
    }

    public <T extends Component> T remove(Object object, Class<T> type) {
        return storage.remove(object, type);
    }

    <T extends Component> void set(Object object, Class<T> type, T component) {
        storage.set(object, type, component);
    }

    public ComponentConfig config() {
        return componentConfig;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    private static <T extends Component> T constructAndAdd(Components context, Object object, Class<T> type) {
        return ComponentBuilder.getBuilder(type).constructAndAdd(context, object);
    }

    private static Map<Plugin, Components> componentsCache = new HashMap<>();

    public <T extends Component> ComponentHandle<T> get(Class<T> type) {
        return new ComponentHandle<>(this, type);
    }

    static {
        componentsCache.put(ReUtilPlugin.instance(), new Components(ReUtilPlugin.instance()));
        ReUtil.registerEvents(new Listeners(), ReUtilPlugin.instance());
    }

    public static Components getGlobal() {
        return componentsCache.get(ReUtilPlugin.instance());
    }

    public static Components get(Plugin plugin) {
        if(!componentsCache.containsKey(plugin))
            componentsCache.put(plugin, new Components(plugin));
        return componentsCache.get(plugin);
    }

    private static class Listeners implements Listener {

        @AutoEventHandler
        private void onPluginDisable(PluginDisableEvent event, Plugin plugin) {
            Components components = componentsCache.remove(plugin);
            if(components == null)
                return;

            for (Component component : components.storage) {
                if(!ArrayUtils.contains(ComponentInfo.getSaveMoments(component), SaveMoment.PluginUnload))
                    continue;

                ComponentLoader.saveComponent(component);
            }

        }
    }
}
