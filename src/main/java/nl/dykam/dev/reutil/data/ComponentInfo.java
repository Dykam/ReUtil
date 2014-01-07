package nl.dykam.dev.reutil.data;

import nl.dykam.dev.reutil.data.annotations.ApplicableTo;
import nl.dykam.dev.reutil.data.annotations.Defaults;
import nl.dykam.dev.reutil.data.annotations.ObjectType;
import nl.dykam.dev.reutil.data.annotations.Require;

class ComponentInfo {
    public static <T extends Component> Defaults getDefaults(Class<T> type) {
        return type.getAnnotation(Defaults.class);
    }

    private static <T extends Component>  ComponentBuilder<T> getBuilder(Class<T> type) {
        return ComponentBuilder.getBuilder(type);
    }

    public static <T extends Component> ObjectType[] getApplicables(Class<T> type) {
        return type.getAnnotation(ApplicableTo.class).value();
    }
    public static <T extends Component> Class<? extends Component>[] getRequired(Class<T> type) {
        return type.getAnnotation(Require.class).value();
    }
}
