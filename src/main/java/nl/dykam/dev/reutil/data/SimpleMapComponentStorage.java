package nl.dykam.dev.reutil.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SimpleMapComponentStorage<T extends Component> implements ComponentStorage<T> {
    private HashMap<Object, T> data;

    public SimpleMapComponentStorage() {
        data = new HashMap<>();
    }

    @Override
    public void put(Object object, T component) {
        data.put(object, component);
    }

    @Override
    public T get(Object object) {
        return data.get(object);
    }

    @Override
    public Iterator<Map.Entry<Object, T>> iterator() {
        return data.entrySet().iterator();
    }

    @Override
    public Iterable<T> components() {
        return data.values();
    }
}
