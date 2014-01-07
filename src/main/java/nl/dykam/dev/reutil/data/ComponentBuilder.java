package nl.dykam.dev.reutil.data;

import nl.dykam.dev.reutil.data.annotations.Defaults;
import nl.dykam.dev.reutil.data.annotations.ObjectType;
import org.apache.commons.lang.ArrayUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class ComponentBuilder<T extends Component> {
    private final Class<T> type;
    private final Defaults defaults;
    private final Constructor<T> constructor;
    private final ObjectType[] applicables;
    private final Class<? extends Component>[] required;

    public ComponentBuilder(Class<T> type) {
        this.type = type;
        defaults = ComponentInfo.getDefaults(type);
        applicables = ComponentInfo.getApplicables(type);
        required = ComponentInfo.getRequired(type);
        try {
            constructor = type.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("type has no valid constructor");
        }
        if(!constructor.isAccessible()) {
            // Set accessible, also helps avoid some slow security checks.
            constructor.setAccessible(true);
        }
    }

    private static ComponentMap builders = new ComponentMap();

    @SuppressWarnings("unchecked")
    static <T extends Component> ComponentBuilder<T> getBuilder(Class<T> type) {
        if(!builders.containsKey(type))
            builders.put(type, new ComponentBuilder<>(type));
        return (ComponentBuilder<T>) builders.get(type);
    }

    public T constructAndAdd(Components context, Object object) {
        ObjectType objectType = ObjectType.getType(object);
        if(objectType == null)
            throw new IllegalArgumentException("object has to be a Player, Block or Chunk");
        if(!ArrayUtils.contains(applicables, objectType))
            throw new IllegalArgumentException("Component does not support " + objectType);

        for (Class<? extends Component> requiredComponent : required) {
            context.ensure(object, requiredComponent);
        }

        try {
            T component = constructor.newInstance();
            component.initialize(object, context);
            Components scope = defaults.global() ? Components.getGlobal() : context;
            scope.set(object, type, component);
            return component;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("This should never happen, report to developer.", e);
        }
    }

    /**
     * Help type inference
     */
    private static class ComponentMap extends HashMap<Class<? extends Component>, ComponentBuilder<?>> {

    }
}
