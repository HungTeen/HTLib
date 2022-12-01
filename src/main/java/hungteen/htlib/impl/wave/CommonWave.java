package hungteen.htlib.impl.wave;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.common.world.raid.SpawnComponent;
import hungteen.htlib.impl.spawn.HTSpawnComponents;
import hungteen.htlib.util.interfaces.IRaid;
import hungteen.htlib.util.interfaces.IWaveComponentType;

import java.util.List;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-29 09:14
 **/
public class CommonWave extends BaseWave{

    /**
     * spawnPlacement : 放置类型
     * prepareDuration : 每波开始前的准备时间
     * waveDuration : 每波的持续时间，设置为0表示无限时间
     * canSkip : 完成后是否能跳过额外时间
     * spawnComponents : 生成部件
     */
    public static final Codec<CommonWave> CODEC = RecordCodecBuilder.<CommonWave>mapCodec(instance -> instance.group(
            WaveSettings.CODEC.fieldOf("wave_settings").forGetter(CommonWave::getWaveSettings),
            HTSpawnComponents.getCodec().listOf().fieldOf("spawns").forGetter(CommonWave::getSpawnComponents)
    ).apply(instance, CommonWave::new)).codec();
    private final List<SpawnComponent> spawnComponents;

    public CommonWave(WaveSettings waveSettings, List<SpawnComponent> spawnComponents) {
        super(waveSettings);
        this.spawnComponents = spawnComponents;
    }

    public List<SpawnComponent> getSpawnComponents() {
        return spawnComponents;
    }

    @Override
    public List<SpawnComponent> getWaveSpawns(IRaid raid, int tick) {
        return spawnComponents;
    }

    @Override
    public IWaveComponentType<?> getType() {
        return HTWaveComponents.COMMON_WAVE_TYPE;
    }
}
