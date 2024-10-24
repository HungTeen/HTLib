package hungteen.htlib.common.impl.wave;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.api.raid.HTRaid;
import hungteen.htlib.api.raid.SpawnComponent;
import hungteen.htlib.api.raid.WaveType;
import hungteen.htlib.common.impl.spawn.HTLibSpawnComponents;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;

import java.util.List;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-29 09:14
 **/
public class CommonWave extends WaveComponentImpl {

    /**
     * spawnPlacement : 放置类型
     * prepareDuration : 每波开始前的准备时间
     * waveDuration : 每波的持续时间，设置为0表示无限时间
     * canSkip : 完成后是否能跳过额外时间
     * spawnComponents : 生成部件
     */
    public static final MapCodec<CommonWave> CODEC = RecordCodecBuilder.<CommonWave>mapCodec(instance -> instance.group(
            WaveSetting.CODEC.fieldOf("setting").forGetter(CommonWave::getWaveSetting),
            HTLibSpawnComponents.pairCodec().listOf().fieldOf("spawns").forGetter(CommonWave::getSpawnComponents)
    ).apply(instance, CommonWave::new));

    private final List<Pair<IntProvider, Holder<SpawnComponent>>> spawnComponents;

    public CommonWave(WaveSetting waveSettings, List<Pair<IntProvider, Holder<SpawnComponent>>> spawnComponents) {
        super(waveSettings);
        this.spawnComponents = spawnComponents;
    }

    public List<Pair<IntProvider, Holder<SpawnComponent>>> getSpawnComponents() {
        return spawnComponents;
    }

    @Override
    public List<Pair<Integer, SpawnComponent>> getWaveSpawns(HTRaid raid, int currentWave, RandomSource random) {
        return spawnComponents.stream().map(p -> Pair.of(p.getFirst().sample(random), p.getSecond().value())).toList();
    }

    @Override
    public WaveType<?> getType() {
        return HTLibWaveTypes.COMMON;
    }
}
