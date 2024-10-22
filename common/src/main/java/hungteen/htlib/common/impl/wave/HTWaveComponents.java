package hungteen.htlib.common.impl.wave;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import hungteen.htlib.api.registry.HTCodecRegistry;
import hungteen.htlib.api.interfaces.raid.PositionComponent;
import hungteen.htlib.api.interfaces.raid.ISpawnComponent;
import hungteen.htlib.api.interfaces.raid.IWaveComponent;
import hungteen.htlib.api.interfaces.raid.WaveType;
import hungteen.htlib.common.impl.position.HTPositionComponents;
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
public interface HTWaveComponents {

    HTCodecRegistry<IWaveComponent> WAVES = HTRegistryManager.create(HTLibHelper.prefix("wave"), HTWaveComponents::getDirectCodec);

    ResourceKey<IWaveComponent> TEST_1 = create("test_1");
    ResourceKey<IWaveComponent> TEST_2 = create("test_2");
    ResourceKey<IWaveComponent> COMMON_WAVE_1 = create("common_wave_1");
    ResourceKey<IWaveComponent> COMMON_WAVE_2 = create("common_wave_2");
    ResourceKey<IWaveComponent> COMMON_WAVE_3 = create("common_wave_3");

    static void register(BootstapContext<IWaveComponent> context) {
        final HolderGetter<ISpawnComponent> spawns = HTSpawnComponents.registry().helper().lookup(context);
        final HolderGetter<PositionComponent> positions = HTPositionComponents.registry().helper().lookup(context);
        final Holder<ISpawnComponent> creeperSpawns = spawns.getOrThrow(HTSpawnComponents.CREEPER_4_8);
        final Holder<ISpawnComponent> poweredCreeperSpawns = spawns.getOrThrow(HTSpawnComponents.POWERED_CREEPER_3_5);
        final Holder<ISpawnComponent> spiderSpawns = spawns.getOrThrow(HTSpawnComponents.SPIDER_5);
        final Holder<ISpawnComponent> skeletonSpawns = spawns.getOrThrow(HTSpawnComponents.LONG_TERM_SKELETON);
        final Holder<ISpawnComponent> witherSkeletonSpawns = spawns.getOrThrow(HTSpawnComponents.WITHER_SKELETON);
        final Holder<ISpawnComponent> diamondZombieSpawns = spawns.getOrThrow(HTSpawnComponents.DIAMOND_ZOMBIE_3_6);
        context.register(TEST_1, new CommonWave(
                HTWaveComponents.builder().prepare(100).wave(800).skip(false)
                        .placement(positions.getOrThrow(HTPositionComponents.TEST)).build(),
                List.of(Pair.of(ConstantInt.of(10), creeperSpawns))
        ));
        context.register(TEST_2, new CommonWave(
                HTWaveComponents.builder().prepare(100).wave(800).skip(false)
                        .placement(positions.getOrThrow(HTPositionComponents.TEST)).build(),
                List.of(
                        Pair.of(ConstantInt.of(10), spiderSpawns),
                        Pair.of(ConstantInt.of(100), skeletonSpawns)
                )
        ));
        context.register(COMMON_WAVE_1, new CommonWave(
                HTWaveComponents.builder().prepare(60).wave(600).skip(true)
                        .placement(positions.getOrThrow(HTPositionComponents.COMMON)).build(),
                List.of(
                        Pair.of(ConstantInt.of(10), poweredCreeperSpawns)
                )
        ));
        context.register(COMMON_WAVE_2, new CommonWave(
                HTWaveComponents.builder().prepare(60).wave(1200).skip(true)
                        .placement(positions.getOrThrow(HTPositionComponents.COMMON)).build(),
                List.of(
                        Pair.of(ConstantInt.of(100), skeletonSpawns),
                        Pair.of(ConstantInt.of(300), witherSkeletonSpawns),
                        Pair.of(ConstantInt.of(600), witherSkeletonSpawns)
                )
        ));
        context.register(COMMON_WAVE_3, new CommonWave(
                HTWaveComponents.builder().prepare(60).wave(1800).skip(true)
                        .placement(positions.getOrThrow(HTPositionComponents.COMMON)).build(),
                List.of(
                        Pair.of(ConstantInt.of(50), witherSkeletonSpawns),
                        Pair.of(ConstantInt.of(200), diamondZombieSpawns),
                        Pair.of(ConstantInt.of(500), creeperSpawns)
                )
        ));
    }

    static Codec<IWaveComponent> getDirectCodec(){
        return HTWaveTypes.registry().byNameCodec().dispatch(IWaveComponent::getType, WaveType::codec);
    }

    static Codec<Holder<IWaveComponent>> getCodec(){
        return registry().getHolderCodec(getDirectCodec());
    }

    static WaveSettingBuilder builder(){
        return new WaveSettingBuilder();
    }

    static ResourceKey<IWaveComponent> create(String name) {
        return registry().createKey(HTLibHelper.prefix(name));
    }

    static HTCodecRegistry<IWaveComponent> registry() {
        return WAVES;
    }

    class WaveSettingBuilder {

        private Optional<Holder<PositionComponent>> spawnPlacement = Optional.empty();
        private int prepareDuration = 100;
        private int waveDuration = 0;
        private boolean canSkip = true;
        private Optional<Holder<SoundEvent>> waveStartSound = Optional.empty();

        public WaveComponent.WaveSetting build(){
            return new WaveComponent.WaveSetting(spawnPlacement, prepareDuration, waveDuration, canSkip, waveStartSound);
        }

        public WaveSettingBuilder placement(@NotNull Holder<PositionComponent> spawnPlacement){
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
