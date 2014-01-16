package nl.dykam.dev.reutil.data;

import nl.dykam.dev.reutil.data.annotations.Defaults;
import nl.dykam.dev.reutil.data.annotations.Instantiation;
import nl.dykam.dev.reutil.data.annotations.SaveMoment;

public class ComponentHandle<T extends Component> {
    private final Class<T> type;
    private final Defaults defaults;

    private boolean persists;

    private SaveMoment[] saveMoments;
    private ComponentManager context;
    private ComponentBuilder<T> builder;
    private ComponentStorage<T> storage;
    private ComponentPersistence<T> persistence;

    public ComponentHandle(Class<T> type, ComponentManager context) {
        this(type, context, null, null, null);
    }

    public ComponentHandle(Class<T> type, ComponentManager context, ComponentStorage<T> storage, ComponentPersistence<T> persistence, ComponentBuilder<T> builder) {
        this.type = type;
        this.context = context;

        defaults = ComponentInfo.getDefaults(type);
        persists = ComponentInfo.isPersistant(type);
        saveMoments = ComponentInfo.getSaveMoments(type);

        setBuilder(builder);
        setStorage(storage);
        setPersistence(persistence);
    }

    public T get(Object object) {
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

    public T load(Object object) {
        return putIfNotNull(object, persistence.load(object));
    }

    public T build(Object object) {
        return putIfNotNull(object, builder.construct(context, object));
    }

    public void saveAll() {
        for (T component : storage.components()) {
            persistence.save(component);
        }
    }

    public void save(Object object) {
        persistence.save(storage.get(object));
    }

    private T putIfNotNull(Object object, T component) {
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

    public boolean isPersists() {
        return persists;
    }

    public SaveMoment[] getSaveMoments() {
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
            return new FileComponentPersistence<>(context, type);
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

    public void ensure(Object object) {
        if(storage.get(object) == null)
            build(object);
    }
}
