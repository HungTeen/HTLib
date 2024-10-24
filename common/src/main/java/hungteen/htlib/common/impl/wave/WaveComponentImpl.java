package hungteen.htlib.common.impl.wave;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.api.raid.PositionComponent;
import hungteen.htlib.api.raid.WaveComponent;
import hungteen.htlib.common.impl.position.HTLibPositionComponents;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;

import java.util.Optional;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-29 09:08
 **/
public abstract class WaveComponentImpl implements WaveComponent {

    private final WaveSetting waveSetting;

    public WaveComponentImpl(WaveSetting waveSetting) {
        this.waveSetting = waveSetting;
    }

    @Override
    public Optional<PositionComponent> getSpawnPlacement() {
        return getWaveSetting().spawnPlacement().map(Holder::value);
    }

    @Override
    public int getPrepareDuration() {
        return getWaveSetting().prepareDuration();
    }

    @Override
    public int getWaveDuration() {
        return getWaveSetting().waveDuration();
    }

    @Override
    public boolean canSkip() {
        return getWaveSetting().canSkip();
    }

    @Override
    public Optional<SoundEvent> getWaveStartSound() {
        return getWaveSetting().waveStartSound().map(Holder::value);
    }

    public WaveSetting getWaveSetting() {
        return waveSetting;
    }

    public record WaveSetting(Optional<Holder<PositionComponent>> spawnPlacement, int prepareDuration, int waveDuration, boolean canSkip, Optional<Holder<SoundEvent>> waveStartSound){

        /**
         * entityType : 生物的类型，The getSpawnEntities entityType of the entity.
         * nbt : 附加数据，CompoundTag of the entity.
         * placementType : 放置类型，决定放在什么地方，
         * spawnTick : 生成的时间，When to getSpawnEntities the entity.
         * spawnCount : 生成数量，How many entities to getSpawnEntities.
         */
        public static final Codec<WaveSetting> CODEC = RecordCodecBuilder.<WaveSetting>mapCodec(instance -> instance.group(
                Codec.optionalField("spawn_placement", HTLibPositionComponents.getCodec(), true).forGetter(WaveSetting::spawnPlacement),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("prepare_duration", 100).forGetter(WaveSetting::prepareDuration),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("wave_duration", 0).forGetter(WaveSetting::waveDuration),
                Codec.BOOL.optionalFieldOf("can_skip_wave", true).forGetter(WaveSetting::canSkip),
                Codec.optionalField("wave_start_sound", SoundEvent.CODEC, true).forGetter(WaveSetting::waveStartSound)
        ).apply(instance, WaveSetting::new)).codec();
    }
}
