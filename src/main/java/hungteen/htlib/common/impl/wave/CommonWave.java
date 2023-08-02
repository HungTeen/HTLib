package hungteen.htlib.common.impl.wave;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.api.interfaces.raid.IRaid;
import hungteen.htlib.api.interfaces.raid.ISpawnComponent;
import hungteen.htlib.api.interfaces.raid.IWaveType;
import hungteen.htlib.common.impl.spawn.HTSpawnComponents;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;

import java.util.List;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-29 09:14
 **/
public class CommonWave extends WaveComponent {

    /**
     * spawnPlacement : 放置类型
     * prepareDuration : 每波开始前的准备时间
     * waveDuration : 每波的持续时间，设置为0表示无限时间
     * canSkip : 完成后是否能跳过额外时间
     * spawnComponents : 生成部件
     */
    public static final Codec<CommonWave> CODEC = RecordCodecBuilder.<CommonWave>mapCodec(instance -> instance.group(
            WaveSetting.CODEC.fieldOf("setting").forGetter(CommonWave::getWaveSetting),
            HTSpawnComponents.pairCodec().listOf().fieldOf("spawns").forGetter(CommonWave::getSpawnComponents)
    ).apply(instance, CommonWave::new)).codec();
    private final List<Pair<IntProvider, Holder<ISpawnComponent>>> spawnComponents;

    public CommonWave(WaveSetting waveSettings, List<Pair<IntProvider, Holder<ISpawnComponent>>> spawnComponents) {
        super(waveSettings);
        this.spawnComponents = spawnComponents;
    }

    public List<Pair<IntProvider, Holder<ISpawnComponent>>> getSpawnComponents() {
        return spawnComponents;
    }

    @Override
    public List<Pair<Integer, ISpawnComponent>> getWaveSpawns(IRaid raid, int currentWave, RandomSource random) {
        return spawnComponents.stream().map(p -> Pair.of(p.getFirst().sample(random), p.getSecond().get())).toList();
    }

    @Override
    public IWaveType<?> getType() {
        return HTWaveTypes.COMMON;
    }
}
