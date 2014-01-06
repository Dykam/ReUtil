package nl.dykam.dev.reutil.data;

import org.bukkit.plugin.java.JavaPlugin;

public class Components {
    Components instance;
    public static Components get(JavaPlugin plugin) {
        return new Components();
    }

    public <T extends Component> T get(Object sender, Class<T> playerScoreComponentClass) {
        return null;
    }
}
