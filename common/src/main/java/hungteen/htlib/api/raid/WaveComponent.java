package hungteen.htlib.api.raid;

import com.mojang.datafixers.util.Pair;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;

import java.util.List;
import java.util.Optional;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/18 10:02
 */
public interface WaveComponent {

    /**
     * Get the wave getSpawnEntities list.
     *
     * @param raid        Current raid.
     * @param currentWave Current wave.
     * @return List of wave getSpawnEntities.
     */
    List<Pair<Integer, SpawnComponent>> getWaveSpawns(HTRaid raid, int currentWave, RandomSource random);

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
    Optional<PositionComponent> getSpawnPlacement();

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
    WaveType<?> getType();

}
