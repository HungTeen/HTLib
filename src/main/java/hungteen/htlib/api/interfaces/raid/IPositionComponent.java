package hungteen.htlib.api.interfaces.raid;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

/**
 * Determines where to spawn entities.
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/18 10:02
 */
public interface IPositionComponent {

    /**
     * Calculate the getSpawnEntities point of entity.
     * @param world Current level of its raid.
     * @param origin Current center of its raid.
     * @return Calculate result.
     */
    Vec3 getPlacePosition(ServerLevel world, Vec3 origin);

    /**
     * Get the type of placement.
     * @return Position type.
     */
    IPositionType<?> getType();

}
