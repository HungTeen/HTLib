package hungteen.htlib.common.impl.raid;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.HTCodecRegistry;
import hungteen.htlib.api.interfaces.raid.*;
import hungteen.htlib.common.HTSounds;
import hungteen.htlib.common.impl.result.HTResultComponents;
import hungteen.htlib.common.impl.wave.HTWaveComponents;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.world.raid.AbstractRaid;
import hungteen.htlib.util.helper.ColorHelper;
import hungteen.htlib.util.helper.HTLibHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.BossEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/18 10:37
 */
public interface HTRaidComponents {

    HTCodecRegistry<IRaidComponent> RAIDS = HTRegistryManager.create(HTLibHelper.prefix("raid"), HTRaidComponents::getDirectCodec, true);

    ResourceKey<IRaidComponent> TEST = create("test");
    ResourceKey<IRaidComponent> COMMON = create("common");

    static void register(BootstapContext<IRaidComponent> context) {
        final HolderGetter<IResultComponent> results = HTResultComponents.registry().helper().lookup(context);
        final HolderGetter<IWaveComponent> waves = HTWaveComponents.registry().helper().lookup(context);
        context.register(TEST, new CommonRaid(
                builder()
                        .blockInside(false)
                        .blockOutside(false)
                        .renderBorder(false)
                        .victoryResult(results.getOrThrow(HTResultComponents.TEST))
                        .color(BossEvent.BossBarColor.BLUE)
                        .raidSound(HTSounds.PREPARE.getHolder())
                        .waveSound(HTSounds.HUGE_WAVE.getHolder())
                        .victorySound(HTSounds.VICTORY.getHolder())
                        .lossSound(HTSounds.LOSS.getHolder())
                        .build(),
                Arrays.asList(
                        waves.getOrThrow(HTWaveComponents.TEST_1),
                        waves.getOrThrow(HTWaveComponents.TEST_2)
                )
        ));
        context.register(COMMON, new CommonRaid(
                builder()
                        .blockInside(true)
                        .blockOutside(true)
                        .renderBorder(true)
                        .victoryResult(results.getOrThrow(HTResultComponents.COMMON_FUNCTION))
                        .victoryResult(results.getOrThrow(HTResultComponents.COMMAND_FUNCTION))
                        .color(BossEvent.BossBarColor.RED)
                        .raidSound(HTSounds.PREPARE.getHolder())
                        .waveSound(HTSounds.HUGE_WAVE.getHolder())
                        .victorySound(HTSounds.VICTORY.getHolder())
                        .lossSound(HTSounds.LOSS.getHolder())
                        .build(),
                Arrays.asList(
                        waves.getOrThrow(HTWaveComponents.COMMON_WAVE_1),
                        waves.getOrThrow(HTWaveComponents.COMMON_WAVE_2),
                        waves.getOrThrow(HTWaveComponents.COMMON_WAVE_3)
                )
        ));
    }

    static Codec<IRaidComponent> getDirectCodec() {
        return HTRaidTypes.registry().byNameCodec().dispatch(IRaidComponent::getType, RaidType::codec);
    }

    static Codec<Holder<IRaidComponent>> getCodec() {
        return registry().getHolderCodec(getDirectCodec());
    }

    static ResourceKey<IRaidComponent> create(String name) {
        return registry().createKey(HTLibHelper.prefix(name));
    }

    static HTCodecRegistry<IRaidComponent> registry() {
        return RAIDS;
    }

    static RaidSettingBuilder builder() {
        return new RaidSettingBuilder();
    }

    class RaidSettingBuilder {
        private Holder<IPositionComponent> positionComponent;
        private final List<Holder<IResultComponent>> victoryResults = new ArrayList<>();
        private final List<Holder<IResultComponent>> lossResults = new ArrayList<>();
        private double raidRange = 40;
        private boolean blockInside = false;
        private boolean blockOutside = false;
        private boolean renderBorder = false;
        private int borderColor = ColorHelper.BORDER_AQUA.rgb();
        private int victoryDuration = 100;
        private int lossDuration = 100;
        private boolean showRoundTitle = true;
        private boolean sendRaidWarn = true;
        private MutableComponent raidTitle = AbstractRaid.RAID_TITLE;
        private BossEvent.BossBarColor raidColor = BossEvent.BossBarColor.RED;
        private MutableComponent victoryTitle = AbstractRaid.RAID_VICTORY_TITLE;
        private MutableComponent lossTitle = AbstractRaid.RAID_LOSS_TITLE;
        private Optional<Holder<SoundEvent>> raidStartSound = Optional.empty();
        private Optional<Holder<SoundEvent>> waveStartSound = Optional.empty();
        private Optional<Holder<SoundEvent>> victorySound = Optional.empty();
        private Optional<Holder<SoundEvent>> lossSound = Optional.empty();

