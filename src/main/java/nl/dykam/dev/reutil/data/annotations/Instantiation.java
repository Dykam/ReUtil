package nl.dykam.dev.reutil.data.annotations;

public enum Instantiation {
    /**
     * Creates the Component the moment the relevant object comes available
     */
    Eager,
    /**
     * Creates the Component the moment it is requested
     */
    Lazy,
    /**
     * The Component has to be constructed and added manually
     */
    Manual,
}
