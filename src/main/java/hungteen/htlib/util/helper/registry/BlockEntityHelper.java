package hungteen.htlib.util.helper.registry;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/2/22 18:40
 */
public class BlockEntityHelper extends RegistryHelper<BlockEntityType<?>> {

    private static final BlockEntityHelper HELPER = new BlockEntityHelper();

    /* Common Methods */

    public static BlockEntityHelper get(){
        return HELPER;
    }

    @Override
    public Either<IForgeRegistry<BlockEntityType<?>>, Registry<BlockEntityType<?>>> getRegistry() {
        return Either.left(ForgeRegistries.BLOCK_ENTITY_TYPES);
    }
}
