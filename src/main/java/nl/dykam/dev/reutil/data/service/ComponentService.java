package nl.dykam.dev.reutil.data.service;


import nl.dykam.dev.reutil.data.Component;
import nl.dykam.dev.reutil.data.ComponentHandle;

public interface ComponentService<O, T extends Component<O>> {
    void register(ComponentHandle<O, T> handle) throws RegisterFailedException;
    void unregister(ComponentHandle<O, T> handle);
}
