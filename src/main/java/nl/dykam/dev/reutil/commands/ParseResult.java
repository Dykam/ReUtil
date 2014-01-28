package nl.dykam.dev.reutil.commands;

import org.apache.commons.lang.NotImplementedException;
import org.bukkit.entity.Entity;

public class ParseResult<T> {
    private final T value;
    private final boolean success;

    public ParseResult(T value, boolean success) {
        this.value = value;
        this.success = success;
    }

    public T getValue() {
        return value;
    }

    public boolean isSuccess() {
        return success;
    }

    public static <T> ParseResult<T> success(T value) {
        return new ParseResult<>(value, true);
    }

    @SuppressWarnings("unchecked")
    public static <T> ParseResult<T> failure() {
        return new ParseResult<>(null, false);
    }

    public static <T> ParseResult<T> notNull(T value) {
        if(value != null)
            return success(value);
        else
            return failure();
    }

    public static ParseResult<Entity[]> failure(CommandResult commandResult) {
        throw new NotImplementedException();
    }

    public boolean isFailure() {
        return !success;
    }
}
