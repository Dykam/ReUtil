package nl.dykam.dev.reutil.ticker.common;

import nl.dykam.dev.reutil.data.EntityComponent;
import nl.dykam.dev.reutil.ticker.Ticking;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class PreviousStateTickerComponent extends EntityComponent implements Ticking {
    private Vector velocity;
    private Location location;

    public Vector getVelocity() {
        return velocity;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public void tick() {
        Entity entity = getObject();
        velocity = entity.getVelocity();
        location = entity.getLocation();
    }
}
