package hungteen.htlib.common.world.raid;

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
     * Get the wave spawn list.
     * @return list of wave spawn.
     */
    public abstract List<SpawnComponent> getWaveSpawns();

    /**
     * Determines the wave spawn placement type.
     * @return the less high priority spawn placement.
     */
    public abstract Optional<PlaceComponent> getSpawnPlacement();

    /**
     * Get the type of wave.
     * @return wave type.
     */
    public abstract IWaveComponentType<?> getType();

}
