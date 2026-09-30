package hungteen.htlib.common.impl.raid;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.api.interfaces.raid.ISpawnComponent;
import hungteen.htlib.api.interfaces.raid.IWaveComponent;
import hungteen.htlib.common.impl.wave.HTWaveComponents;
import hungteen.htlib.api.interfaces.raid.IRaid;
import hungteen.htlib.api.interfaces.raid.IRaidType;
import hungteen.htlib.common.world.raid.AbstractRaid;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-28 23:37
 **/
public class CommonRaid extends RaidComponent {

    public static final Codec<CommonRaid> CODEC = RecordCodecBuilder.<CommonRaid>mapCodec(instance -> instance.group(
            RaidSetting.CODEC.fieldOf("setting").forGetter(CommonRaid::getRaidSettings),
            HTWaveComponents.getCodec().listOf().fieldOf("waves").forGetter(CommonRaid::getWaveComponents)
    ).apply(instance, CommonRaid::new)).codec();
    private final List<Holder<IWaveComponent>> waveComponents;

    public CommonRaid(RaidSetting raidSettings, List<Holder<IWaveComponent>> waveComponents) {
        super(raidSettings);
        this.waveComponents = waveComponents;
    }

    @Override
    public int getWaveCount(IRaid raid) {
        return this.waveComponents.size();
    }

    @Override
    public @NotNull IWaveComponent getCurrentWave(IRaid raid, int currentWave) {
        return this.waveComponents.get(currentWave).get();
    }

    @Override
    public List<Pair<Integer, ISpawnComponent>> getCurrentWaveSpawns(AbstractRaid abstractRaid,
        IWaveComponent currentWaveComponent, int currentWave, RandomSource random) {
        if (currentWaveComponent != null) {
            // 当前波次生成什么取决于波次怎么设置的
            return currentWaveComponent.getWaveSpawns(abstractRaid, currentWave, random);
        }
        return null;
    }

    public List<Holder<IWaveComponent>> getWaveComponents() {
        return this.waveComponents;
    }

    @Override
    public IRaidType<?> getType() {
        return HTRaidTypes.COMMON;
    }

}
