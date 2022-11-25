package hungteen.htlib.impl.placement;

import com.mojang.serialization.Codec;
import hungteen.htlib.HTLib;
import hungteen.htlib.api.SpawnPlacement;
import hungteen.htlib.api.interfaces.ISpawnPlacementType;
import hungteen.htlib.common.registry.HTCodecRegistry;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/18 10:37
 */
public class HTPlacements {

    public static final HTCodecRegistry<SpawnPlacement, ISpawnPlacementType<SpawnPlacement>>
    public static final DeferredRegister<ISpawnPlacementType<?>> PLACEMENT_TYPES = DeferredRegister.create(HTLib.prefix("placement_type"), HTLib.MOD_ID);
    public static final DeferredRegister<SpawnPlacement> PLACEMENTS = DeferredRegister.create(HTLib.prefix("spawn_placement"), HTLib.MOD_ID);
    public static final Supplier<IForgeRegistry<ISpawnPlacementType<?>>> PLACEMENT_TYPE_REGISTRY = PLACEMENT_TYPES.makeRegistry(RegistryBuilder::new);
    public static final Supplier<IForgeRegistry<SpawnPlacement>> PLACEMENT_REGISTRY = HTPlacements.PLACEMENTS.makeRegistry(() -> {
        RegistryBuilder<SpawnPlacement> registryBuilder = new RegistryBuilder<>();
        return registryBuilder.dataPackRegistry(HTPlacements.getCodec(), HTPlacements.getCodec());
    });

    /* Placement types */

    public static final RegistryObject<ISpawnPlacementType<CenterAreaPlacement>> CENTER_AREA_TYPE = PLACEMENT_TYPES.register("center_area", () -> () -> CenterAreaPlacement.CODEC);
    public static final RegistryObject<ISpawnPlacementType<AbsoluteAreaPlacement>> ABSOLUTE_AREA_TYPE = PLACEMENT_TYPES.register("absolute_area", () -> () -> AbsoluteAreaPlacement.CODEC);

    /* Placements */

    public static final RegistryObject<SpawnPlacement> TEST1 = PLACEMENTS.register("test1", () -> new CenterAreaPlacement(
       new CenterAreaPlacement.CenterAreaPlacementSetting(
               Vec3.ZERO, 10, 20.5, true, 0, true
       )
    ));

    public static final RegistryObject<SpawnPlacement> TEST2 = PLACEMENTS.register("test2", () -> new AbsoluteAreaPlacement(
            new AbsoluteAreaPlacement.AbsoluteAreaPlacementSetting(
                    Vec3.ZERO, 10, 20.5, true
            )
    ));

    public static Codec<SpawnPlacement> getCodec() {
        return HTPlacements.PLACEMENT_TYPE_REGISTRY.get().getCodec().dispatch(SpawnPlacement::getType, ISpawnPlacementType::codec);
    }

    public static void register(IEventBus bus){
        PLACEMENTS.register(bus);
        PLACEMENT_TYPES.register(bus);
    }

}
