ReUtil
======

REusable UTILity library - Advanced utilities for Bukkit

This toolset skips past the usual things, and adds features making your code cleaner and more to the point. Currently the major modules are:

- Components
- Commands
- Events

Commands and Events provide a layer on top of Bukkit's own Command functionality, taking away some of the error and type handling. Components add functionality that is greatly underappreciated: attaching data to just about everything (in a safe way, too). And it even gets saved! Here are some examples of each submodule

## Commands
Suppose you want to add a simple `/tp` command. Important is that the `<from>` has to be optional. If omitted, it has to be the one doing `/tp`. This is what you would write normally:
```java
@AutoCommand(
    aliases = {"teleport"},
    description = "Teleports <from> to <to>"
)
public void onCommand(CommandSender sender, Command command, String label, String[] args) {
    if(command.getName().equals("tp")) {
            Player from, to;
            if(args.length == 0) {
                sender.sendMessage("Not enough arguments");
                return false;
            } else if(args.length > 2) {
                sender.sendMessage("Too many arguments");
                return false;
            } else if(args.length == 1) {
                if(!(sender instanceof Player)) {
                    sender.sendMessage("You are not a player. Specify one or execute ingame");
                    return false;
                }
                from = (Player)sender;
                to = Bukkit.getPlayerExact(args[0]);
                if(to == null) {
                    sender.sendMessage("Player " + args[0] + " not found");
                    return false;
                }
            } else if(args.length == 2) {
                from = Bukkit.getPlayerExact(args[0]);
                if(from == null) {
                    sender.sendMessage("Player " + args[0] + " not found");
                    return false;
                }
                to = Bukkit.getPlayerExact(args[1]);
                if(to == null) {
                    sender.sendMessage("Player " + args[1] + " not found");
                    return false;
                }
            }
            from.teleport(to);
            return true;
    }
    return false;
}
```

That is a *lot* of code, just to handle all the wrong input. And that doesn't even include what you need in the `plugin.yml`. Here is the equivalent using ReUtil's `@AutoCommand`:
```java
@CanSee
@AutoCommand(
    aliases = {"teleport"},
    description = "Teleports <from> to <to>"
)
public void tp(@Sender @Name("from") Player from, @Name("to") Player to) {
    from.teleport(to);
}
```

The 'usage' is generated on the fly, and all arguments are dynamically parsed based on the methods' signature. Note the use of `@CanSee`. This forces it so any `Player` specified must also be visible to the executing person. `@Sender` notes that this parameter can also be the CommandSender, if necessary and possible. `@Name` is to give it a nice name, if you omit it it will use the default name for `Player` arguments.

## Events
*Todo: Expand past simple and useless code demo*
```java
@AutoEventHandler
private void onPlayerHitPlayer(EntityDamageByEntityEvent event, @Bind("entity") Player damaged, @Bind("damager") Player damager) {
    damaged.sendMessage(damager.getName() + " damaged out!");
}
```

## Components
*Todo*

---
Future plans at my [Trello development board](https://trello.com/c/SB8qfvcI)
