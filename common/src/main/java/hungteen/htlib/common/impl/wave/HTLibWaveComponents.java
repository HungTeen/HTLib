package hungteen.htlib.common.impl.wave;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import hungteen.htlib.api.raid.SpawnComponent;
import hungteen.htlib.api.raid.WaveComponent;
import hungteen.htlib.api.raid.PositionComponent;
import hungteen.htlib.api.raid.WaveType;
import hungteen.htlib.api.registry.HTCodecRegistry;
import hungteen.htlib.common.impl.position.HTLibPositionComponents;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.common.impl.spawn.HTLibSpawnComponents;
import hungteen.htlib.util.helper.impl.HTLibHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
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
public interface HTLibWaveComponents {

    HTCodecRegistry<WaveComponent> WAVES = HTRegistryManager.codec(HTLibHelper.prefix("wave"), HTLibWaveComponents::getDirectCodec);

    ResourceKey<WaveComponent> TEST_1 = create("test_1");
    ResourceKey<WaveComponent> TEST_2 = create("test_2");
    ResourceKey<WaveComponent> COMMON_WAVE_1 = create("common_wave_1");
    ResourceKey<WaveComponent> COMMON_WAVE_2 = create("common_wave_2");
    ResourceKey<WaveComponent> COMMON_WAVE_3 = create("common_wave_3");

    static void register(BootstrapContext<WaveComponent> context) {
        final HolderGetter<SpawnComponent> spawns = HTLibSpawnComponents.registry().helper().lookup(context);
        final HolderGetter<PositionComponent> positions = HTLibPositionComponents.registry().helper().lookup(context);
        final Holder<SpawnComponent> creeperSpawns = spawns.getOrThrow(HTLibSpawnComponents.CREEPER_4_8);
        final Holder<SpawnComponent> poweredCreeperSpawns = spawns.getOrThrow(HTLibSpawnComponents.POWERED_CREEPER_3_5);
        final Holder<SpawnComponent> spiderSpawns = spawns.getOrThrow(HTLibSpawnComponents.SPIDER_5);
        final Holder<SpawnComponent> skeletonSpawns = spawns.getOrThrow(HTLibSpawnComponents.LONG_TERM_SKELETON);
        final Holder<SpawnComponent> witherSkeletonSpawns = spawns.getOrThrow(HTLibSpawnComponents.WITHER_SKELETON);
        final Holder<SpawnComponent> diamondZombieSpawns = spawns.getOrThrow(HTLibSpawnComponents.DIAMOND_ZOMBIE_3_6);
        context.register(TEST_1, new CommonWave(
                HTLibWaveComponents.builder().prepare(100).wave(800).skip(false)
                        .placement(positions.getOrThrow(HTLibPositionComponents.TEST)).build(),
                List.of(Pair.of(ConstantInt.of(10), creeperSpawns))
        ));
        context.register(TEST_2, new CommonWave(
                HTLibWaveComponents.builder().prepare(100).wave(800).skip(false)
                        .placement(positions.getOrThrow(HTLibPositionComponents.TEST)).build(),
                List.of(
                        Pair.of(ConstantInt.of(10), spiderSpawns),
                        Pair.of(ConstantInt.of(100), skeletonSpawns)
                )
        ));
        context.register(COMMON_WAVE_1, new CommonWave(
                HTLibWaveComponents.builder().prepare(60).wave(600).skip(true)
                        .placement(positions.getOrThrow(HTLibPositionComponents.COMMON)).build(),
                List.of(
                        Pair.of(ConstantInt.of(10), poweredCreeperSpawns)
                )
        ));
        context.register(COMMON_WAVE_2, new CommonWave(
                HTLibWaveComponents.builder().prepare(60).wave(1200).skip(true)
                        .placement(positions.getOrThrow(HTLibPositionComponents.COMMON)).build(),
                List.of(
                        Pair.of(ConstantInt.of(100), skeletonSpawns),
                        Pair.of(ConstantInt.of(300), witherSkeletonSpawns),
                        Pair.of(ConstantInt.of(600), witherSkeletonSpawns)
                )
        ));
        context.register(COMMON_WAVE_3, new CommonWave(
                HTLibWaveComponents.builder().prepare(60).wave(1800).skip(true)
                        .placement(positions.getOrThrow(HTLibPositionComponents.COMMON)).build(),
                List.of(
                        Pair.of(ConstantInt.of(50), witherSkeletonSpawns),
                        Pair.of(ConstantInt.of(200), diamondZombieSpawns),
                        Pair.of(ConstantInt.of(500), creeperSpawns)
                )
        ));
    }

    static Codec<WaveComponent> getDirectCodec(){
        return HTLibWaveTypes.registry().byNameCodec().dispatch(WaveComponent::getType, WaveType::codec);
    }

    static Codec<Holder<WaveComponent>> getCodec(){
        return registry().getHolderCodec(getDirectCodec());
    }

    static WaveSettingBuilder builder(){
        return new WaveSettingBuilder();
    }

    static ResourceKey<WaveComponent> create(String name) {
        return registry().createKey(HTLibHelper.prefix(name));
    }

    static HTCodecRegistry<WaveComponent> registry() {
        return WAVES;
    }

    class WaveSettingBuilder {

        private Optional<Holder<PositionComponent>> spawnPlacement = Optional.empty();
        private int prepareDuration = 100;
        private int waveDuration = 0;
        private boolean canSkip = true;
        private Optional<Holder<SoundEvent>> waveStartSound = Optional.empty();

        public WaveComponentImpl.WaveSetting build(){
            return new WaveComponentImpl.WaveSetting(spawnPlacement, prepareDuration, waveDuration, canSkip, waveStartSound);
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
