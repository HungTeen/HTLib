package hungteen.htlib.impl.wave;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.common.world.raid.PlaceComponent;
import hungteen.htlib.common.world.raid.SpawnComponent;
import hungteen.htlib.impl.placement.HTPlaceComponents;
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
            HTPlaceComponents.getCodec().optionalFieldOf("placement_type", null).forGetter(CommonWave::getSpawnPlacement),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("prepare_duration", 80).forGetter(CommonWave::getPrepareDuration),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("wave_duration", 0).forGetter(CommonWave::getWaveDuration),
            Codec.BOOL.optionalFieldOf("can_skip_wave", true).forGetter(CommonWave::canSkip),
            HTSpawnComponents.getCodec().listOf().fieldOf("spawns").forGetter(CommonWave::getSpawnComponents)
    ).apply(instance, CommonWave::new)).codec();
    private final List<SpawnComponent> spawnComponents;

    public CommonWave(PlaceComponent spawnPlacement, int prepareDuration, int waveDuration, boolean canSkip, List<SpawnComponent> spawnComponents) {
        super(spawnPlacement, prepareDuration, waveDuration, canSkip);
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
