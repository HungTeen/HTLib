package hungteen.htlib.common.impl.raid;

import com.mojang.serialization.Codec;
import hungteen.htlib.HTLib;
import hungteen.htlib.common.HTSounds;
import hungteen.htlib.common.impl.placement.HTPlaceComponents;
import hungteen.htlib.common.impl.result.HTResultComponents;
import hungteen.htlib.common.impl.spawn.DurationSpawn;
import hungteen.htlib.common.impl.spawn.HTSpawnComponents;
import hungteen.htlib.common.impl.spawn.OnceSpawn;
import hungteen.htlib.common.impl.wave.CommonWave;
import hungteen.htlib.common.impl.wave.HTWaveComponents;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryHolder;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.htlib.common.world.raid.AbstractRaid;
import hungteen.htlib.api.interfaces.raid.IResultComponent;
import hungteen.htlib.api.interfaces.raid.IPlaceComponent;
import hungteen.htlib.api.interfaces.raid.IRaidComponent;
import hungteen.htlib.util.helper.ColorHelper;
import hungteen.htlib.api.interfaces.raid.IRaidComponentType;
import hungteen.htlib.util.helper.StringHelper;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;

import java.util.*;
import java.util.stream.Stream;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/18 10:37
 */
public class HTRaidComponents {

    public static final HTSimpleRegistry<IRaidComponentType<?>> RAID_TYPES = HTRegistryManager.create(StringHelper.prefix("raid_type"));
    public static final HTCodecRegistry<IRaidComponent> RAIDS = HTRegistryManager.create(IRaidComponent.class, "custom_raid/raids", HTRaidComponents::getCodec, true);

    /* Raid types */

    public static final IRaidComponentType<CommonRaid> COMMON_RAID_TYPE = new DefaultRaidType<>("common", CommonRaid.CODEC);

    /* Raids */

    public static final HTRegistryHolder<IRaidComponent> TEST = RAIDS.innerRegister(StringHelper.prefix("test"),
            new CommonRaid(
                    HTRaidComponents.builder().blockInside(true).blockOutside(true).renderBorder(true).result(HTResultComponents.TEST.getValue()).color(BossEvent.BossBarColor.BLUE).build(),
                    Arrays.asList(
                            new CommonWave(
                                    HTWaveComponents.builder().prepare(100).wave(1200).skip(false).build(),
                                    Arrays.asList(
                                            new OnceSpawn(
                                                    HTSpawnComponents.builder().entityType(EntityType.CREEPER).build(),
                                                    10,
                                                    10
                                            )
                                    )
                            ),
                            new CommonWave(
                                    HTWaveComponents.builder().prepare(100).wave(1200).skip(false).build(),
                                    Arrays.asList(
                                            new OnceSpawn(
                                                    HTSpawnComponents.builder().entityType(EntityType.SPIDER).build(),
                                                    10,
                                                    5
                                            ),
                                            new DurationSpawn(
                                                    HTSpawnComponents.builder().entityType(EntityType.SKELETON).build(),
                                                    100,
                                                    400,
                                                    100,
                                                    1,
                                                    0
                                            )
                                    )
                            )
                    )
            )
    );

    /**
     * {@link HTLib#HTLib()}
     */
    public static void registerStuffs(){
        List.of(COMMON_RAID_TYPE).forEach(HTRaidComponents::registerRaidType);
    }

    public static IRaidComponentType<?> getRaidType(ResourceLocation location){
        return RAID_TYPES.getValue(location).orElse(null);
    }

    public static IRaidComponent getRaidComponent(ResourceLocation location){
        return RAIDS.getValue(location).orElse(null);
    }

    public static Stream<ResourceLocation> getIds(){
        return RAIDS.getEntries().stream().map(Map.Entry::getKey);
    }

    public static void registerRaidType(IRaidComponentType<?> type){
        RAID_TYPES.register(type);
    }

    public static Codec<IRaidComponent> getCodec(){
        return RAID_TYPES.byNameCodec().dispatch(IRaidComponent::getType, IRaidComponentType::codec);
    }

    public static RaidSettingBuilder builder(){
        return new RaidSettingBuilder();
    }

    protected record DefaultRaidType<P extends IRaidComponent>(String name, Codec<P> codec) implements IRaidComponentType<P> {

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getModID() {
            return HTLib.MOD_ID;
        }
    }

    public static class RaidSettingBuilder {
        private IPlaceComponent placeComponent = HTPlaceComponents.DEFAULT.getValue();
        private List<IResultComponent> resultComponents = new ArrayList<>();
        private double raidRange = 40;
        private boolean blockInside = false;
        private boolean blockOutside = false;
        private boolean renderBorder = false;
        private int borderColor = ColorHelper.BORDER_AQUA;
        private int victoryDuration = 100;
        private int lossDuration = 100;
        private boolean showRoundTitle = true;
        private MutableComponent raidTitle = AbstractRaid.RAID_TITLE;
        private BossEvent.BossBarColor raidColor = BossEvent.BossBarColor.RED;
        private MutableComponent victoryTitle = AbstractRaid.RAID_VICTORY_TITLE;
        private MutableComponent lossTitle = AbstractRaid.RAID_LOSS_TITLE;
        private SoundEvent raidStartSound = HTSounds.PREPARE.get();
        private SoundEvent waveStartSound = HTSounds.HUGE_WAVE.get();
        private SoundEvent victorySound = HTSounds.VICTORY.get();
        private SoundEvent lossSound = HTSounds.LOSS.get();

        public RaidSettingBuilder place(IPlaceComponent placeComponent){
            this.placeComponent = placeComponent;
            return this;
        }

        public RaidSettingBuilder result(IResultComponent resultComponent){
            this.resultComponents.add(resultComponent);
            return this;
        }

        public RaidSettingBuilder range(int raidRange){
            this.raidRange = raidRange;
            return this;
        }

        public RaidSettingBuilder blockInside(boolean block){
            this.blockInside = block;
            return this;
        }

        public RaidSettingBuilder blockOutside(boolean block) {
            this.blockOutside = block;
            return this;
        }

        public RaidSettingBuilder renderBorder(boolean render){
            this.renderBorder = render;
            return this;
        }

        public RaidSettingBuilder borderColor(int color){
            this.borderColor = color;
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

        public RaidSettingBuilder showRoundTitle(boolean showRoundTitle){
            this.showRoundTitle = showRoundTitle;
            return this;
        }

        public RaidSettingBuilder color(BossEvent.BossBarColor color){
            this.raidColor = color;
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

        public RaidSettingBuilder victorySound(SoundEvent soundEvent){
            this.victorySound = soundEvent;
            return this;
        }

        public RaidSettingBuilder lossSound(SoundEvent soundEvent){
            this.lossSound = soundEvent;
            return this;
        }

        public RaidComponent.RaidSetting build(){
            return new RaidComponent.RaidSetting(
                    this.placeComponent,
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
                            Optional.of(Holder.direct(this.raidStartSound)),
                            Optional.of(Holder.direct(this.waveStartSound)),
                            Optional.of(Holder.direct(this.victorySound)),
                            Optional.of(Holder.direct(this.lossSound))
                    ),
                    this.resultComponents,
                    this.victoryDuration,
                    this.lossDuration,
                    this.showRoundTitle
            );
        }

    }

}
