package nl.dykam.dev.reutil.data;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class ComponentBuilder<T extends Component<?>> {
    private final Constructor<T> constructor;
    private final Class<?> applicableTo;
    private final Class<? extends Component>[] required;

    public ComponentBuilder(Class<T> type) {
        applicableTo = ComponentInfo.getApplicableTo(type);
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
    static <T extends Component<?>> ComponentBuilder<T> getBuilder(Class<T> type) {
        if(!builders.containsKey(type))
            builders.put(type, new ComponentBuilder<>(type));
        return (ComponentBuilder<T>) builders.get(type);
    }

    public T construct(ComponentHandle<?, ?> handle, Object object) {
        if(!applicableTo.isInstance(object))
            throw new IllegalArgumentException("Component does not support object of type " + object.getClass().getSimpleName());

        for (Class<? extends Component> requiredComponent : required) {
            handle.getContext().ensure(object, requiredComponent);
        }

        T component;
        try {
            component = constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("This should never happen, report to developer.", e);
        }
        component.initialize(object, applicableTo, handle);
        return component;
    }

    /**
     * Help type inference
     */
    private static class ComponentMap extends HashMap<Class<? extends Component>, ComponentBuilder<?>> {

    }
}
