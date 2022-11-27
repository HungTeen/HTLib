package hungteen.htlib.impl.placement;

import com.mojang.serialization.Codec;
import hungteen.htlib.HTLib;
import hungteen.htlib.common.world.raid.PlaceComponent;
import hungteen.htlib.util.interfaces.IPlaceComponentType;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryHolder;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/18 10:37
 */
public class HTPlacements {

    public static final HTSimpleRegistry<IPlaceComponentType<?>> PLACEMENT_TYPES = HTRegistryManager.create(HTLib.prefix("placement_type"));
    public static final HTCodecRegistry<PlaceComponent> PLACEMENTS = HTRegistryManager.create(PlaceComponent.class, "spawn_placements", HTPlacements::getCodec);

    /* Placement types */

    public static final IPlaceComponentType<CenterAreaPlacement> CENTER_AREA_TYPE = new DefaultSpawnPlacement<>("center_area",  CenterAreaPlacement.CODEC);
    public static final IPlaceComponentType<AbsoluteAreaPlacement> ABSOLUTE_AREA_TYPE = new DefaultSpawnPlacement<>("absolute_area",  AbsoluteAreaPlacement.CODEC);

    /* Placements */

    public static final HTRegistryHolder<PlaceComponent> DEFAULT = PLACEMENTS.innerRegister(
            HTLib.prefix("default"), new CenterAreaPlacement(
                    Vec3.ZERO, 0, 1, true, 0, true
            )
    );

    /**
     * {@link HTLib#HTLib()}
     */
    public static void registerStuffs(){
        List.of(CENTER_AREA_TYPE, ABSOLUTE_AREA_TYPE).forEach(HTPlacements::registerPlacementType);
    }

    public static void registerPlacementType(IPlaceComponentType<?> type){
        PLACEMENT_TYPES.register(type);
    }

    public static Codec<PlaceComponent> getCodec(){
        return PLACEMENT_TYPES.byNameCodec().dispatch(PlaceComponent::getType, IPlaceComponentType::codec);
    }

    protected record DefaultSpawnPlacement<P extends PlaceComponent>(String name, Codec<P> codec) implements IPlaceComponentType<P> {

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
