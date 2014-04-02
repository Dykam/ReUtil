package nl.dykam.dev.reutil.data;

import nl.dykam.dev.reutil.data.annotations.Defaults;
import nl.dykam.dev.reutil.data.annotations.Instantiation;
import nl.dykam.dev.reutil.data.annotations.SaveMoment;
import nl.dykam.dev.reutil.utils.TypeUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ComponentHandle<O, T extends Component<O>> {
    private final Class<T> type;
    private final Class<O> objectType;

    private final Defaults defaults;
    private boolean persists;
    private Set<SaveMoment> saveMoments;

    private ComponentManager context;
    private ComponentBuilder<T> builder;
    private ComponentStorage<T> storage;
    private ComponentPersistence<T> persistence;
    private ObjectInfo objectInfo;
    private boolean smartSaving;

    private transient Map<Class<?>, ComponentHandle<O, ? extends Component<O>>> requiredComponentHandles;

    public ComponentHandle(Class<T> type, ComponentManager context) {
        this(type, context, null, null, null, null);
    }

    @SuppressWarnings("unchecked")
    public ComponentHandle(Class<T> type, ComponentManager context, ComponentStorage<T> storage, ComponentPersistence<T> persistence, ComponentBuilder<T> builder, ObjectInfo objectInfo) {
        this.type = type;
        this.context = context;

        this.objectType = (Class<O>) TypeUtils.getTypeArguments(Component.class, type).get(0);

        defaults = ComponentInfo.getDefaults(type);
        persists = ComponentInfo.isPersistant(type);
        saveMoments = ComponentInfo.getSaveMoments(type);
        smartSaving = ComponentInfo.hasSmartPersistency(type);

        setBuilder(builder);
        setStorage(storage);
        setPersistence(persistence);
        setObjectInfo(objectInfo);

        requiredComponentHandles = new HashMap<>();
        for (Class<? extends Component> componentType : ComponentInfo.getRequired(type)) {
            requiredComponentHandles.put(type, context.getHandle(type));
        }

    }

    public T get(O object) {
        T component = storage.get(object);
        if(component != null)
            return component;
        component = load(object);
        if(component != null)
            return component;
        if(defaults.instantiation() == Instantiation.Manual)
            return null;
        return build(object);
    }

    public T load(O object) {
        return putIfNotNull(object, persistence.load(object));
    }

    public T build(O object) {
        return putIfNotNull(object, builder.construct(this, object));
    }

    public void saveAll() {
        for (T component : storage.components()) {
            if(shouldSkipSave(component.getObject()))
                continue;
            persistence.save(component);
        }
    }

    public void save(O object) {
        if(shouldSkipSave(object))
            return;
        T component = storage.get(object);
        if(component != null)
            persistence.save(component);
    }

    private boolean shouldSkipSave(O object) {
        return smartSaving && !getObjectInfo().isPersistentObject(object);
    }

    private T putIfNotNull(O object, T component) {
        if(component == null)
            return null;
        storage.put(object, component);
        return component;
    }

    public Class<T> getType() {
        return type;
    }

    public Defaults getDefaults() {
        return defaults;
    }

    public boolean isPersistent() {
        return persists;
    }

    public Set<SaveMoment> getSaveMoments() {
        return saveMoments;
    }

    public ComponentPersistence<T> getPersistence() {
        return persistence;
    }

    public void setPersistence(ComponentPersistence<T> persistence) {
        this.persistence = persistence != null ? persistence : getDefaultPersistence();
    }

    private ComponentPersistence<T> getDefaultPersistence() {
        if (persists) {
            return new FileComponentPersistence<>(this, type);
        } else {
            return new VoidComponentPersistence<>(type);
        }
    }

    public ComponentStorage<T> getStorage() {
        return storage;
    }

    public void setStorage(ComponentStorage<T> storage) {
        this.storage = storage != null ? storage : new SimpleMapComponentStorage<T>();
    }

    public ComponentBuilder<T> getBuilder() {
        return builder;
    }

    public void setBuilder(ComponentBuilder<T> builder) {
        this.builder = builder != null ? builder : ComponentBuilder.getBuilder(type);
    }

    public void ensure(O object) {
        if(storage.get(object) == null)
            build(object);
    }

    public ObjectInfo getObjectInfo() {
        return objectInfo != null ? objectInfo : context.getObjectInfo();
    }

    public void setObjectInfo(ObjectInfo objectInfo) {
        this.objectInfo = objectInfo;
    }

    public void remove(O object) {
        storage.remove(object);
        persistence.remove(object);
    }

    public Class<O> getObjectType() {
        return objectType;
    }

    public void unload(O object) {
        save(object);
        storage.remove(object);
    }

    public ComponentManager getContext() {
        return context;
    }

    /**
     * Similar to ComponentManager.getHandle, but optimized for @Require'd components
     * @param componentType Class of the component
     * @return The component
     */
    @SuppressWarnings("unchecked")
    public <U extends Component<O>> ComponentHandle<O, U> getHandle(Class<U> componentType) {
        ComponentHandle<O, U> componentHandle = (ComponentHandle<O, U>) requiredComponentHandles.get(componentType);
        if(componentHandle == null)
            componentHandle = getContext().getHandle(componentType);
        return componentHandle;
    }
}
