package hungteen.htlib.api.interfaces;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Global dummy entity on logical side, 无形的全局虚拟实体。
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-27 20:52
 **/
public interface IDummyEntity {

    /**
     * Used to determine whether remove ths entity, 决定是否移除此实体。
     * @return true to remove, false to keep alive.
     */
    boolean isRemoved();

    /**
     * Use this method to save data, 用这个方法来保存数据。
     */
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
     * @return Level.
     */
    Level getLevel();

}
