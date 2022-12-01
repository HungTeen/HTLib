package hungteen.htlib.impl.raid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.common.world.raid.WaveComponent;
import hungteen.htlib.impl.wave.HTWaveComponents;
import hungteen.htlib.util.interfaces.IRaid;
import hungteen.htlib.util.interfaces.IRaidComponentType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-28 23:37
 **/
public class CommonRaid extends BaseRaid {

    public static final Codec<CommonRaid> CODEC = RecordCodecBuilder.<CommonRaid>mapCodec(instance -> instance.group(
            RaidSettings.CODEC.fieldOf("settings").forGetter(CommonRaid::getRaidSettings),
            HTWaveComponents.getCodec().listOf().fieldOf("waves").forGetter(CommonRaid::getWaveComponents)
    ).apply(instance, CommonRaid::new)).codec();
    private final List<WaveComponent> waveComponents;

    public CommonRaid(RaidSettings raidSettings, List<WaveComponent> waveComponents) {
        super(raidSettings);
        this.waveComponents = waveComponents;
    }

    @Override
    public int getWaveCount(IRaid raid) {
        return this.waveComponents.size();
    }

    @Override
    public @NotNull WaveComponent getCurrentWave(IRaid raid, int currentWave) {
        return this.waveComponents.get(currentWave);
    }

    public List<WaveComponent> getWaveComponents(){
        return this.waveComponents;
    }

    @Override
    public IRaidComponentType<?> getType() {
        return HTRaidComponents.COMMON_RAID_TYPE;
    }
}
