package nl.dykam.dev.reutil.commands.parsing;

import nl.dykam.dev.reutil.commands.CommandExecuteContext;
import nl.dykam.dev.reutil.commands.CommandTabContext;

import java.util.Collections;
import java.util.List;

public abstract class ArgumentParser<T> {
    private final boolean requiresTarget;
    private final String defaultName;
    private final ArgumentParserTree.Type type;

    protected ArgumentParser(String defaultName) {
        this(defaultName, ArgumentParserTree.Type.FREE_SYNTAX);
    }

    protected ArgumentParser(String defaultName, ArgumentParserTree.Type type) {
        this(defaultName, false, type);
    }

    protected ArgumentParser(String defaultName, boolean requiresTarget) {
        this(defaultName, requiresTarget, ArgumentParserTree.Type.FREE_SYNTAX);
    }

    protected ArgumentParser(String defaultName, boolean requiresTarget, ArgumentParserTree.Type type) {
        this.defaultName = defaultName;
        this.requiresTarget = requiresTarget;
        this.type = type;
    }

    public abstract ParseResult<T> parse(CommandExecuteContext context, String argument, String name);
    public List<String> complete(CommandTabContext context, String current) {
        return Collections.emptyList();
    }

    public boolean requiresTarget() {
        return requiresTarget;
    }

    public String getDefaultName() { return defaultName; }

    public ArgumentParserTree.Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArgumentParser that = (ArgumentParser) o;

        if (requiresTarget != that.requiresTarget) return false;
        if (!defaultName.equals(that.defaultName)) return false;
        if (type != that.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (requiresTarget ? 1 : 0);
        result = 31 * result + defaultName.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }
}
