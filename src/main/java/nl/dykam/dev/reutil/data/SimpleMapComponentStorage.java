package nl.dykam.dev.reutil.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

public class SimpleMapComponentStorage<T> implements ComponentStorage<T> {
    private WeakHashMap<Object, T> data;

    public SimpleMapComponentStorage() {
        data = new WeakHashMap<>();
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
    public void remove(Object object) {
        data.remove(object);
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
