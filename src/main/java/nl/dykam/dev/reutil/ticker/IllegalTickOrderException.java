package nl.dykam.dev.reutil.ticker;

public class IllegalTickOrderException extends Exception {
    public IllegalTickOrderException(Class<?> type) {
        super("Before and After constraints of component of type " + type.getSimpleName() + " could not be met. Type: " + type.getName());
    }
}
