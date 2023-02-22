package hungteen.htlib.util.helper;

import hungteen.htlib.util.Pair;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/2/22 18:40
 */
public class BlockEntityHelper extends RegistryHelper<BlockEntityType<?>>{

    private static final BlockEntityHelper HELPER = new BlockEntityHelper();

    /**
     * Get predicate registry objects.
     */
    public static List<BlockEntityType<?>> getFilterBlockEntityTypes(Predicate<BlockEntityType<?>> predicate) {
        return get().getFilterObjects(predicate);
    }

    /**
     * Get all registered objects with keys.
     */
    public static Collection<Pair<ResourceKey<BlockEntityType<?>>, BlockEntityType<?>>> getBlockEntityTypeWithKeys() {
        return get().getObjectWithKeys();
    }

    /**
     * Get key of specific object.
     */
    public static ResourceLocation getKey(BlockEntityType<?> object) {
        return get().getResourceLocation(object);
    }

    public static BlockEntityHelper get(){
        return HELPER;
    }

    @Override
    public IForgeRegistry<BlockEntityType<?>> getForgeRegistry() {
        return ForgeRegistries.BLOCK_ENTITY_TYPES;
    }
}
