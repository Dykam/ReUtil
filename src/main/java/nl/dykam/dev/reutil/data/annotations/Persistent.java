package nl.dykam.dev.reutil.data.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Persistent {
    SaveMoment[] value() default { SaveMoment.PluginUnload };

    /**
     * Indicates whether or not to save according to the entity's persistency property.
     * This prevents saving data for temporary entities which cannot be tracked
     * @return Whether to consider entity persistency when saving.
     */
    boolean smart() default true;
}
