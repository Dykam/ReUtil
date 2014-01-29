package nl.dykam.dev.reutil.commands;

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
}
