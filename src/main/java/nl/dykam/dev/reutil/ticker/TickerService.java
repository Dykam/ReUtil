package nl.dykam.dev.reutil.ticker;

import nl.dykam.dev.reutil.data.Component;
import nl.dykam.dev.reutil.data.ComponentHandle;
import nl.dykam.dev.reutil.data.service.ComponentService;
import nl.dykam.dev.reutil.data.service.RegisterFailedException;
import nl.dykam.dev.reutil.ticker.annotations.After;
import nl.dykam.dev.reutil.ticker.annotations.Before;

import java.util.*;

public class TickerService<T extends Component<Object> & Ticking> implements ComponentService<Object, T> {
    List<ComponentHandle<Object, T>> handles = new ArrayList<>();
    @Override
    public void register(ComponentHandle<Object, T> handle) throws RegisterFailedException {
        After after = handle.getType().getAnnotation(After.class);
        Set<Class<? extends Component<?>>> afterTypes = new HashSet<>(Arrays.asList(after.value()));
        Before before = handle.getType().getAnnotation(Before.class);
        Set<Class<? extends Component<?>>> beforeTypes = new HashSet<>(Arrays.asList(before.value()));
        // Loop to find position before everything in before, and after everything in after.
        // Keep track of last found after
        int i = 0, lastInAfterFound = 0;
        boolean beforeFound = false;
        for (ComponentHandle<?, T> componentHandle : handles) {
            beforeFound &= beforeTypes.contains(componentHandle.getType());

            if(afterTypes.contains(componentHandle.getType())) {
                if(beforeFound)
                    throw new RegisterFailedException(new IllegalTickOrderException(handle.getType()));
                lastInAfterFound = i;
            }
            i++;
        }

        handles.add(lastInAfterFound + 1, handle);
    }

    @Override
    public void unregister(ComponentHandle<Object, T> handle) {
        handles.remove(handle);
    }

    private void tick() {
        for (ComponentHandle<Object, T> handle : handles) {
            for (T component : handle.getStorage().components()) {
                component.tick();
            }
        }
    }
}
