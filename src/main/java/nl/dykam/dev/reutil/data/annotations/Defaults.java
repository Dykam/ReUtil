package nl.dykam.dev.reutil.data.annotations;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Defaults {

    /**
     * When to construct the Component.
     * @return When to construct the Component
     */
    Instantiation instantiation() default Instantiation.Lazy;

    /**
     * Whether an instance of this component should be shared accross plugins.
     * @return Whether an instance of this component should be shared accross plugins
     */
    boolean global() default true;

    /**
     * How long a Component will exist.
     * @return How long a Component will exist.
     */
    Lifespan lifespan() default Lifespan.Object;
}
