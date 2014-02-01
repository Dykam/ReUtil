package nl.dykam.dev.reutil.commands.parsing;

public class ParseResult<T> extends ExecuteResult {
    private final T value;

    private ParseResult(T value) {
        super(true, null);
        this.value = value;
    }

    private ParseResult(String message) {
        super(false, message);
        value = null;
    }

    public T getValue() {
        return value;
    }

    public <U> ParseResult<U> retypeFailure() {
        if(isSuccess())
            throw new IllegalStateException("Can't retype a success-parseresult");
        return failure(getMessage());
    }

    public static <T> ParseResult<T> success(T value) {
        return new ParseResult<>(value);
    }

    public static <T> ParseResult<T> failure() {
        return failure(null);
    }

    public static <T> ParseResult<T> failure(String message) {
        return new ParseResult<>(message);
    }

    public static <T> ParseResult<T> notNull(T value) {
        return notNull(value, null);
    }

    public static <T> ParseResult<T> notNull(T value, String failureMessage) {
        if(value != null)
            return success(value);
        else
            return failure(failureMessage);
    }

    public static <T> ParseResult<T> noTarget() {
        return failure("No target supplied. Execute either ingame or provide a user.");
    }
}
