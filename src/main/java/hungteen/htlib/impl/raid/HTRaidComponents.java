package hungteen.htlib.impl.raid;

import com.mojang.serialization.Codec;
import hungteen.htlib.HTLib;
import hungteen.htlib.common.HTSounds;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryHolder;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.htlib.common.world.raid.AbstractRaid;
import hungteen.htlib.common.world.raid.PlaceComponent;
import hungteen.htlib.common.world.raid.RaidComponent;
import hungteen.htlib.impl.placement.HTPlaceComponents;
import hungteen.htlib.impl.spawn.DurationSpawn;
import hungteen.htlib.impl.spawn.HTSpawnComponents;
import hungteen.htlib.impl.spawn.OnceSpawn;
import hungteen.htlib.impl.wave.CommonWave;
import hungteen.htlib.impl.wave.HTWaveComponents;
import hungteen.htlib.util.helper.ColorHelper;
import hungteen.htlib.util.interfaces.IRaidComponentType;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/18 10:37
 */
public class HTRaidComponents {

    public static final HTSimpleRegistry<IRaidComponentType<?>> RAID_TYPES = HTRegistryManager.create(HTLib.prefix("raid_type"));
    public static final HTCodecRegistry<RaidComponent> RAIDS = HTRegistryManager.create(RaidComponent.class, "custom_raid/raids", HTRaidComponents::getCodec);

    /* Raid types */

    public static final IRaidComponentType<CommonRaid> COMMON_RAID_TYPE = new DefaultRaidType<>("common_raid", CommonRaid.CODEC);

    /* Raids */

    public static final HTRegistryHolder<RaidComponent> TEST = RAIDS.innerRegister(HTLib.prefix("test"),
            new CommonRaid(
                    HTRaidComponents.builder().build(),
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

    public static RaidComponent getRaidComponent(ResourceLocation location){
        return RAIDS.getValue(location).orElse(null);
    }

    public static Stream<ResourceLocation> getIds(){
        return RAIDS.getAllWithLocation().stream().map(Map.Entry::getKey);
    }

    public static void registerRaidType(IRaidComponentType<?> type){
        RAID_TYPES.register(type);
    }

    public static Codec<RaidComponent> getCodec(){
        return RAID_TYPES.byNameCodec().dispatch(RaidComponent::getType, IRaidComponentType::codec);
    }

    public static RaidSettingBuilder builder(){
        return new RaidSettingBuilder();
    }

    protected record DefaultRaidType<P extends RaidComponent>(String name, Codec<P> codec) implements IRaidComponentType<P> {

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
        private PlaceComponent placeComponent = HTPlaceComponents.DEFAULT.getValue();
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

        public RaidSettingBuilder place(PlaceComponent placeComponent){
            this.placeComponent = placeComponent;
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
            this.renderBorder = renderBorder;
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

        public RaidSettingBuilder victorySound(SoundEvent soundEvent){
            this.victorySound = soundEvent;
            return this;
        }

        public RaidSettingBuilder lossSound(SoundEvent soundEvent){
            this.lossSound = soundEvent;
            return this;
        }

        public BaseRaid.RaidSettings build(){
            return new BaseRaid.RaidSettings(
                    placeComponent,
                    new BaseRaid.BorderSettings(
                            this.raidRange,
                            this.blockInside,
                            this.blockOutside,
                            this.renderBorder,
                            this.borderColor
                    ),
                    victoryDuration,
                    lossDuration,
                    showRoundTitle,
                    raidTitle,
                    raidColor,
                    victoryTitle,
                    lossTitle,
                    Optional.ofNullable(raidStartSound),
                    Optional.ofNullable(waveStartSound),
                    Optional.ofNullable(victorySound),
                    Optional.ofNullable(lossSound));
        }

    }

}
