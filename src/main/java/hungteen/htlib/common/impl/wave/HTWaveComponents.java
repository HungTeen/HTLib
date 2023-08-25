package hungteen.htlib.common.impl.wave;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.IHTCodecRegistry;
import hungteen.htlib.api.interfaces.raid.IPositionComponent;
import hungteen.htlib.api.interfaces.raid.ISpawnComponent;
import hungteen.htlib.api.interfaces.raid.IWaveComponent;
import hungteen.htlib.api.interfaces.raid.IWaveType;
import hungteen.htlib.common.impl.spawn.HTSpawnComponents;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.util.helper.HTLibHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.valueproviders.ConstantInt;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-28 23:32
 **/
public class HTWaveComponents {

    private static final HTCodecRegistry<IWaveComponent> WAVES = HTRegistryManager.create(HTLibHelper.prefix("wave"), IWaveComponent.class, HTWaveComponents::getDirectCodec, false);

    public static final ResourceKey<IWaveComponent> TEST_1 = create("test_1");
    public static final ResourceKey<IWaveComponent> TEST_2 = create("test_2");

    public static void register(BootstapContext<IWaveComponent> context) {
        final HolderGetter<ISpawnComponent> spawns = HTSpawnComponents.registry().helper().lookup(context);
        final Holder<ISpawnComponent> testSpawn1 = spawns.getOrThrow(HTSpawnComponents.TEST_1);
        final Holder<ISpawnComponent> testSpawn2 = spawns.getOrThrow(HTSpawnComponents.TEST_2);
        final Holder<ISpawnComponent> testSpawn3 = spawns.getOrThrow(HTSpawnComponents.TEST_3);
        context.register(TEST_1, new CommonWave(
                HTWaveComponents.builder().prepare(100).wave(1200).skip(false).build(),
                List.of(Pair.of(ConstantInt.of(10), testSpawn1))
        ));
        context.register(TEST_2, new CommonWave(
                HTWaveComponents.builder().prepare(100).wave(1200).skip(false).build(),
                List.of(Pair.of(ConstantInt.of(10), testSpawn2), Pair.of(ConstantInt.of(100), testSpawn3))
        ));

    }

    public static Codec<IWaveComponent> getDirectCodec(){
        return HTWaveTypes.registry().byNameCodec().dispatch(IWaveComponent::getType, IWaveType::codec);
    }

    public static Codec<Holder<IWaveComponent>> getCodec(){
        return registry().getHolderCodec(getDirectCodec());
    }

    public static WaveSettingBuilder builder(){
        return new WaveSettingBuilder();
    }

    public static ResourceKey<IWaveComponent> create(String name) {
        return registry().createKey(HTLibHelper.prefix(name));
    }

    public static IHTCodecRegistry<IWaveComponent> registry() {
        return WAVES;
    }

    public static class WaveSettingBuilder {

        private Optional<Holder<IPositionComponent>> spawnPlacement = Optional.empty();
        private int prepareDuration = 100;
        private int waveDuration = 0;
        private boolean canSkip = true;
        private Optional<Holder<SoundEvent>> waveStartSound = Optional.empty();

        public WaveComponent.WaveSetting build(){
            return new WaveComponent.WaveSetting(spawnPlacement, prepareDuration, waveDuration, canSkip, waveStartSound);
        }

        public WaveSettingBuilder placement(@NotNull Holder<IPositionComponent> spawnPlacement){
            this.spawnPlacement = Optional.of(spawnPlacement);
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
