package nl.dykam.dev.reutil.commands;

public abstract class ParseResult<T> {

    public static final Failure FAILURE_PARSE_RESULT = new Failure();

    public static <T> ParseResult<T> success(T value) {
        return new Success<>(value);
    }

    @SuppressWarnings("unchecked")
    public static <T> ParseResult<T> failure() {
        return FAILURE_PARSE_RESULT;
    }

    public static <T> ParseResult<T> notNull(T value) {
        if(value != null)
            return success(value);
        else
            return failure();
    }

    public static class Success<T> extends ParseResult<T> {
        private final T value;

        private Success(T value) {
            this.value = value;
        }

        public T getValue() {
            return value;
        }
    }

    public static class Failure<T> extends ParseResult<T> {
        private Failure() {

        }
    }
}
