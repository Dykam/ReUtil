package nl.dykam.dev.reutil.data;

import nl.dykam.dev.reutil.ReUtilPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

public class ComponentConfig {
    private static YamlConfiguration yamlConfiguration;
    private final File componentConfig;
    private final YamlConfiguration config;

    Callback updateListener;

    private final Plugin plugin;

    public ComponentConfig(Plugin plugin) {
        this.plugin = plugin;
        this.componentConfig = Paths.get(
                ReUtilPlugin.instance().getDataFolder().getAbsolutePath(),
                "componentConfig",
                plugin.getName() + ".yml").toFile();
        config = YamlConfiguration.loadConfiguration(componentConfig);
        config.getRoot().setDefaults(getDefaults());
        config.options().copyDefaults(true);
        config.options().copyHeader(true);
    }

    public ComponentConfig(Plugin plugin, Callback updateListener) {
        this(plugin);
        this.updateListener = updateListener;
    }

    public int getInterval() {
        return config.getInt("interval");
    }

    public void setInterval(int interval) {
        set("interval", interval);
    }

    public String getLocation() {
        return config.getString("location");
    }

    public void setLocation(String location) {
        set("location", location);
    }

    public Callback getUpdateListener() {
        return updateListener;
    }

    public void setUpdateListener(Callback updateListener) {
        this.updateListener = updateListener;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    private void set(String key, Object value) {
        config.set(key, value);
        try {
            config.save(componentConfig);
        } catch (IOException e) {
            ReUtilPlugin.getMessage().failure(Bukkit.getConsoleSender(), "Failed to store component configuration for " + plugin.getName());
        }
        if(updateListener != null) {
            updateListener.call(key, value, this);
        }
    }

    private static Configuration getDefaults() {
        if(yamlConfiguration == null) {
            InputStream resource = ReUtilPlugin.instance().getResource("componentConfig.yml");
            yamlConfiguration = YamlConfiguration.loadConfiguration(resource);
        }
        return yamlConfiguration;
    }

    public interface Callback {
        void call(String key, Object value, ComponentConfig config);
    }
}
