package nl.dykam.dev.reutil.commands.parsing;

import nl.dykam.dev.reutil.commands.CommandExecuteContext;
import nl.dykam.dev.reutil.commands.CommandTabContext;

import java.util.List;

public abstract class ArgumentParser<T> {
    private final boolean requiresTarget;
    private final String defaultName;

    protected ArgumentParser(String defaultName) {
        this(defaultName, false);
    }

    protected ArgumentParser(String defaultName, boolean requiresTarget) {
        this.defaultName = defaultName;
        this.requiresTarget = requiresTarget;
    }

    public abstract ParseResult<T> parse(CommandExecuteContext context, String argument);
    public abstract List<String> complete(CommandTabContext context, String current);

    public boolean requiresTarget() {
        return requiresTarget;
    }

    public String getDefaultName() { return defaultName; }
}
