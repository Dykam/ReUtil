package nl.dykam.dev.reutil.data.annotations;

public enum ObjectType {
//    Block,
//    Chunk,
    Player;

    public static ObjectType getType(Object object) {
        if(object instanceof org.bukkit.entity.Player)
            return ObjectType.Player;
//        if(object instanceof org.bukkit.Chunk)
//            return ObjectType.Chunk;
//        if(object instanceof org.bukkit.block.Block)
//            return ObjectType.Block;
        return null;
    }

    public static ObjectType getType(Class<?> type) {
        if(org.bukkit.entity.Player.class.isAssignableFrom(type))
            return ObjectType.Player;
//        if(org.bukkit.Chunk.class.isAssignableFrom(type))
//            return ObjectType.Chunk;
//        if(bukkit.block.Block.class.isAssignableFrom(type))
//            return ObjectType.Block;
        return null;
    }
}
