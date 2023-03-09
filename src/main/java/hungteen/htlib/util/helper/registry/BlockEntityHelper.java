package hungteen.htlib.util.helper.registry;

import net.minecraft.core.Registry;
import net.minecraft.world.level.block.entity.BlockEntityType;

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
    public Registry<BlockEntityType<?>> getRegistry() {
        return Registry.BLOCK_ENTITY_TYPE;
    }
}
