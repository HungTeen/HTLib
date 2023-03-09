package hungteen.htlib.common.impl.wave;

import com.mojang.serialization.Codec;
import hungteen.htlib.HTLib;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.htlib.api.interfaces.raid.IPlaceComponent;
import hungteen.htlib.api.interfaces.raid.IWaveComponent;
import hungteen.htlib.api.interfaces.raid.IWaveComponentType;
import hungteen.htlib.util.helper.StringHelper;
import net.minecraft.sounds.SoundEvent;

import java.util.List;
import java.util.Optional;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-28 23:32
 **/
public class HTWaveComponents {

    public static final HTSimpleRegistry<IWaveComponentType<?>> WAVE_TYPES = HTRegistryManager.create(StringHelper.prefix("wave_type"));
    public static final HTCodecRegistry<IWaveComponent> WAVES = HTRegistryManager.create(IWaveComponent.class, "custom_raid/waves", HTWaveComponents::getCodec, true);

    /* Wave types */

    public static final IWaveComponentType<CommonWave> COMMON_WAVE_TYPE = new DefaultWaveType<>("common", CommonWave.CODEC);

    /* Waves */

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

    public static Codec<IWaveComponent> getCodec(){
        return WAVE_TYPES.byNameCodec().dispatch(IWaveComponent::getType, IWaveComponentType::codec);
    }

    public static WaveSettingBuilder builder(){
        return new WaveSettingBuilder();
    }

    protected record DefaultWaveType<P extends IWaveComponent>(String name, Codec<P> codec) implements IWaveComponentType<P> {

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

        private Optional<IPlaceComponent> spawnPlacement = Optional.empty();
        private int prepareDuration = 100;
        private int waveDuration = 0;
        private boolean canSkip = true;
        private Optional<SoundEvent> waveStartSound = Optional.empty();

        public WaveComponent.WaveSettings build(){
            return new WaveComponent.WaveSettings(spawnPlacement, prepareDuration, waveDuration, canSkip, waveStartSound);
        }

        public WaveSettingBuilder placement(IPlaceComponent spawnPlacement){
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
