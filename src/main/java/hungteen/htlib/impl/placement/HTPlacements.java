package hungteen.htlib.impl.placement;

import com.mojang.serialization.Codec;
import hungteen.htlib.HTLib;
import hungteen.htlib.api.SpawnPlacement;
import hungteen.htlib.api.interfaces.ISpawnPlacementType;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;

import java.util.List;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/18 10:37
 */
public class HTPlacements {

    public static final HTSimpleRegistry<ISpawnPlacementType<?>> PLACEMENT_TYPES = HTRegistryManager.create(HTLib.prefix("placement_type"));
    public static final HTCodecRegistry<SpawnPlacement> PLACEMENTS = HTRegistryManager.create(SpawnPlacement.class, "spawn_placements", () -> {
        return PLACEMENT_TYPES.byNameCodec().dispatch(SpawnPlacement::getType, ISpawnPlacementType::codec);
    });

    /* Placement types */

    public static final ISpawnPlacementType<CenterAreaPlacement> CENTER_AREA_TYPE = new DefaultSpawnPlacement<>("center_area",  CenterAreaPlacement.CODEC);
    public static final ISpawnPlacementType<AbsoluteAreaPlacement> ABSOLUTE_AREA_TYPE = new DefaultSpawnPlacement<>("absolute_area",  AbsoluteAreaPlacement.CODEC);

    /* Placements */

//    public static final HTRegistryHolder<SpawnPlacement> TEST1 = PLACEMENTS.innerRegister(
//            HTLib.prefix("test1"), new CenterAreaPlacement(
//                    Vec3.ZERO, 10, 20.5, true, 0, true
//            )
//    );
//
//    public static final HTRegistryHolder<SpawnPlacement> TEST2 = PLACEMENTS.innerRegister(
//            HTLib.prefix("test2"), new AbsoluteAreaPlacement(
//                    Vec3.ZERO, 10, 20.5, true
//            )
//    );

    /**
     * {@link HTLib#HTLib()}
     */
    public static void registerStuffs(){
        List.of(CENTER_AREA_TYPE, ABSOLUTE_AREA_TYPE).forEach(HTPlacements::registerPlacementType);
    }

    public static void registerPlacementType(ISpawnPlacementType<?> type){
        PLACEMENT_TYPES.register(type);
    }

    protected record DefaultSpawnPlacement<P extends SpawnPlacement>(String name, Codec<P> codec) implements ISpawnPlacementType<P> {

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
