package nl.dykam.dev.reutil.data;

import nl.dykam.dev.reutil.data.annotations.ApplicableTo;
import nl.dykam.dev.reutil.data.annotations.ObjectType;
import org.bukkit.Chunk;

@ApplicableTo(ObjectType.Chunk)
public abstract class ChunkComponent extends Component {
    private Chunk chunk;

    @Override
    protected void onInitialize(Object object) {
        chunk = (Chunk)object;
    }

    @Override
    public Chunk getObject() {
        return chunk;
    }
}
