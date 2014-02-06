package nl.dykam.dev.reutil.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoCommand {
    public String name() default "";
    public String[] aliases() default {};
    public String permission() default "";
    public String permissionMessage() default "";
    public String description() default "";
    public Class<?>[] children() default {};
}
