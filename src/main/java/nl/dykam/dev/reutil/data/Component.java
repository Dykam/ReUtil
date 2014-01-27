package nl.dykam.dev.reutil.data;

import nl.dykam.dev.reutil.data.annotations.Defaults;
import nl.dykam.dev.reutil.utiils.TypeUtils;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

@Defaults
public abstract class Component<T> {
    private transient WeakReference<T> object;
    private transient Class<?> objectType;
    private transient ComponentManager context;

    public T getObject() {
        return object.get();
    }

    @SuppressWarnings("unchecked")
    final void initialize(Object object, Class<?> objectType, ComponentManager context) {
        this.objectType = objectType;
        this.object = new WeakReference<>((T)object);
        this.context = context;

        onInitialize();
    }

    protected void onInitialize() {}

    public ComponentManager getContext() {
        return context;
    }

    /**
     * The type of the object this Component is meant to support. This can be a superclass of #getObject()
     * @return The type of the object this Component is meant to support
     */
    public Class<?> getObjectType() {
        return objectType;
    }
}
