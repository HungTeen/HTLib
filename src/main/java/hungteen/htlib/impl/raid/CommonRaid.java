package hungteen.htlib.impl.raid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.api.interfaces.raid.IWaveComponent;
import hungteen.htlib.impl.wave.HTWaveComponents;
import hungteen.htlib.api.interfaces.raid.IRaid;
import hungteen.htlib.api.interfaces.raid.IRaidComponentType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-28 23:37
 **/
public class CommonRaid extends RaidComponent {

    public static final Codec<CommonRaid> CODEC = RecordCodecBuilder.<CommonRaid>mapCodec(instance -> instance.group(
            RaidSettings.CODEC.fieldOf("settings").forGetter(CommonRaid::getRaidSettings),
            HTWaveComponents.getCodec().listOf().fieldOf("waves").forGetter(CommonRaid::getWaveComponents)
    ).apply(instance, CommonRaid::new)).codec();
    private final List<IWaveComponent> waveComponents;

    public CommonRaid(RaidSettings raidSettings, List<IWaveComponent> waveComponents) {
        super(raidSettings);
        this.waveComponents = waveComponents;
    }

    @Override
    public int getWaveCount(IRaid raid) {
        return this.waveComponents.size();
    }

    @Override
    public @NotNull IWaveComponent getCurrentWave(IRaid raid, int currentWave) {
        return this.waveComponents.get(currentWave);
    }

    public List<IWaveComponent> getWaveComponents(){
        return this.waveComponents;
    }

    @Override
    public IRaidComponentType<?> getType() {
        return HTRaidComponents.COMMON_RAID_TYPE;
    }
}
