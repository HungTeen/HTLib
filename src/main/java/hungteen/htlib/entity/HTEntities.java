package hungteen.htlib.entity;

import hungteen.htlib.HTLib;
import net.minecraft.Util;
import net.minecraft.world.entity.Entity;
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

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =  DeferredRegister.create(ForgeRegistries.ENTITIES, HTLib.MOD_ID);

    public static final RegistryObject<EntityType<HTBoat>> BOAT = registerEntityType(HTBoat::new, "boat", MobCategory.MISC);

    private static <T extends Entity> RegistryObject<EntityType<T>> registerEntityType(EntityType.EntityFactory factory, String name, MobCategory classification){
        return ENTITY_TYPES.register(name, () -> EntityType.Builder.of(factory, classification).build(HTLib.prefix(name).toString()));
    }
}
