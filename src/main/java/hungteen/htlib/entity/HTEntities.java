package hungteen.htlib.entity;

import hungteen.htlib.HTLib;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-13 23:07
 **/
public class HTEntities{

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =  DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, HTLib.MOD_ID);

    public static final RegistryObject<EntityType<HTBoat>> BOAT = ENTITY_TYPES.register("boat", () -> {
        return EntityType.Builder.<HTBoat>of(HTBoat::new, MobCategory.MISC).sized(1.375F, 0.5625F).clientTrackingRange(10).build(HTLib.prefix("boat").toString());
    });

    public static final RegistryObject<EntityType<HTBoat>> CHEST_BOAT = ENTITY_TYPES.register("chest_boat", () -> {
        return EntityType.Builder.<HTBoat>of(HTChestBoat::new, MobCategory.MISC).sized(1.375F, 0.5625F).clientTrackingRange(10).build(HTLib.prefix("chest_boat").toString());
    });

//    private static <T extends Entity> RegistryObject<EntityType<T>> registerEntityType(EntityType.EntityFactory factory, String name, MobCategory classification){
//        return ENTITY_TYPES.register(name, () -> EntityType.Builder.of(factory, classification).build(HTLib.prefix(name).toString()));
//    }
}
