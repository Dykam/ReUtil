package nl.dykam.dev.reutil.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Ensures that the command sender can see all players in the arguments
 */
@Target({ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface CanSee {
    /**
     * Set to false to negate the effect of the attribute on a parameter if the method defines this as well
     * @return Whether to actually use CanSee
     */
    boolean value() default true;
}
