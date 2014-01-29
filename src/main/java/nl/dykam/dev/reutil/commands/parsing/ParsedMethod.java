package nl.dykam.dev.reutil.commands.parsing;

import com.google.common.base.Preconditions;

import java.lang.invoke.MethodHandle;

public class ParsedMethod {
    private String name;
    private String[] aliases = {};
    private String permission;
    private String permissionMessage;
    private String description;

    private ParsedMethodParam[] params;
    private int senderIndex;
    private MethodHandle method;

    public ParsedMethod(MethodHandle method, String name, String[] aliases, String permission, String permissionMessage, String description, int senderIndex, ParsedMethodParam[] params) {
        this.method = method;
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
}
