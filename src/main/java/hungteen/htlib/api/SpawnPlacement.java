package hungteen.htlib.api;

import hungteen.htlib.api.interfaces.ISpawnPlacementType;
import hungteen.htlib.common.registry.ICodecRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/18 10:02
 */
public abstract class SpawnPlacement implements ICodecRegistry {

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
    public abstract ISpawnPlacementType<?> getType();

}
