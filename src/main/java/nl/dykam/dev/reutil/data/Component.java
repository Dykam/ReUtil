package nl.dykam.dev.reutil.data;

import nl.dykam.dev.reutil.data.annotations.Defaults;
import nl.dykam.dev.reutil.utiils.TypeUtils;

import java.util.HashSet;
import java.util.Set;

@Defaults
public abstract class Component<T> {
    private transient T object;
    private transient Class<?> objectType;
    private transient ComponentManager context;

    public T getObject() {
        return object;
    }

    @SuppressWarnings("unchecked")
    final void initialize(Object object, Class<?> objectType, ComponentManager context) {
        this.objectType = objectType;
        this.object = (T)object;
        this.context = context;

        onInitialize();
    }

    protected void onInitialize() {}

    public ComponentManager getContext() {
        return context;
    }

    public Class<?> getObjectType() {
        return objectType;
    }
}
