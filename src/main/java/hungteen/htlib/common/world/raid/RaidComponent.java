package hungteen.htlib.common.world.raid;

import hungteen.htlib.util.interfaces.IRaid;
import hungteen.htlib.util.interfaces.IRaidComponentType;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.BossEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/18 10:02
 */
public abstract class RaidComponent {

    /**
     * TODO change by difficulty ?
     * @param raid
     * @return
     */
    public abstract int getWaveCount(IRaid raid);

    /**
     * Get current wave.
     * @return Current Wave.
     */
    @NotNull
    public abstract WaveComponent getCurrentWave(IRaid raid, int currentWave);

    /**
     * Determines the wave getSpawnEntities placement entityType.
     * @return the least high priority getSpawnEntities placement.
     */
    public abstract PlaceComponent getSpawnPlacement();

    /**
     * Get the raid bar title.
     * @return Raid bar title.
     */
    public abstract MutableComponent getRaidTitle();

    /**
     * Get the raid bar color.
     * @return Raid bar color.
     */
    public abstract BossEvent.BossBarColor getRaidColor();

    /**
     * Get the bar title when defeated the raid.
     * @return Victory raid title.
     */
    public abstract MutableComponent getVictoryTitle();

    /**
     * Get the bar title when lost the raid.
     * @return Loss raid title.
     */
    public abstract MutableComponent getLossTitle();

    /**
     * How long will the raid last when victory.
     * @return Last time.
     */
    public abstract int getVictoryDuration();

    /**
     * How long will the raid last when lost.
     * @return Last time.
     */
    public abstract int getLossDuration();

    public abstract double getRaidRange();

    public abstract boolean showRoundTitle();

    public abstract Optional<SoundEvent> getRaidStartSound();

    public abstract Optional<SoundEvent> getWaveStartSound();

    /**
     * Get the entityType of raid.
     * @return raid entityType.
     */
    public abstract IRaidComponentType<?> getType();

    public abstract Optional<SoundEvent> getVictorySound();

    public abstract Optional<SoundEvent> getLossSound();
}
