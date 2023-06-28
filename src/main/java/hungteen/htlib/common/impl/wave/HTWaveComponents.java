package hungteen.htlib.common.impl.wave;

import com.mojang.serialization.Codec;
import hungteen.htlib.HTLib;
import hungteen.htlib.api.interfaces.raid.IPositionComponent;
import hungteen.htlib.api.interfaces.raid.IWaveComponent;
import hungteen.htlib.api.interfaces.raid.IWaveType;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;

import java.util.Optional;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-28 23:32
 **/
public class HTWaveComponents {

//    public static final HTCodecRegistry<IWaveComponent> WAVES = HTRegistryManager.create(IWaveComponent.class, "custom_raid/waves", HTWaveComponents::getCodec, true);

//    public static final HTRegistryHolder<SpawnPlacement> DEFAULT = SPAWNS.innerRegister(
//            HTLib.prefix("default"), new CenterAreaPlacement(
//                    Vec3.ZERO, 0, 1, true, 0, true
//            )
//    );

    public static Codec<IWaveComponent> getCodec(){
        return HTWaveTypes.registry().byNameCodec().dispatch(IWaveComponent::getType, IWaveType::codec);
    }

    public static WaveSettingBuilder builder(){
        return new WaveSettingBuilder();
    }

    protected record DefaultWaveType<P extends IWaveComponent>(String name, Codec<P> codec) implements IWaveType<P> {

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

        private Optional<IPositionComponent> spawnPlacement = Optional.empty();
        private int prepareDuration = 100;
        private int waveDuration = 0;
        private boolean canSkip = true;
        private Optional<Holder<SoundEvent>> waveStartSound = Optional.empty();

        public WaveComponent.WaveSettings build(){
            return new WaveComponent.WaveSettings(spawnPlacement, prepareDuration, waveDuration, canSkip, waveStartSound);
        }

        public WaveSettingBuilder placement(IPositionComponent spawnPlacement){
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
            this.waveStartSound = Optional.of(Holder.direct(soundEvent));
            return this;
        }

    }

}
