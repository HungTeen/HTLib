package hungteen.htlib.common.blockentity;

import hungteen.htlib.api.HTLibAPI;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.common.impl.registry.HTVanillaRegistry;
import hungteen.htlib.common.suit.TreeSuits;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/4/14 8:44
 */
public class HTBlockEntities {

    private static final HTVanillaRegistry<BlockEntityType<?>> BLOCK_ENTITIES = HTRegistryManager.vanilla(Registries.BLOCK_ENTITY_TYPE, HTLibAPI.id());

    public static final Supplier<BlockEntityType<HTSignBlockEntity>> SIGN = BLOCK_ENTITIES.register("sign", () -> BlockEntityType.Builder.of(
            HTSignBlockEntity::new, TreeSuits.getSignBlocks().toArray(new Block[0])
    ).build(null));

    public static final Supplier<BlockEntityType<HTHangingSignBlockEntity>> HANGING_SIGN = BLOCK_ENTITIES.register("hanging_sign", () -> BlockEntityType.Builder.of(
            HTHangingSignBlockEntity::new, TreeSuits.getHangingSignBlocks().toArray(new Block[0])
    ).build(null));

    public static HTVanillaRegistry<BlockEntityType<?>> registry(){
        return BLOCK_ENTITIES;
    }

}
