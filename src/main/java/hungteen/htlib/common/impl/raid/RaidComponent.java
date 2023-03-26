package hungteen.htlib.common.impl.raid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.common.impl.placement.HTPlaceComponents;
import hungteen.htlib.common.impl.result.HTResultComponents;
import hungteen.htlib.common.world.raid.AbstractRaid;
import hungteen.htlib.api.interfaces.raid.IResultComponent;
import hungteen.htlib.api.interfaces.raid.IPlaceComponent;
import hungteen.htlib.api.interfaces.raid.IRaidComponent;
import hungteen.htlib.util.helper.ColorHelper;
import hungteen.htlib.util.helper.StringHelper;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.BossEvent;

import java.util.List;
import java.util.Optional;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-28 23:37
 **/
public abstract class RaidComponent implements IRaidComponent {

    public static final Codec<BossEvent.BossBarColor> BOSS_BAR_COLOR_CODEC = ExtraCodecs.stringResolverCodec(BossEvent.BossBarColor::getName, BossEvent.BossBarColor::byName);
    private final RaidSetting raidSettings;

    public RaidComponent(RaidSetting raidSettings){
        this.raidSettings = raidSettings;
    }

    public RaidSetting getRaidSettings(){
        return this.raidSettings;
    }

    @Override
    public IPlaceComponent getSpawnPlacement() {
        return getRaidSettings().placeComponent();
    }

    @Override
    public int getVictoryDuration() {
        return getRaidSettings().victoryDuration();
    }

    @Override
    public int getLossDuration() {
        return getRaidSettings().lossDuration();
    }

    @Override
    public double getRaidRange() {
        return getRaidSettings().borderSetting().raidRange();
    }

    @Override
    public boolean blockInside() {
        return getRaidSettings().borderSetting().blockInside();
    }

    @Override
    public boolean blockOutside() {
        return getRaidSettings().borderSetting().blockOutside();
    }

    @Override
    public boolean renderBorder() {
        return getRaidSettings().borderSetting().renderBorder();
    }

    @Override
    public int getBorderColor() {
        return getRaidSettings().borderSetting().borderColor();
    }

    @Override
    public boolean showRoundTitle() {
        return getRaidSettings().showRoundTitle();
    }

    @Override
    public List<IResultComponent> getResultComponents() {
        return getRaidSettings().resultComponents();
    }

    @Override
    public MutableComponent getRaidTitle() {
        return getRaidSettings().barSetting().raidTitle();
    }

    @Override
    public BossEvent.BossBarColor getBarColor() {
        return getRaidSettings().barSetting().raidColor();
    }

    @Override
    public MutableComponent getVictoryTitle() {
        return getRaidSettings().barSetting().victoryTitle();
    }

    @Override
    public MutableComponent getLossTitle() {
        return getRaidSettings().barSetting().lossTitle();
    }

    @Override
    public Optional<SoundEvent> getRaidStartSound() {
        return getRaidSettings().soundSetting().raidStartSound().map(Holder::get);
    }

    @Override
    public Optional<SoundEvent> getWaveStartSound() {
        return getRaidSettings().soundSetting().waveStartSound().map(Holder::get);
    }

    @Override
    public Optional<SoundEvent> getVictorySound() {
        return getRaidSettings().soundSetting().victorySound().map(Holder::get);
    }

    @Override
    public Optional<SoundEvent> getLossSound() {
        return getRaidSettings().soundSetting().lossSound().map(Holder::get);
    }

    protected record RaidSetting(IPlaceComponent placeComponent, BorderSetting borderSetting, BarSetting barSetting, SoundSetting soundSetting, List<IResultComponent> resultComponents, int victoryDuration, int lossDuration, boolean showRoundTitle) {
        public static final Codec<RaidSetting> CODEC = RecordCodecBuilder.<RaidSetting>mapCodec(instance -> instance.group(
                HTPlaceComponents.getCodec().optionalFieldOf("placement_type", HTPlaceComponents.DEFAULT.getValue()).forGetter(RaidSetting::placeComponent),

                BorderSetting.CODEC.fieldOf("border_setting").forGetter(RaidSetting::borderSetting),
                BarSetting.CODEC.fieldOf("bar_setting").forGetter(RaidSetting::barSetting),
                SoundSetting.CODEC.fieldOf("sound_setting").forGetter(RaidSetting::soundSetting),

                HTResultComponents.getCodec().listOf().optionalFieldOf("results", List.of()).forGetter(RaidSetting::resultComponents),

                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("victory_duration", 100).forGetter(RaidSetting::victoryDuration),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("loss_duration", 100).forGetter(RaidSetting::lossDuration),
                Codec.BOOL.optionalFieldOf("show_wave_title", true).forGetter(RaidSetting::showRoundTitle)


        ).apply(instance, RaidSetting::new)).codec();
    }

    protected record BorderSetting(double raidRange, boolean blockInside, boolean blockOutside, boolean renderBorder, int borderColor) {
        public static final Codec<BorderSetting> CODEC = RecordCodecBuilder.<BorderSetting>mapCodec(instance -> instance.group(
                Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("raid_range", 40D).forGetter(BorderSetting::raidRange),
                Codec.BOOL.optionalFieldOf("block_inside", false).forGetter(BorderSetting::blockInside),
                Codec.BOOL.optionalFieldOf("block_outside", false).forGetter(BorderSetting::blockOutside),
                Codec.BOOL.optionalFieldOf("render_border", false).forGetter(BorderSetting::renderBorder),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("border_color", ColorHelper.BORDER_AQUA).forGetter(BorderSetting::borderColor)
        ).apply(instance, BorderSetting::new)).codec();
    }

    protected record SoundSetting(Optional<Holder<SoundEvent>> raidStartSound, Optional<Holder<SoundEvent>> waveStartSound, Optional<Holder<SoundEvent>> victorySound, Optional<Holder<SoundEvent>> lossSound) {
        public static final Codec<SoundSetting> CODEC = RecordCodecBuilder.<SoundSetting>mapCodec(instance -> instance.group(
                Codec.optionalField("raid_start_sound", SoundEvent.CODEC).forGetter(SoundSetting::raidStartSound),
                Codec.optionalField("wave_start_sound", SoundEvent.CODEC).forGetter(SoundSetting::waveStartSound),
                Codec.optionalField("victory_sound", SoundEvent.CODEC).forGetter(SoundSetting::victorySound),
                Codec.optionalField("loss_sound", SoundEvent.CODEC).forGetter(SoundSetting::lossSound)
        ).apply(instance, SoundSetting::new)).codec();
    }

    protected record BarSetting(MutableComponent raidTitle, BossEvent.BossBarColor raidColor, MutableComponent victoryTitle, MutableComponent lossTitle) {
        public static final Codec<BarSetting> CODEC = RecordCodecBuilder.<BarSetting>mapCodec(instance -> instance.group(
                StringHelper.CODEC.optionalFieldOf("raid_title", AbstractRaid.RAID_TITLE).forGetter(BarSetting::raidTitle),
                BOSS_BAR_COLOR_CODEC.optionalFieldOf("raid_bar_color", BossEvent.BossBarColor.RED).forGetter(BarSetting::raidColor),
                StringHelper.CODEC.optionalFieldOf("victory_title", AbstractRaid.RAID_VICTORY_TITLE).forGetter(BarSetting::victoryTitle),
                StringHelper.CODEC.optionalFieldOf("loss_title", AbstractRaid.RAID_LOSS_TITLE).forGetter(BarSetting::lossTitle)
        ).apply(instance, BarSetting::new)).codec();
    }
}
