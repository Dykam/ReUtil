package nl.dykam.dev.reutil.data;

import com.google.common.base.Preconditions;
import nl.dykam.dev.reutil.data.annotations.Defaults;

import java.lang.ref.WeakReference;

@Defaults
public abstract class Component<O> {
    private transient WeakReference<O> object;
    private transient Class<?> objectType;
    private transient ComponentManager context;
    private transient ComponentHandle<O, Component<O>> handle;

    public O getObject() {
        return object.get();
    }

    @SuppressWarnings("unchecked")
    final void initialize(Object object, Class<?> objectType, ComponentHandle<?, ?> handle) {
        Preconditions.checkNotNull(object);
        Preconditions.checkNotNull(objectType);
        Preconditions.checkNotNull(context);
        this.objectType = objectType;
        this.object = new WeakReference<>((O)object);
        this.handle = (ComponentHandle<O, Component<O>>)handle;

        onInitialize();
    }

    protected void onInitialize() {}

    public ComponentManager getContext() {
        return handle.getContext();
    }

    public ComponentHandle<O, Component<O>> getHandle() {
        return handle;
    }

    /**
     * The type of the object this Component is meant to support. This can be a superclass of #getObject()
     * @return The type of the object this Component is meant to support
     */
    public Class<?> getObjectType() {
        return objectType;
    }
}
