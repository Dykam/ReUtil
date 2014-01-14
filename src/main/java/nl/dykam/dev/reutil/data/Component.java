package nl.dykam.dev.reutil.data;

import nl.dykam.dev.reutil.data.annotations.ApplicableTo;
import nl.dykam.dev.reutil.data.annotations.Defaults;

@Defaults
@ApplicableTo
public abstract class Component {
    private transient Object object;
    private transient Components context;

    void initialize(Object object, Components context) {
        this.object = object;
        this.context = context;

        onInitialize(object);
    }

    protected abstract void onInitialize(Object object);

    public Object getObject() {
        return object;
    }

    public Components getContext() {
        return context;
    }
}
