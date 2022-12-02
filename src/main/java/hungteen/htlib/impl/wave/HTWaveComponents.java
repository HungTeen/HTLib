package hungteen.htlib.impl.wave;

import com.mojang.serialization.Codec;
import hungteen.htlib.HTLib;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.htlib.common.world.raid.PlaceComponent;
import hungteen.htlib.common.world.raid.WaveComponent;
import hungteen.htlib.util.interfaces.IWaveComponentType;
import net.minecraft.sounds.SoundEvent;

import java.util.List;
import java.util.Optional;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-28 23:32
 **/
public class HTWaveComponents {

    public static final HTSimpleRegistry<IWaveComponentType<?>> WAVE_TYPES = HTRegistryManager.create(HTLib.prefix("wave_type"));
    public static final HTCodecRegistry<WaveComponent> WAVES = HTRegistryManager.create(WaveComponent.class, "custom_raid/waves", HTWaveComponents::getCodec);

    /* Spawn types */

    public static final IWaveComponentType<CommonWave> COMMON_WAVE_TYPE = new DefaultWaveType<>("common_wave", CommonWave.CODEC);

    /* Spawns */

//    public static final HTRegistryHolder<SpawnPlacement> DEFAULT = SPAWNS.innerRegister(
//            HTLib.prefix("default"), new CenterAreaPlacement(
//                    Vec3.ZERO, 0, 1, true, 0, true
//            )
//    );

    /**
     * {@link HTLib#HTLib()}
     */
    public static void registerStuffs(){
        List.of(COMMON_WAVE_TYPE).forEach(HTWaveComponents::registerWaveType);
    }

    public static void registerWaveType(IWaveComponentType<?> type){
        WAVE_TYPES.register(type);
    }

    public static Codec<WaveComponent> getCodec(){
        return WAVE_TYPES.byNameCodec().dispatch(WaveComponent::getType, IWaveComponentType::codec);
    }

    public static WaveSettingBuilder builder(){
        return new WaveSettingBuilder();
    }

    protected record DefaultWaveType<P extends WaveComponent>(String name, Codec<P> codec) implements IWaveComponentType<P> {

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getModID() {
            return HTLib.MOD_ID;
        }
    }

    public static class WaveSettingBuilder {

        private Optional<PlaceComponent> spawnPlacement = Optional.empty();
        private int prepareDuration = 100;
        private int waveDuration = 0;
        private boolean canSkip = true;
        private Optional<SoundEvent> waveStartSound = Optional.empty();

        public BaseWave.WaveSettings build(){
            return new BaseWave.WaveSettings(spawnPlacement, prepareDuration, waveDuration, canSkip, waveStartSound);
        }

        public WaveSettingBuilder placement(PlaceComponent spawnPlacement){
            this.spawnPlacement = Optional.ofNullable(spawnPlacement);
            return this;
        }

        public WaveSettingBuilder prepare(int duration){
            this.prepareDuration = duration;
            return this;
        }

        public WaveSettingBuilder wave(int duration){
            this.waveDuration = duration;
            return this;
        }

        public WaveSettingBuilder skip(boolean skip){
            this.canSkip = skip;
            return this;
        }

        public WaveSettingBuilder waveStart(SoundEvent soundEvent){
            this.waveStartSound = Optional.ofNullable(soundEvent);
            return this;
        }

    }

}
