package hungteen.htlib.common.world.raid;

import hungteen.htlib.util.interfaces.IRaid;
import hungteen.htlib.util.interfaces.ISpawnComponentType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/18 10:02
 *
 * 决定了生成的生物的种类
 */
public abstract class SpawnComponent {

    /**
     * spawn entities.
     * @param tick current tick.
     */
    public abstract List<Entity> spawn(ServerLevel level, IRaid raid, int tick);

    /**
     * Check for skipping this wave.
     * @param tick current tick.
     * @return true means can skip.
     */
    public abstract boolean finishedSpawn(int tick);

    /**
     * Get the method to place the upcoming spawn entity.
     * @return placement method.
     */
    @Nullable
    public abstract PlaceComponent getSpawnPlacement();

    /**
     * Get the type of spawn.
     * @return wave type.
     */
    public abstract ISpawnComponentType<?> getType();

}
