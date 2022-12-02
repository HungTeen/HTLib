package hungteen.htlib.common.world.raid;

import hungteen.htlib.util.interfaces.IRaid;
import hungteen.htlib.util.interfaces.ISpawnComponentType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.Optional;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/18 10:02
 *
 * 决定了生成的生物的种类
 */
public abstract class SpawnComponent {

    /**
     * getSpawnEntities entities.
     * @param tick current tick.
     */
    public abstract List<Entity> getSpawnEntities(ServerLevel level, IRaid raid, int tick);

    /**
     * Check for skipping this wave.
     * @param tick current tick.
     * @return true means can skip.
     */
    public abstract boolean finishedSpawn(int tick);

    /**
     * Get the method to place the upcoming getSpawnEntities entity.
     * @return placement method.
     */
    public abstract Optional<PlaceComponent> getSpawnPlacement();

    /**
     * Get the entityType of getSpawnEntities.
     * @return wave entityType.
     */
    public abstract ISpawnComponentType<?> getType();

}
