package nl.dykam.dev.reutil.data;

import nl.dykam.dev.reutil.ReUtilPlugin;
import nl.dykam.dev.reutil.message.Message;
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
    private final Plugin plugin;

    public ComponentConfig(Plugin plugin) {
        this.plugin = plugin;
        this.componentConfig = Paths.get(
                ReUtilPlugin.instance().getDataFolder().getAbsolutePath(),
                "componentConfig",
                plugin.getName() + ".yml").toFile();
        config = YamlConfiguration.loadConfiguration(componentConfig);
        config.getRoot().setDefaults(getDefaults());
    }

    private static Configuration getDefaults() {
        if(yamlConfiguration == null) {
            InputStream resource = ReUtilPlugin.instance().getResource("componentConfig.yml");
            yamlConfiguration = YamlConfiguration.loadConfiguration(resource);
        }
        return yamlConfiguration;
    }

    public void setLocation(String location) {
        config.set("location", location);
        save();
    }

    public String getLocation() {
        return config.getString("location");
    }

    private void save() {
        try {
            config.save(componentConfig);
        } catch (IOException e) {
            ReUtilPlugin.getMessage().failure(Bukkit.getConsoleSender(), "Failed to store component configuration for " + plugin.getName());
        }
    }
}
