package nl.dykam.dev.reutil.data;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;

class MapComponentStorage implements ComponentStorage {
    // Quick&Dirty.
    Map<Object, ComponentMap> map = new HashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Component> T get(Object object, Class<T> type) {
        return (T) getComponentMap(object).get(type);
    }

    @SuppressWarnings("unchecked")
    private ComponentMap getComponentMap(Object object) {
        if(!map.containsKey(object))
            map.put(object, new ComponentMap());
        return map.get(object);
    }

    @Override
    public <T extends Component> T get(Player player, Class<T> type) {
        return get((Object)player, type);
    }

    @Override
    public <T extends Component> T get(Chunk chunk, Class<T> type) {
        return get((Object)chunk, type);
    }

    @Override
    public <T extends Component> T get(Block block, Class<T> type) {
        return get((Object)block, type);
    }

    @Override
    public <T extends Component> void set(Object object, Class<T> type, T component) {
        ComponentMap componentMap = getComponentMap(object);
        componentMap.put(type, component);
    }

    @Override
    public <T extends Component> void set(Player player, Class<T> type, T component) {
        set((Object)player, type, component);
    }

    @Override
    public <T extends Component> void set(Chunk chunk, Class<T> type, T component) {
        set((Object)chunk, type, component);
    }

    @Override
    public <T extends Component> void set(Block block, Class<T> type, T component) {
        set((Object)block, type, component);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Component> T remove(Object object, Class<T> type) {
        ComponentMap componentMap = getComponentMap(object);
        return (T) componentMap.remove(type);
    }

    @Override
    public <T extends Component> T remove(Player player, Class<T> type) {
        return remove((Object)player, type);
    }

    @Override
    public <T extends Component> T remove(Chunk chunk, Class<T> type) {
        return remove((Object)chunk, type);
    }

    @Override
    public <T extends Component> T remove(Block block, Class<T> type) {
        return remove((Object)block, type);
    }

    @Override
    public Iterator<Component> iterator() {
        return new Iterator<Component>() {
            private Iterator<ComponentMap> mapIterator;
            private Iterator<Component> componentIterator;

            {
                mapIterator = map.values().iterator();
            }

            @Override
            public boolean hasNext() {
                if(componentIterator != null && componentIterator.hasNext())
                    return true;
                if(!mapIterator.hasNext())
                    return false;
                componentIterator = mapIterator.next().values().iterator();
                return hasNext();
            }

            @Override
            public Component next() {
                if(!hasNext())
                    throw new NoSuchElementException();
                return componentIterator.next();
            }

            @Override
            public void remove() {
                if(!hasNext())
                    throw new NoSuchElementException();
                componentIterator.remove();
            }
        };
    }

    /**
     * Help the type inference
     */
    private static class ComponentMap extends HashMap<Class<? extends Component>, Component> {

    }
}
