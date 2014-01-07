package nl.dykam.dev.reutil.events;

import org.bukkit.event.EventPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

@java.lang.annotation.Target({ElementType.METHOD})
@java.lang.annotation.Retention(RetentionPolicy.RUNTIME)
public @interface AutoEventHandler {
    EventPriority priority() default EventPriority.NORMAL;
    boolean ignoreCancelled() default false;
}
