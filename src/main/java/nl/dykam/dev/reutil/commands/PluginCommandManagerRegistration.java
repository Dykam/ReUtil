package nl.dykam.dev.reutil.commands;

import nl.dykam.dev.reutil.data.Component;
import org.bukkit.plugin.Plugin;

class PluginCommandManagerRegistration extends Component<Plugin> {
    private CustomCommandManager commandManager;

    @Override
    protected void onInitialize() {
        commandManager = new CustomCommandManager(getObject());
        commandManager.setFallback(CommandManager.getGlobal());
    }

    public CustomCommandManager getCommandManager() {
        return commandManager;
    }
}
