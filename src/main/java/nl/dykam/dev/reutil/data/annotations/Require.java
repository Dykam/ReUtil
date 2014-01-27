package nl.dykam.dev.reutil.data.annotations;

import nl.dykam.dev.reutil.data.Component;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Require {
    public Class<? extends Component<?>>[] value();
}
