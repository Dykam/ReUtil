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
}
