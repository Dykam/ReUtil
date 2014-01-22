package nl.dykam.dev.reutil.data;

import nl.dykam.dev.reutil.data.annotations.ApplicableTo;
import nl.dykam.dev.reutil.data.annotations.ObjectType;
import org.bukkit.entity.Player;

@ApplicableTo(ObjectType.Player)
public abstract class PlayerComponent extends Component {
    private transient Player player;

    @Override
    protected void onInitialize(Object object) {
        player = (Player)object;
    }

    @Override
    public Player getObject() {
        return player;
    }
}
