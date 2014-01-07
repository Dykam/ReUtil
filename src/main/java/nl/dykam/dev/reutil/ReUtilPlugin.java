package nl.dykam.dev.reutil;

import org.bukkit.plugin.java.JavaPlugin;

public class ReUtilPlugin extends JavaPlugin {
    private static ReUtilPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
    }

    public static ReUtilPlugin instance() {
        return instance;
    }
}
