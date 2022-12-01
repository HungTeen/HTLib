package hungteen.htlib.common.world.raid;

import hungteen.htlib.util.interfaces.IRaid;
import hungteen.htlib.util.interfaces.IWaveComponentType;

import java.util.List;
import java.util.Optional;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/18 10:02
 */
public abstract class WaveComponent {

    /**
     * Get the wave getSpawnEntities list.
     * @return list of wave getSpawnEntities.
     */
    public abstract List<SpawnComponent> getWaveSpawns(IRaid raid, int tick);

    /**
     * How long before the wave start.
     * @return prepare duration.
     */
    public abstract int getPrepareDuration();

    /**
     * How long will the wave last.
     * @return wave duration.
     */
    public abstract int getWaveDuration();

    /**
     * Determines the wave getSpawnEntities placement type.
     * @return the less high priority getSpawnEntities placement.
     */
    public abstract Optional<PlaceComponent> getSpawnPlacement();

    /**
     * Can skip wave duration when finished.
     * @return set true to skip.
     */
    public abstract boolean canSkip();

    /**
     * Get the type of wave.
     * @return wave type.
     */
    public abstract IWaveComponentType<?> getType();

}
