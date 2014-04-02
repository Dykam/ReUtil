package nl.dykam.dev.reutil.ticker.annotations;

import nl.dykam.dev.reutil.data.Component;

import java.lang.annotation.*;

/**
 * Indicates that the component has to run before the specified component.
 * Does **not** indicate a dependency.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface After {
    public Class<? extends Component<?>>[] value();
}
