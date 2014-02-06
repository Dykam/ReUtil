package nl.dykam.dev.reutil.commands.parsing;

class ParsedMethodParam {
    private final String name;
    private final ArgumentParser<?> parser;
    private final boolean optional;
    private final boolean sender;
    private final boolean canSee;

    public ParsedMethodParam(String name, ArgumentParser<?> parser, boolean optional, boolean sender, boolean canSee) {
        this.name = name;
        this.parser = parser;
        this.optional = optional;
        this.sender = sender;
        this.canSee = canSee;
    }

    public String getName() {
        return name;
    }

    public ArgumentParser<?> getParser() {
        return parser;
    }

    public boolean isOptional() {
        return optional;
    }

    public boolean isSender() {
        return sender;
    }

    public boolean isCanSee() {
        return canSee;
    }

    public Object getUsingName() {
        return optional | sender ? '[' + name + ']' : '<' + name + '>';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParsedMethodParam that = (ParsedMethodParam) o;

        if (canSee != that.canSee) return false;
        if (optional != that.optional) return false;
        if (sender != that.sender) return false;
        if (!name.equals(that.name)) return false;
        if (!parser.equals(that.parser)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + parser.hashCode();
        result = 31 * result + (optional ? 1 : 0);
        result = 31 * result + (sender ? 1 : 0);
        result = 31 * result + (canSee ? 1 : 0);
        return result;
    }
}
