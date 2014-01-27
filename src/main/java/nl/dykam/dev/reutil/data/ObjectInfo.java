package nl.dykam.dev.reutil.data;

import org.bukkit.entity.LivingEntity;

public class ObjectInfo {
    public boolean isPersistentObject(Object object) {
        if(object instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) object;
            return !livingEntity.getRemoveWhenFarAway();
        }
        return true;
    }
}
