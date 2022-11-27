package hungteen.htlib.common.world.raid;

import hungteen.htlib.util.interfaces.IPlaceComponentType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/18 10:02
 *
 * 决定了在哪里生成生物。
 */
public abstract class PlaceComponent {

    /**
     * Calculate the spawn point of entity.
     * @param world current level of its raid.
     * @param origin current center of its raid.
     * @return calculate result.
     */
    public abstract Vec3 getPlacePosition(ServerLevel world, Vec3 origin);

    /**
     * Get the type of placement.
     * @return wave type.
     */
    public abstract IPlaceComponentType<?> getType();

}
