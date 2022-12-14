package hungteen.htlib.impl.raid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.common.world.raid.AbstractRaid;
import hungteen.htlib.api.interfaces.raid.IResultComponent;
import hungteen.htlib.api.interfaces.raid.IPlaceComponent;
import hungteen.htlib.api.interfaces.raid.IRaidComponent;
import hungteen.htlib.impl.placement.HTPlaceComponents;
import hungteen.htlib.impl.result.HTResultComponents;
import hungteen.htlib.util.helper.ColorHelper;
import hungteen.htlib.util.helper.StringHelper;
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
    private final RaidSettings raidSettings;

    public RaidComponent(RaidSettings raidSettings){
        this.raidSettings = raidSettings;
    }

    public RaidSettings getRaidSettings(){
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
        return getRaidSettings().borderSettings().raidRange();
    }

    @Override
    public boolean blockInside() {
        return getRaidSettings().borderSettings().blockInside();
    }

    @Override
    public boolean blockOutside() {
        return getRaidSettings().borderSettings().blockOutside();
    }

    @Override
    public boolean renderBorder() {
        return getRaidSettings().borderSettings().renderBorder();
    }

    @Override
    public int getBorderColor() {
        return getRaidSettings().borderSettings().borderColor();
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
        return getRaidSettings().barSettings().raidTitle();
    }

    @Override
    public BossEvent.BossBarColor getBarColor() {
        return getRaidSettings().barSettings().raidColor();
    }

    @Override
    public MutableComponent getVictoryTitle() {
        return getRaidSettings().barSettings().victoryTitle();
    }

    @Override
    public MutableComponent getLossTitle() {
        return getRaidSettings().barSettings().lossTitle();
    }

    @Override
    public Optional<SoundEvent> getRaidStartSound() {
        return getRaidSettings().soundSettings().raidStartSound();
    }

    @Override
    public Optional<SoundEvent> getWaveStartSound() {
        return getRaidSettings().soundSettings().waveStartSound();
    }

    @Override
    public Optional<SoundEvent> getVictorySound() {
        return getRaidSettings().soundSettings().victorySound();
    }

    @Override
    public Optional<SoundEvent> getLossSound() {
        return getRaidSettings().soundSettings().lossSound();
    }

    protected record RaidSettings(IPlaceComponent placeComponent, BorderSettings borderSettings, BarSettings barSettings, SoundSettings soundSettings, List<IResultComponent> resultComponents, int victoryDuration, int lossDuration, boolean showRoundTitle) {
        public static final Codec<RaidSettings> CODEC = RecordCodecBuilder.<RaidSettings>mapCodec(instance -> instance.group(
                HTPlaceComponents.getCodec().optionalFieldOf("placement_type", HTPlaceComponents.DEFAULT.getValue()).forGetter(RaidSettings::placeComponent),

                BorderSettings.CODEC.fieldOf("border_settings").forGetter(RaidSettings::borderSettings),
                BarSettings.CODEC.fieldOf("bar_settings").forGetter(RaidSettings::barSettings),
                SoundSettings.CODEC.fieldOf("sound_settings").forGetter(RaidSettings::soundSettings),

                HTResultComponents.getCodec().listOf().optionalFieldOf("results", List.of()).forGetter(RaidSettings::resultComponents),

                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("victory_duration", 100).forGetter(RaidSettings::victoryDuration),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("loss_duration", 100).forGetter(RaidSettings::lossDuration),
                Codec.BOOL.optionalFieldOf("show_wave_title", true).forGetter(RaidSettings::showRoundTitle)


        ).apply(instance, RaidSettings::new)).codec();
    }

    protected record BorderSettings(double raidRange, boolean blockInside, boolean blockOutside, boolean renderBorder, int borderColor) {
        public static final Codec<BorderSettings> CODEC = RecordCodecBuilder.<BorderSettings>mapCodec(instance -> instance.group(
                Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("raid_range", 40D).forGetter(BorderSettings::raidRange),
                Codec.BOOL.optionalFieldOf("block_inside", false).forGetter(BorderSettings::blockInside),
                Codec.BOOL.optionalFieldOf("block_outside", false).forGetter(BorderSettings::blockOutside),
                Codec.BOOL.optionalFieldOf("render_border", false).forGetter(BorderSettings::renderBorder),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("border_color", ColorHelper.BORDER_AQUA).forGetter(BorderSettings::borderColor)
        ).apply(instance, BorderSettings::new)).codec();
    }

    protected record SoundSettings(Optional<SoundEvent> raidStartSound, Optional<SoundEvent> waveStartSound, Optional<SoundEvent> victorySound, Optional<SoundEvent> lossSound) {
        public static final Codec<SoundSettings> CODEC = RecordCodecBuilder.<SoundSettings>mapCodec(instance -> instance.group(
                Codec.optionalField("raid_start_sound", SoundEvent.CODEC).forGetter(SoundSettings::raidStartSound),
                Codec.optionalField("wave_start_sound", SoundEvent.CODEC).forGetter(SoundSettings::waveStartSound),
                Codec.optionalField("victory_sound", SoundEvent.CODEC).forGetter(SoundSettings::victorySound),
                Codec.optionalField("loss_sound", SoundEvent.CODEC).forGetter(SoundSettings::lossSound)
        ).apply(instance, SoundSettings::new)).codec();
    }

    protected record BarSettings(MutableComponent raidTitle, BossEvent.BossBarColor raidColor, MutableComponent victoryTitle, MutableComponent lossTitle) {
        public static final Codec<BarSettings> CODEC = RecordCodecBuilder.<BarSettings>mapCodec(instance -> instance.group(
                StringHelper.CODEC.optionalFieldOf("raid_title", AbstractRaid.RAID_TITLE).forGetter(BarSettings::raidTitle),
                BOSS_BAR_COLOR_CODEC.optionalFieldOf("raid_bar_color", BossEvent.BossBarColor.RED).forGetter(BarSettings::raidColor),
                StringHelper.CODEC.optionalFieldOf("victory_title", AbstractRaid.RAID_VICTORY_TITLE).forGetter(BarSettings::victoryTitle),
                StringHelper.CODEC.optionalFieldOf("loss_title", AbstractRaid.RAID_LOSS_TITLE).forGetter(BarSettings::lossTitle)
        ).apply(instance, BarSettings::new)).codec();
    }
}
