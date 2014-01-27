package nl.dykam.dev.reutil.data;

import java.util.Iterator;
import java.util.Map;

public interface ComponentStorage<T> extends Iterable<Map.Entry<Object, T>> {
    void put(Object object, T component);

    T get(Object object);

    void remove(Object object);

    @Override
    Iterator<Map.Entry<Object, T>> iterator();

    Iterable<T> components();
}
