package hungteen.htlib.common.blockentity;

import hungteen.htlib.HTLib;
import hungteen.htlib.common.WoodIntegrations;
import hungteen.htlib.util.helper.registry.BlockHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/4/14 8:44
 */
public class HTBlockEntities {

    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = BlockHelper.entity().createRegister(HTLib.id());

    public static final RegistryObject<BlockEntityType<HTSignBlockEntity>> SIGN = BLOCK_ENTITIES.register("sign", () -> BlockEntityType.Builder.of(
            HTSignBlockEntity::new, WoodIntegrations.getSignBlocks().toArray(new Block[0])
    ).build(null));

    public static final RegistryObject<BlockEntityType<HTHangingSignBlockEntity>> HANGING_SIGN = BLOCK_ENTITIES.register("sign", () -> BlockEntityType.Builder.of(
            HTHangingSignBlockEntity::new, WoodIntegrations.getHangingSignBlocks().toArray(new Block[0])
    ).build(null));

}
