package hungteen.htlib.util.helper;

import hungteen.htlib.util.Pair;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-07 08:53
 **/
public class EntityHelper extends RegistryHelper<EntityType<?>>{

    private static final EntityHelper HELPER = new EntityHelper();

    public static boolean isEntityValid(Entity entity) {
        return entity != null && entity.isAlive();
    }

    /**
     * Get predicate registry objects.
     */
    public static List<EntityType<?>> getFilterEntityTypes(Predicate<EntityType<?>> predicate) {
        return HELPER.getFilterObjects(predicate);
    }

    /**
     * Get all registered objects with keys.
     */
    public static Collection<Pair<ResourceKey<EntityType<?>>, EntityType<?>>> getEntityTypeWithKeys() {
        return HELPER.getObjectWithKeys();
    }

    /**
     * Get key of specific object.
     */
    public static ResourceLocation getKey(EntityType<?> object) {
        return HELPER.getResourceLocation(object);
    }

    @Override
    public IForgeRegistry<EntityType<?>> getForgeRegistry() {
        return ForgeRegistries.ENTITY_TYPES;
    }
}
