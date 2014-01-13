package nl.dykam.dev.reutil.data;

import nl.dykam.dev.reutil.data.annotations.*;

import java.lang.reflect.Array;

class ComponentInfo {

    public static final ObjectType[] ALL_OBJECT_TYPES = ObjectType.values();
    @SuppressWarnings("unchecked")
    public static final Class<? extends Component>[] NO_CLASSES = (Class<? extends Component>[]) Array.newInstance(Class.class, 0);
    private static final SaveMoment[] DEFAULT_SAVE_MOMENTS = {SaveMoment.PluginUnload};

    public static <T extends Component> Defaults getDefaults(Class<T> type) {
        return type.getAnnotation(Defaults.class);
    }

    private static <T extends Component>  ComponentBuilder<T> getBuilder(Class<T> type) {
        return ComponentBuilder.getBuilder(type);
    }

    public static <T extends Component> ObjectType[] getApplicables(Class<T> type) {
        ApplicableTo annotation = type.getAnnotation(ApplicableTo.class);
        return annotation == null ? ALL_OBJECT_TYPES : annotation.value();
    }

    @SuppressWarnings("unchecked")
    public static <T extends Component> Class<? extends Component>[] getRequired(Class<T> type) {
        Require annotation = type.getAnnotation(Require.class);
        return annotation == null ? NO_CLASSES : annotation.value();
    }

    public static SaveMoment[] getSaveMoments(Component component) {
        Persistent annotation = component.getClass().getAnnotation(Persistent.class);
        return annotation == null ? DEFAULT_SAVE_MOMENTS : annotation.value();
    }

    public static Defaults getDefaults(Component component) {
        return getDefaults(component.getClass());
    }
}
