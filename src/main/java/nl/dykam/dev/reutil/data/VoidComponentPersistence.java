package nl.dykam.dev.reutil.data;

/**
 * Represents a storage which doesn't story anything.
 * @param <T> The type of the component to store
 */
public class VoidComponentPersistence<T extends Component<?>> implements ComponentPersistence<T> {
    public VoidComponentPersistence(Class<T> type) {

    }

    @Override
    public T load(Object object) {
        return null;
    }

    @Override
    public void save(T component) {

    }
}
