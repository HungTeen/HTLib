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
     * @param tick current tick.
     * @return List of spawned entities.
     */
    List<Entity> getSpawnEntities(ServerLevel level, IRaid raid, int tick);

    /**
     * Check for skipping this wave.
     * @param tick current tick.
     * @return true means can skip.
     */
    boolean finishedSpawn(int tick);

    /**
     * Get the method to place the upcoming getSpawnEntities entity.
     * @return placement method.
     */
    Optional<IPlaceComponent> getSpawnPlacement();

    /**
     * Get Serializer.
     * @return Serialize type.
     */
    ISpawnComponentType<?> getType();

}
