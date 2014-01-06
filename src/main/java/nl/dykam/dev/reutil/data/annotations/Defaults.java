package nl.dykam.dev.reutil.data.annotations;

public @interface Defaults {
    /**
     * Whether to automatically instantiate the component when requested.
     * @return Whether to automatically instantiate the component when requested
     */
    boolean autoInstance() default true;

    /**
     * Whether multiple instances of this component are allowed on one object.
     * @return Whether multiple instances of this component are allowed on one object
     */
    boolean multiple() default false;

    /**
     * Whether an instance of this component should be shared accross plugins.
     * @return Whether an instance of this component should be shared accross plugins
     */
    boolean global() default true;
}
