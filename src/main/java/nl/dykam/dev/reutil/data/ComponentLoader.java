package nl.dykam.dev.reutil.data;

import nl.dykam.dev.reutil.data.annotations.ObjectType;

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
                .resolve(type.getSimpleName() + ".yml").toFile();
    }

    static {
        loaders = new EnumMap<>(ObjectType.class);
        loaders.put(ObjectType.Player, new PlayerComponentLoader());
    }

    static void saveComponent(Component component) {
        ObjectType type = ObjectType.getType(component.getObject());
        loaders.get(type).save(component);
    }

    static <T extends Component> T loadComponent(Object object, Components context, Class<T> componentType) {
        ObjectType type = ObjectType.getType(object);
        return loaders.get(type).load(object, context, componentType);
    }
}