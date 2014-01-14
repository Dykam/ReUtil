package nl.dykam.dev.reutil.data;

import nl.dykam.dev.reutil.ReUtilPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

class PlayerComponentLoader extends ComponentLoader {
    @Override
    protected void save(Component component) {
        Components context = component.getContext();
        Player player = (Player)component.getObject();
        File file = getFile(context, player, component.getClass());
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.set(component.getClass().getName(), component);
        try {
            configuration.save(file);
        } catch (IOException e) {
            ReUtilPlugin.getMessage().failure(Bukkit.getConsoleSender(), "Failed to save component of " + context.getPlugin().getName());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T extends Component> T load(Object object, Components context, Class<T> type) {
        Player player = (Player)object;
        File file = getFile(context, player, type);
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        if(configuration == null)
            return null;
        try {
            return (T) configuration.get(type.getName());
        } catch (ClassCastException ex) {
            ReUtilPlugin.getMessage().warn(Bukkit.getConsoleSender(), "Failed to load component of " + context.getPlugin().getName());
            return null;
        }
    }

    private File getFile(Components context, Player player, Class<? extends Component> type) {
        String key = player.getUniqueId().toString();
        return getFile(context, type, key);
    }
}
