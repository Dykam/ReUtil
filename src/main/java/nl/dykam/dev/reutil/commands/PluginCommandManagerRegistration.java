package nl.dykam.dev.reutil.commands;

import nl.dykam.dev.reutil.data.Component;
import org.bukkit.plugin.Plugin;

class PluginCommandManagerRegistration extends Component<Plugin> {
    private CommandManager commandManager;

    @Override
    protected void onInitialize() {
        commandManager = new CommandManager(getObject());
        commandManager.setFallback(CommandManager.getGlobal());
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }
}
