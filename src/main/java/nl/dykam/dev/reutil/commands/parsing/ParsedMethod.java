package nl.dykam.dev.reutil.commands.parsing;

import com.google.common.base.Preconditions;
import nl.dykam.dev.reutil.commands.CommandHandler;

import java.lang.invoke.MethodHandle;

public class ParsedMethod {
    private String name;
    private String[] aliases = {};
    private String permission;
    private String permissionMessage;
    private String description;

    private ParsedMethodParam[] params;
    private int senderIndex;
    private CommandHandler handler;
    private MethodHandle method;
    private boolean requiresContext;

    public ParsedMethod(CommandHandler handler, MethodHandle method, String name, String[] aliases, String permission, String permissionMessage, String description, int senderIndex, ParsedMethodParam[] params, boolean requiresContext) {
        this.handler = handler;
        this.method = method;
        this.requiresContext = requiresContext;
        Preconditions.checkElementIndex(senderIndex, params.length, "Sender Parameter Index");
        this.name = name;
        this.aliases = aliases;
        this.permission = permission;
        this.permissionMessage = permissionMessage;
        this.description = description;
        this.senderIndex = senderIndex;
        this.params = params;
    }

    public String getName() {
        return name;
    }

    public String[] getAliases() {
        return aliases;
    }

    public String getPermission() {
        return permission;
    }

    public String getPermissionMessage() {
        return permissionMessage;
    }

    public String getDescription() {
        return description;
    }

    public ParsedMethodParam[] getParams() {
        return params;
    }

    public ParsedMethodParam getSender() {
        return senderIndex >= 0 ? params[senderIndex] : null;
    }

    public MethodHandle getMethod() {
        return method;
    }

    public boolean isRequiresContext() {
        return requiresContext;
    }

    public CommandHandler getHandler() {
        return handler;
    }
}