        public RaidSettingBuilder place(Holder<IPositionComponent> positionComponent) {
            this.positionComponent = positionComponent;
            return this;
        }

        public RaidSettingBuilder victoryResult(Holder<IResultComponent> resultComponent) {
            this.victoryResults.add(resultComponent);
            return this;
        }

        public RaidSettingBuilder lossResult(Holder<IResultComponent> resultComponent) {
            this.lossResults.add(resultComponent);
            return this;
        }

        public RaidSettingBuilder range(int raidRange) {
            this.raidRange = raidRange;
            return this;
        }

        public RaidSettingBuilder blockInside(boolean block) {
            this.blockInside = block;
            return this;
        }

        public RaidSettingBuilder blockOutside(boolean block) {
            this.blockOutside = block;
            return this;
        }

        public RaidSettingBuilder renderBorder(boolean render) {
            this.renderBorder = render;
            return this;
        }

        public RaidSettingBuilder borderColor(int color) {
            this.borderColor = color;
            return this;
        }

        public RaidSettingBuilder victoryDuration(int victoryDuration) {
            this.victoryDuration = victoryDuration;
            return this;
        }

        public RaidSettingBuilder lossDuration(int lossDuration) {
            this.lossDuration = lossDuration;
            return this;
        }

        public RaidSettingBuilder showRoundTitle(boolean showRoundTitle) {
            this.showRoundTitle = showRoundTitle;
            return this;
        }

        public RaidSettingBuilder sendRaidWarn(boolean sendRaidWarn) {
            this.sendRaidWarn = sendRaidWarn;
            return this;
        }

        public RaidSettingBuilder color(BossEvent.BossBarColor color) {
            this.raidColor = color;
            return this;
        }

        public RaidSettingBuilder title(MutableComponent title) {
            this.raidTitle = title;
            return this;
        }

        public RaidSettingBuilder victoryTitle(MutableComponent title) {
            this.victoryTitle = title;
            return this;
        }

        public RaidSettingBuilder lossTitle(MutableComponent title) {
            this.lossTitle = title;
            return this;
        }

        public RaidSettingBuilder raidSound(Optional<Holder<SoundEvent>> soundEvent) {
            this.raidStartSound = soundEvent;
            return this;
        }

        public RaidSettingBuilder waveSound(Optional<Holder<SoundEvent>> soundEvent) {
            this.waveStartSound = soundEvent;
            return this;
        }

        public RaidSettingBuilder victorySound(Optional<Holder<SoundEvent>> soundEvent) {
            this.victorySound = soundEvent;
            return this;
        }

        public RaidSettingBuilder lossSound(Optional<Holder<SoundEvent>> soundEvent) {
            this.lossSound = soundEvent;
            return this;
        }

        public RaidComponent.RaidSetting build() {
            return new RaidComponent.RaidSetting(
                    Optional.ofNullable(this.positionComponent),
                    new RaidComponent.BorderSetting(
                            this.raidRange,
                            this.blockInside,
                            this.blockOutside,
                            this.renderBorder,
                            this.borderColor
                    ),
                    new RaidComponent.BarSetting(
                            this.raidTitle,
                            this.raidColor,
                            this.victoryTitle,
                            this.lossTitle
                    ),
                    new RaidComponent.SoundSetting(
                            this.raidStartSound,
                            this.waveStartSound,
                            this.victorySound,
                            this.lossSound
                    ),
                    this.victoryResults,
                    this.lossResults,
                    this.victoryDuration,
                    this.lossDuration,
                    this.showRoundTitle,
                    this.sendRaidWarn
            );
        }

    }

}
