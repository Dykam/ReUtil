package nl.dykam.dev.reutil.data;

import nl.dykam.dev.reutil.ReUtilPlugin;
import nl.dykam.dev.reutil.data.annotations.Persistent;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.NotSerializableException;
import java.util.HashSet;
import java.util.Set;

public class FileComponentPersistence<T extends Component> implements ComponentPersistence<T> {
    private ComponentManager context;
    private Class<T> type;
    private static Set<Class<? extends Component>> registred = new HashSet<>();

    public FileComponentPersistence(ComponentManager context, Class<T> type) {
        try {
            register(type);
        } catch (NotSerializableException e) {
            throw new IllegalArgumentException("component", e);
        }
        this.context = context;
        this.type = type;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T load(Object object) {
        File file = getFile(context, type, object);
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        if(configuration == null) {
            return null;
        }
        T component;
        try {
            component = (T) configuration.get(type.getName());
        } catch (ClassCastException ex) {
            ReUtilPlugin.getMessage().warn(Bukkit.getConsoleSender(), "Failed to load component of " + context.getPlugin().getName());
            return null;
        }
        if(component != null)
            component.initialize(object, context);
        return component;
    }

    @Override
    public void save(T component) {
        ComponentManager context = component.getContext();
        File file = getFile(context, component.getClass(), component.getObject());
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.set(component.getClass().getName(), component);
        try {
            configuration.save(file);
        } catch (IOException e) {
            ReUtilPlugin.getMessage().failure(Bukkit.getConsoleSender(), "Failed to save component of " + context.getPlugin().getName());
        }
    }

    protected static File getFile(ComponentManager context, Class<? extends Component> type, Object object) {
        return context.getPlugin().getDataFolder().toPath()
                .resolve(context.config().getLocation())
                .resolve(getKey(object))
                .resolve(type.getName() + ".yml").toFile();
    }

    protected static String getKey(Object object) {
        if(object instanceof Entity) {
            Entity entity = (Entity)object;
            String category = "entity";
            if(object instanceof Player) {
                category = "players";
            } else if(object instanceof Monster) {
                category = "monster";
            } else if(object instanceof LivingEntity) {
                category = "mob";
            }
            return category + "/" + entity.getUniqueId();
        } else if(object instanceof Block) {
            Block block = (Block)object;
            return "block" + "/" + block.getX() + "," + block.getY() + "," + block.getZ();
        }
        throw new NotImplementedException("Only entities and blocks are supported");
    }

    private static <T extends Component> void register(Class<T> type) throws NotSerializableException {
        if(registred.contains(type))
            return;
        checkPersistable(type);
        ReUtilPlugin.getMessage().success(Bukkit.getConsoleSender(), "Registered " + type.getName());
        ConfigurationSerialization.registerClass(type.asSubclass(ConfigurationSerializable.class), type.getName());
        registred.add(type);
    }

    private static <T extends Component> void checkPersistable(Class<T> type) throws NotSerializableException {
        Persistent annotation = type.getAnnotation(Persistent.class);
        if(annotation == null)
            throw new NotSerializableException("@Persistent not available on  " + type.getName());
        if(!ConfigurationSerializable.class.isAssignableFrom(type)) {
            throw new NotSerializableException("ConfigurationSerializable not implemented by @Persistent " + type.getName());
        }
    }
}
