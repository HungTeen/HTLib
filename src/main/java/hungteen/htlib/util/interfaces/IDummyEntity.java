package hungteen.htlib.util.interfaces;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-27 20:52
 **/
public interface IDummyEntity {

    boolean isRemoved();

    void setDirty();

    /**
     * Get specify ID.
     * @return ID.
     */
    int getEntityID();

    /**
     * Where this entity is located.
     * @return The location.
     */
    Vec3 getPosition();

    /**
     * Which level this entity stays in.
     * @return level.
     */
    Level getLevel();

}
