package hungteen.htlib.api.interfaces.raid;

import net.minecraft.sounds.SoundEvent;

import java.util.List;
import java.util.Optional;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/18 10:02
 */
public interface IWaveComponent {

    /**
     * Get the wave getSpawnEntities list.
     * @param raid Current raid.
     * @param tick Current tick.
     * @return List of wave getSpawnEntities.
     */
    List<ISpawnComponent> getWaveSpawns(IRaid raid, int tick);

    /**
     * How long before the wave start.
     * @return Prepare duration.
     */
    int getPrepareDuration();

    /**
     * How long will the wave last.
     * @return Wave duration.
     */
    int getWaveDuration();

    /**
     * Determines the wave getSpawnEntities placement entityType.
     * @return The less high priority getSpawnEntities placement.
     */
    Optional<IPlaceComponent> getSpawnPlacement();

    /**
     * Sound when wave starts.
     * @return Empty if no sound is available.
     */
    Optional<SoundEvent> getWaveStartSound();

    /**
     * Can skip wave duration when finished.
     * @return Set true to skip.
     */
    boolean canSkip();

    /**
     * Get the type of wave.
     * @return Wave type.
     */
    IWaveComponentType<?> getType();

}
