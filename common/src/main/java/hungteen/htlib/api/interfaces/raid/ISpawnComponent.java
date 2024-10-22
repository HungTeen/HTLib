package hungteen.htlib.api.interfaces.raid;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.Optional;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/18 10:02
 */
public interface ISpawnComponent {

    /**
     * Get spawned entities.
     * @param level Check level.
     * @param raid Current raid.
     * @param tick Current tick.
     * @param startTick Start tick.
     * @return List of spawned entities.
     */
    List<Entity> getSpawnEntities(ServerLevel level, IRaid raid, int tick, int startTick);

    /**
     * Check for skipping this wave.
     * @param tick Current tick.
     * @param startTick Start spawn tick.
     * @return True means can skip.
     */
    boolean finishedSpawn(int tick, int startTick);

    /**
     * Get the method to place the upcoming getSpawnEntities entity.
     * @return placement method.
     */
    Optional<PositionComponent> getSpawnPlacement();

    /**
     * Get Serializer.
     * @return Serialize type.
     */
    SpawnType<?> getType();

}
