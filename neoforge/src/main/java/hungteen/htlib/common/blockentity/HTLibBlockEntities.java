package hungteen.htlib.common.blockentity;

import hungteen.htlib.api.HTLibAPI;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.common.impl.registry.HTVanillaRegistry;
import hungteen.htlib.common.registry.HTLibWoodSuits;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/4/14 8:44
 */
public interface HTLibBlockEntities {

    HTVanillaRegistry<BlockEntityType<?>> BLOCK_ENTITIES = HTRegistryManager.vanilla(Registries.BLOCK_ENTITY_TYPE, HTLibAPI.id());

    Supplier<BlockEntityType<HTSignBlockEntity>> SIGN = BLOCK_ENTITIES.register("sign", () -> BlockEntityType.Builder.of(
            HTSignBlockEntity::new, HTLibWoodSuits.getSignBlocks().toArray(new Block[0])
    ).build(null));

    Supplier<BlockEntityType<HTHangingSignBlockEntity>> HANGING_SIGN = BLOCK_ENTITIES.register("hanging_sign", () -> BlockEntityType.Builder.of(
            HTHangingSignBlockEntity::new, HTLibWoodSuits.getHangingSignBlocks().toArray(new Block[0])
    ).build(null));

    static HTVanillaRegistry<BlockEntityType<?>> registry(){
        return BLOCK_ENTITIES;
    }

}
