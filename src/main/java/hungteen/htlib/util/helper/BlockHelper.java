package hungteen.htlib.util.helper;

import hungteen.htlib.util.Pair;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-07 08:48
 **/
public class BlockHelper extends RegistryHelper<Block>{

    /**
     * Axe Strip when {@link hungteen.htlib.event.HTBlockEvents#onToolModifyBlock(BlockEvent.BlockToolModificationEvent)}
     */
    private static final Map<Block, Block> STRIPPABLES = new HashMap<>();
    private static final BlockHelper HELPER = new BlockHelper();
    private static final BlockEntityHelper BLOCK_ENTITY_HELPER = new BlockEntityHelper();

    public static ResourceLocation blockTexture(Block block){
        return StringHelper.blockTexture(BlockHelper.getKey(block));
    }

    public static ResourceLocation blockTexture(Block block, String suffix){
        return StringHelper.blockTexture(BlockHelper.getKey(block), suffix);
    }

    public static boolean stillValid(Player player, BlockEntity entity) {
        if (player.level.getBlockEntity(entity.getBlockPos()) != entity) {
            return false;
        } else {
            return player.distanceToSqr(MathHelper.toVec3(entity.getBlockPos())) <= 64;
        }
    }

    /**
     * remember to subscribe {@link net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent}.
     */
    public static void registerCompostable(float chance, ItemLike itemIn) {
        ComposterBlock.COMPOSTABLES.put(itemIn.asItem(), chance);
    }

    /**
     * remember to subscribe {@link net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent}.
     */
    public static void registerAxeStrip(Block oldState, Block newState) {
        BlockHelper.STRIPPABLES.put(oldState, newState);
    }

    /**
     * Set property only when there is a property for state.
     */
    public static <T extends Comparable<T>> BlockState setProperty(BlockState state, Property<T> property, T value) {
        return state.hasProperty(property) ? state.setValue(property, value) : state;
    }

    /**
     * Get predicate registry objects.
     */
    public static List<Block> getFilterBlocks(Predicate<Block> predicate) {
        return HELPER.getFilterObjects(predicate);
    }

    /**
     * Get all registered objects with keys.
     */
    public static Collection<Pair<ResourceKey<Block>, Block>> getBlockWithKeys() {
        return HELPER.getObjectWithKeys();
    }

    /**
     * Get key of specific object.
     */
    public static ResourceLocation getKey(Block object) {
        return HELPER.getResourceLocation(object);
    }

    /**
     * Get predicate registry objects.
     */
    public static List<BlockEntityType<?>> getFilterBlockEntityTypes(Predicate<BlockEntityType<?>> predicate) {
        return BLOCK_ENTITY_HELPER.getFilterObjects(predicate);
    }

    /**
     * Get all registered objects with keys.
     */
    public static Collection<Pair<ResourceKey<BlockEntityType<?>>, BlockEntityType<?>>> getBlockEntityTypeWithKeys() {
        return BLOCK_ENTITY_HELPER.getObjectWithKeys();
    }

    /**
     * Get key of specific object.
     */
    public static ResourceLocation getKey(BlockEntityType<?> object) {
        return BLOCK_ENTITY_HELPER.getResourceLocation(object);
    }

    @Override
    public IForgeRegistry<Block> getForgeRegistry() {
        return ForgeRegistries.BLOCKS;
    }

    private static class BlockEntityHelper extends RegistryHelper<BlockEntityType<?>>{

        @Override
        public IForgeRegistry<BlockEntityType<?>> getForgeRegistry() {
            return ForgeRegistries.BLOCK_ENTITY_TYPES;
        }
    }
}
