package nl.dykam.dev.reutil.data;

public class ComponentHandle<T extends Component> {
    private final Components components;
    private final Class<T> type;

    public ComponentHandle(Components components, Class<T> type) {
        this.components = components;
        this.type = type;
    }

    public T get(Object object) {
        return components.get(object, type);
    }
}
