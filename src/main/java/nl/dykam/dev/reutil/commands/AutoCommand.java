package nl.dykam.dev.reutil.commands;

public @interface AutoCommand {
    public String name() default "";
    public String[] aliases() default {};
    public String permission() default "";
    public String permissionMessage() default "";
    public String description() default "";
}
