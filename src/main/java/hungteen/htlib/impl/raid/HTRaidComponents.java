package hungteen.htlib.impl.raid;

import com.mojang.serialization.Codec;
import hungteen.htlib.HTLib;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryHolder;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.htlib.common.world.raid.RaidComponent;
import hungteen.htlib.impl.placement.HTPlaceComponents;
import hungteen.htlib.impl.spawn.BaseSpawn;
import hungteen.htlib.impl.spawn.OnceSpawn;
import hungteen.htlib.impl.wave.BaseWave;
import hungteen.htlib.impl.wave.CommonWave;
import hungteen.htlib.util.interfaces.IRaidComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
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
                    BaseRaid.builder().build(),
                    Arrays.asList(
                            new CommonWave(
                                    new BaseWave.WaveSettings(
                                            Optional.empty(),
                                            100,
                                            1000,
                                            false
                                    ),
                                    Arrays.asList(
                                            new OnceSpawn(
                                                    new BaseSpawn.SpawnSettings(
                                                            EntityType.CREEPER,
                                                            new CompoundTag(),
                                                            true,
                                                            Optional.ofNullable(HTPlaceComponents.DEFAULT.getValue())
                                                    ),
                                                    10,
                                                    10
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

}
