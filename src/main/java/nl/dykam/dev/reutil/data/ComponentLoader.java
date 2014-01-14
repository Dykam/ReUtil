package nl.dykam.dev.reutil.data;

import nl.dykam.dev.reutil.ReUtilPlugin;
import nl.dykam.dev.reutil.data.annotations.Defaults;
import nl.dykam.dev.reutil.data.annotations.ObjectType;
import nl.dykam.dev.reutil.data.annotations.Persistent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.io.File;
import java.util.EnumMap;
import java.util.Map;

abstract class ComponentLoader {
    static Map<ObjectType, ComponentLoader> loaders;

    protected abstract void save(Component component);

    protected abstract <T extends Component> T load(Object object, Components context, Class<T> type);

    protected File getFile(Components context, Class<? extends Component> type, String key) {
        return context.getPlugin().getDataFolder().toPath()
                .resolve(context.config().getLocation())
                .resolve(key)
                .resolve(type.getName() + ".yml").toFile();
    }

    static {
        loaders = new EnumMap<>(ObjectType.class);
        loaders.put(ObjectType.Player, new PlayerComponentLoader());
    }

    static void saveComponent(Component component) {
        ObjectType type = ObjectType.getType(component.getObject());
        loaders.get(type).save(component);
    }

    static <T extends Component> T loadComponent(Components context, Object object, Class<T> componentType) {
        if(!register(componentType))
            return null;
        ObjectType type = ObjectType.getType(object);
        T component = loaders.get(type).load(object, context, componentType);
        component.initialize(object, context);
        return component;
    }

    private static <T extends Component> boolean register(Class<T> componentType) {
        Persistent annotation = componentType.getAnnotation(Persistent.class);
        if(annotation == null)
            return false;
        if(!ConfigurationSerializable.class.isAssignableFrom(componentType)) {
            ReUtilPlugin.getMessage().failure(Bukkit.getConsoleSender(), "ConfigurationSerializable not implemented by @Persistent " + componentType.getName());
            return false;
        }
        ReUtilPlugin.getMessage().success(Bukkit.getConsoleSender(), "Registered " + componentType.getName());
        ConfigurationSerialization.registerClass(componentType.asSubclass(ConfigurationSerializable.class), componentType.getName());
        return true;
    }

    public static <T extends Component> T loadAndAdd(Components context, Object object, Class<T> type) {
        T component = loadComponent(context, object, type);
        if(component == null)
            return null;
        Defaults defaults = ComponentInfo.getDefaults(type);
        Components scope = defaults.global() ? Components.getGlobal() : context;
        scope.set(object, type, component);
        return component;
    }
}