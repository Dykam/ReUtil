package nl.dykam.dev.reutil.data;

import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

interface ComponentStorage {
    public <T extends Component> T get(Object object, Class<T> type);
    public <T extends Component> T get(Player player, Class<T> type);
    public <T extends Component> T get(Chunk chunk, Class<T> type);
    public <T extends Component> T get(Block block, Class<T> type);
    public <T extends Component> void set(Object object, Class<T> type, T component);
    public <T extends Component> void set(Player player, Class<T> type, T component);
    public <T extends Component> void set(Chunk chunk, Class<T> type, T component);
    public <T extends Component> void set(Block block, Class<T> type, T component);
    public <T extends Component> T remove(Object object, Class<T> type);
    public <T extends Component> T remove(Player player, Class<T> type);
    public <T extends Component> T remove(Chunk chunk, Class<T> type);
    public <T extends Component> T remove(Block block, Class<T> type);
}
