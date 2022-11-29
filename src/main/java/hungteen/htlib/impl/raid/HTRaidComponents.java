package hungteen.htlib.impl.raid;

import com.mojang.serialization.Codec;
import hungteen.htlib.HTLib;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.htlib.common.world.raid.RaidComponent;
import hungteen.htlib.util.interfaces.IRaidComponentType;
import net.minecraft.resources.ResourceLocation;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/18 10:37
 */
public class HTRaidComponents {

    private static final HTSimpleRegistry<IRaidComponentType<?>> RAID_TYPES = HTRegistryManager.create(HTLib.prefix("raid_type"));
    private static final HTCodecRegistry<RaidComponent> RAIDS = HTRegistryManager.create(RaidComponent.class, "custom_raid/raids", HTRaidComponents::getCodec);

    /* Spawn types */

//    public static final IRaidComponentType<OnceSpawn> COMMON_RAID_TYPE = new DefaultRaidType<>();

    /* Spawns */

//    public static final HTRegistryHolder<SpawnPlacement> DEFAULT = SPAWNS.innerRegister(
//            HTLib.prefix("default"), new CenterAreaPlacement(
//                    Vec3.ZERO, 0, 1, true, 0, true
//            )
//    );

    /**
     * {@link HTLib#HTLib()}
     */
    public static void registerStuffs(){
//        List.of(ONCE_SPAWN_TYPE, DURATION_SPAWN_TYPE).forEach(HTSpawnComponents::registerSpawnType);
    }

    public static IRaidComponentType<?> getRaidType(ResourceLocation location){
        return RAID_TYPES.getValue(location).orElse(null);
    }

    public static RaidComponent getRaidComponent(ResourceLocation location){
        return RAIDS.getValue(location).orElse(null);
    }

    public static void registerSpawnType(IRaidComponentType<?> type){
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
