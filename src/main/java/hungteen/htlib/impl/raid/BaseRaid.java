package hungteen.htlib.impl.raid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.common.world.raid.PlaceComponent;
import hungteen.htlib.common.world.raid.RaidComponent;
import hungteen.htlib.impl.placement.HTPlaceComponents;
import hungteen.htlib.util.helper.StringHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.BossEvent;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-28 23:37
 **/
public abstract class BaseRaid extends RaidComponent {

    public static final Codec<BossEvent.BossBarColor> BOSS_BAR_COLOR_CODEC = ExtraCodecs.stringResolverCodec(BossEvent.BossBarColor::getName, BossEvent.BossBarColor::byName);
    private final RaidSettings raidSettings;

    public BaseRaid(RaidSettings raidSettings){
        this.raidSettings = raidSettings;
    }

    public RaidSettings getRaidSettings(){
        return this.raidSettings;
    }

    @Override
    public PlaceComponent getSpawnPlacement() {
        return raidSettings.placeComponent();
    }

    @Override
    public int getVictoryDuration() {
        return raidSettings.victoryDuration();
    }

    @Override
    public int getLossDuration() {
        return raidSettings.lossDuration();
    }

    @Override
    public double getRaidRange() {
        return raidSettings.raidRange();
    }

    @Override
    public boolean showRoundTitle() {
        return raidSettings.showRoundTitle();
    }

    @Override
    public MutableComponent getRaidTitle() {
        return raidSettings.raidTitle();
    }

    @Override
    public BossEvent.BossBarColor getRaidColor() {
        return raidSettings.raidColor();
    }

    @Override
    public MutableComponent getVictoryTitle() {
        return raidSettings.victoryTitle();
    }

    @Override
    public MutableComponent getLossTitle() {
        return raidSettings.lossTitle();
    }

    @Override
    public SoundEvent getRaidStartSound() {
        return raidSettings.raidStartSound();
    }

    @Override
    public SoundEvent getWaveStartSound() {
        return raidSettings.waveStartSound();
    }

    public static RaidSettingBuilder builder(){
        return new RaidSettingBuilder();
    }

    protected record RaidSettings(PlaceComponent placeComponent, int victoryDuration, int lossDuration, double raidRange, boolean showRoundTitle, MutableComponent raidTitle, BossEvent.BossBarColor raidColor, MutableComponent victoryTitle, MutableComponent lossTitle, SoundEvent raidStartSound, SoundEvent waveStartSound) {
        public static final Codec<RaidSettings> CODEC = RecordCodecBuilder.<RaidSettings>mapCodec(instance -> instance.group(
                HTPlaceComponents.getCodec().optionalFieldOf("placement_type", HTPlaceComponents.DEFAULT.getValue()).forGetter(RaidSettings::placeComponent),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("victory_duration", 100).forGetter(RaidSettings::victoryDuration),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("loss_duration", 100).forGetter(RaidSettings::lossDuration),
                Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("raid_range", 40D).forGetter(RaidSettings::raidRange),
                Codec.BOOL.optionalFieldOf("show_wave_title", true).forGetter(RaidSettings::showRoundTitle),
                StringHelper.CODEC.optionalFieldOf("raid_title", Component.empty()).forGetter(RaidSettings::raidTitle),
                BOSS_BAR_COLOR_CODEC.optionalFieldOf("raid_bar_color", BossEvent.BossBarColor.RED).forGetter(RaidSettings::raidColor),
                StringHelper.CODEC.optionalFieldOf("victory_title", Component.empty()).forGetter(RaidSettings::victoryTitle),
                StringHelper.CODEC.optionalFieldOf("loss_title", Component.empty()).forGetter(RaidSettings::lossTitle),
                SoundEvent.CODEC.optionalFieldOf("raid_start_sound", SoundEvents.RAID_HORN).forGetter(RaidSettings::raidStartSound),
                SoundEvent.CODEC.optionalFieldOf("wave_start_sound", SoundEvents.RAID_HORN).forGetter(RaidSettings::waveStartSound)
        ).apply(instance, RaidSettings::new)).codec();
    }

    public static class RaidSettingBuilder {
        private PlaceComponent placeComponent = HTPlaceComponents.DEFAULT.getValue();
        private int victoryDuration = 100;
        private int lossDuration = 100;
        private double raidRange = 40;
        private boolean showRoundTitle = true;
        private MutableComponent raidTitle = Component.empty();
        private BossEvent.BossBarColor raidColor = BossEvent.BossBarColor.RED;
        private MutableComponent victoryTitle = Component.empty();
        private MutableComponent lossTitle = Component.empty();
        private SoundEvent raidStartSound = null;
        private SoundEvent waveStartSound = null;

        public RaidSettingBuilder place(PlaceComponent placeComponent){
            this.placeComponent = placeComponent;
            return this;
        }

        public RaidSettingBuilder victoryDuration(int victoryDuration){
            this.victoryDuration = victoryDuration;
            return this;
        }

        public RaidSettingBuilder lossDuration(int lossDuration){
            this.lossDuration = lossDuration;
            return this;
        }

        public RaidSettingBuilder range(int raidRange){
            this.raidRange = raidRange;
            return this;
        }

        public RaidSettingBuilder showRoundTitle(boolean showRoundTitle){
            this.showRoundTitle = showRoundTitle;
            return this;
        }

        public RaidSettingBuilder color(BossEvent.BossBarColor color){
            this.raidColor = raidColor;
            return this;
        }

        public RaidSettingBuilder title(MutableComponent title){
            this.raidTitle = title;
            return this;
        }

        public RaidSettingBuilder victoryTitle(MutableComponent title){
            this.victoryTitle = title;
            return this;
        }

        public RaidSettingBuilder lossTitle(MutableComponent title){
            this.lossTitle = title;
            return this;
        }

        public RaidSettingBuilder raidSound(SoundEvent soundEvent){
            this.raidStartSound = soundEvent;
            return this;
        }

        public RaidSettingBuilder waveSound(SoundEvent soundEvent){
            this.waveStartSound = soundEvent;
            return this;
        }

        public RaidSettings build(){
            return new RaidSettings(placeComponent, victoryDuration, lossDuration, raidRange, showRoundTitle, raidTitle, raidColor, victoryTitle, lossTitle, raidStartSound, waveStartSound);
        }

    }
}
