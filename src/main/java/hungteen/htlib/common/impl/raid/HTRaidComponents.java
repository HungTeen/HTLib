package hungteen.htlib.common.impl.raid;

import hungteen.htlib.api.interfaces.raid.IRaidComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.stream.Stream;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/18 10:37
 */
public class HTRaidComponents {

//    private static final HTCodecRegistry<IRaidComponent> RAIDS = HTRegistryManager.create(HTLibHelper.prefix("custom_raid/raids"), HTRaidComponents::getCodec, HTRaidComponents::getCodec);


//    public static final HTRegistryHolder<IRaidComponent> TEST = RAIDS.innerRegister(HTLibHelper.prefix("test"),
//            new CommonRaid(
//                    HTRaidComponents.builder().blockInside(true).blockOutside(true).renderBorder(true).result(HTResultComponents.TEST.getValue()).color(BossEvent.BossBarColor.BLUE).build(),
//                    Arrays.asList(
//                            new CommonWave(
//                                    HTWaveComponents.builder().prepare(100).wave(1200).skip(false).build(),
//                                    Arrays.asList(
//                                            new OnceSpawn(
//                                                    HTSpawnComponents.builder().entityType(EntityType.CREEPER).build(),
//                                                    10,
//                                                    10
//                                            )
//                                    )
//                            ),
//                            new CommonWave(
//                                    HTWaveComponents.builder().prepare(100).wave(1200).skip(false).build(),
//                                    Arrays.asList(
//                                            new OnceSpawn(
//                                                    HTSpawnComponents.builder().entityType(EntityType.SPIDER).build(),
//                                                    10,
//                                                    5
//                                            ),
//                                            new DurationSpawn(
//                                                    HTSpawnComponents.builder().entityType(EntityType.SKELETON).build(),
//                                                    100,
//                                                    400,
//                                                    100,
//                                                    1,
//                                                    0
//                                            )
//                                    )
//                            )
//                    )
//            )
//    );

    public static IRaidComponent getRaidComponent(ResourceLocation location){
        return null;
//        return RAIDS.getValue(location).orElse(null);
    }

    public static Stream<ResourceLocation> getIds(){
        return Stream.of();
//        return RAIDS.getEntries().stream().map(Map.Entry::getKey);
    }
//
//    public static void register(BootstapContext<IRaidComponent> context) {
////        OverworldTradingMarket.initStructures(context);
////        SpiritualPlainsVillage.initStructures(context);
////        final HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
////        context.register()
//        System.out.println("Registering Raids");
//    }
//
//    public static IHTRegistry<IRaidComponent> registry(){
//        return RAIDS;
//    }
//
//    public static Codec<IRaidComponent> getCodec(){
//        return HTRaidTypes.registry().byNameCodec().dispatch(IRaidComponent::getType, IRaidComponentType::codec);
//    }
//
//    public static RaidSettingBuilder builder(){
//        return new RaidSettingBuilder();
//    }
//
//    public static class RaidSettingBuilder {
//        private IPlaceComponent placeComponent = HTPlaceComponents.DEFAULT.getValue();
//        private List<IResultComponent> resultComponents = new ArrayList<>();
//        private double raidRange = 40;
//        private boolean blockInside = false;
//        private boolean blockOutside = false;
//        private boolean renderBorder = false;
//        private int borderColor = ColorHelper.BORDER_AQUA;
//        private int victoryDuration = 100;
//        private int lossDuration = 100;
//        private boolean showRoundTitle = true;
//        private MutableComponent raidTitle = AbstractRaid.RAID_TITLE;
//        private BossEvent.BossBarColor raidColor = BossEvent.BossBarColor.RED;
//        private MutableComponent victoryTitle = AbstractRaid.RAID_VICTORY_TITLE;
//        private MutableComponent lossTitle = AbstractRaid.RAID_LOSS_TITLE;
//        private SoundEvent raidStartSound = HTSounds.PREPARE.get();
//        private SoundEvent waveStartSound = HTSounds.HUGE_WAVE.get();
//        private SoundEvent victorySound = HTSounds.VICTORY.get();
//        private SoundEvent lossSound = HTSounds.LOSS.get();
//
//        public RaidSettingBuilder place(IPlaceComponent placeComponent){
//            this.placeComponent = placeComponent;
//            return this;
//        }
//
//        public RaidSettingBuilder result(IResultComponent resultComponent){
//            this.resultComponents.add(resultComponent);
//            return this;
//        }
//
//        public RaidSettingBuilder range(int raidRange){
//            this.raidRange = raidRange;
//            return this;
//        }
//
//        public RaidSettingBuilder blockInside(boolean block){
//            this.blockInside = block;
//            return this;
//        }
//
//        public RaidSettingBuilder blockOutside(boolean block) {
//            this.blockOutside = block;
//            return this;
//        }
//
//        public RaidSettingBuilder renderBorder(boolean render){
//            this.renderBorder = render;
//            return this;
//        }
//
//        public RaidSettingBuilder borderColor(int color){
//            this.borderColor = color;
//            return this;
//        }
//
//        public RaidSettingBuilder victoryDuration(int victoryDuration){
//            this.victoryDuration = victoryDuration;
//            return this;
//        }
//
//        public RaidSettingBuilder lossDuration(int lossDuration){
//            this.lossDuration = lossDuration;
//            return this;
//        }
//
//        public RaidSettingBuilder showRoundTitle(boolean showRoundTitle){
//            this.showRoundTitle = showRoundTitle;
//            return this;
//        }
//
//        public RaidSettingBuilder color(BossEvent.BossBarColor color){
//            this.raidColor = color;
//            return this;
//        }
//
//        public RaidSettingBuilder title(MutableComponent title){
//            this.raidTitle = title;
//            return this;
//        }
//
//        public RaidSettingBuilder victoryTitle(MutableComponent title){
//            this.victoryTitle = title;
//            return this;
//        }
//
//        public RaidSettingBuilder lossTitle(MutableComponent title){
//            this.lossTitle = title;
//            return this;
//        }
//
//        public RaidSettingBuilder raidSound(SoundEvent soundEvent){
//            this.raidStartSound = soundEvent;
//            return this;
//        }
//
//        public RaidSettingBuilder waveSound(SoundEvent soundEvent){
//            this.waveStartSound = soundEvent;
//            return this;
//        }
//
//        public RaidSettingBuilder victorySound(SoundEvent soundEvent){
//            this.victorySound = soundEvent;
//            return this;
//        }
//
//        public RaidSettingBuilder lossSound(SoundEvent soundEvent){
//            this.lossSound = soundEvent;
//            return this;
//        }
//
//        public RaidComponent.RaidSetting build(){
//            return new RaidComponent.RaidSetting(
//                    this.placeComponent,
//                    new RaidComponent.BorderSetting(
//                            this.raidRange,
//                            this.blockInside,
//                            this.blockOutside,
//                            this.renderBorder,
//                            this.borderColor
//                    ),
//                    new RaidComponent.BarSetting(
//                            this.raidTitle,
//                            this.raidColor,
//                            this.victoryTitle,
//                            this.lossTitle
//                    ),
//                    new RaidComponent.SoundSetting(
//                            Optional.of(Holder.direct(this.raidStartSound)),
//                            Optional.of(Holder.direct(this.waveStartSound)),
//                            Optional.of(Holder.direct(this.victorySound)),
//                            Optional.of(Holder.direct(this.lossSound))
//                    ),
//                    this.resultComponents,
//                    this.victoryDuration,
//                    this.lossDuration,
//                    this.showRoundTitle
//            );
//        }
//
//    }

}
