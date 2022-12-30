package hungteen.htlib.impl.wave;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.api.interfaces.raid.IPlaceComponent;
import hungteen.htlib.api.interfaces.raid.IWaveComponent;
import hungteen.htlib.impl.placement.HTPlaceComponents;
import net.minecraft.sounds.SoundEvent;

import java.util.Optional;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-29 09:08
 **/
public abstract class WaveComponent implements IWaveComponent {

    private final WaveSettings waveSettings;

    public WaveComponent(WaveSettings waveSettings) {
        this.waveSettings = waveSettings;
    }

    @Override
    public Optional<IPlaceComponent> getSpawnPlacement() {
        return getWaveSettings().spawnPlacement();
    }

    @Override
    public int getPrepareDuration() {
        return getWaveSettings().prepareDuration();
    }

    @Override
    public int getWaveDuration() {
        return getWaveSettings().waveDuration();
    }

    @Override
    public boolean canSkip() {
        return getWaveSettings().canSkip();
    }

    @Override
    public Optional<SoundEvent> getWaveStartSound() {
        return getWaveSettings().waveStartSound();
    }

    public WaveSettings getWaveSettings() {
        return waveSettings;
    }

    public record WaveSettings(Optional<IPlaceComponent> spawnPlacement, int prepareDuration, int waveDuration, boolean canSkip, Optional<SoundEvent> waveStartSound){

        /**
         * entityType : 生物的类型，The getSpawnEntities entityType of the entity.
         * nbt : 附加数据，CompoundTag of the entity.
         * placementType : 放置类型，决定放在什么地方，
         * spawnTick : 生成的时间，When to getSpawnEntities the entity.
         * spawnCount : 生成数量，How many entities to getSpawnEntities.
         */
        public static final Codec<WaveSettings> CODEC = RecordCodecBuilder.<WaveSettings>mapCodec(instance -> instance.group(
                Codec.optionalField("spawn_placement", HTPlaceComponents.getCodec()).forGetter(WaveSettings::spawnPlacement),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("prepare_duration", 100).forGetter(WaveSettings::prepareDuration),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("wave_duration", 0).forGetter(WaveSettings::waveDuration),
                Codec.BOOL.optionalFieldOf("can_skip_wave", true).forGetter(WaveSettings::canSkip),
                Codec.optionalField("wave_start_sound", SoundEvent.CODEC).forGetter(WaveSettings::waveStartSound)
        ).apply(instance, WaveSettings::new)).codec();
    }
}
