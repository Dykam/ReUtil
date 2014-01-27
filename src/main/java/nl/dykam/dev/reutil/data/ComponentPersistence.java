package nl.dykam.dev.reutil.data;

public interface ComponentPersistence<T extends Component<?>> {
    T load(Object object);
    void save(T component);

    boolean remove(Object object);
}
