package nl.dykam.dev.reutil.commands;

public class ParseResult<T> {
    private final T value;
    private final boolean success;
    private final CommandResult commandResult;

    public ParseResult(T value) {
        this.value = value;
        this.success = true;
        commandResult = CommandResult.silent();
    }

    public ParseResult(CommandResult commandResult) {
        this.commandResult = commandResult;
        this.success = false;
        value = null;
    }

    public T getValue() {
        return value;
    }

    public boolean isSuccess() {
        return success;
    }

    public static <T> ParseResult<T> success(T value) {
        return new ParseResult<>(value);
    }

    public static <T> ParseResult<T> failure() {
        return failure(null);
    }

    public static <T> ParseResult<T> failure(CommandResult commandResult) {
        return new ParseResult<>(commandResult);
    }

    public static <T> ParseResult<T> notNull(T value) {
        if(value != null)
            return success(value);
        else
            return failure();
    }

    public boolean isFailure() {
        return !success;
    }

    public CommandResult getCommandResult() {
        return commandResult;
    }
}
