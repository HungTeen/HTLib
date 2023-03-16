package hungteen.htlib.util.helper.registry;

import hungteen.htlib.util.helper.MathHelper;
import hungteen.htlib.util.helper.StringHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;

import java.util.*;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-07 08:48
 **/
public class BlockHelper extends RegistryHelper<Block> {

    /**
     * Axe Strip when {@link hungteen.htlib.common.event.HTBlockEvents#onToolModifyBlock(BlockEvent.BlockToolModificationEvent)}
     */
    private static final Map<Block, Block> STRIPPABLES = new HashMap<>();
    private static final List<WoodType> WOOD_TYPES = Collections.synchronizedList(List.of());
    private static final BlockHelper HELPER = new BlockHelper();

    public static ResourceLocation blockTexture(Block block){
        return StringHelper.blockTexture(BlockHelper.get().getKey(block));
    }

    public static ResourceLocation blockTexture(Block block, String suffix){
        return StringHelper.blockTexture(BlockHelper.get().getKey(block), suffix);
    }

    public static boolean stillValid(Player player, BlockEntity entity) {
        if (player.level.getBlockEntity(entity.getBlockPos()) != entity) {
            return false;
        } else {
            return player.distanceToSqr(MathHelper.toVec3(entity.getBlockPos())) <= 64;
        }
    }

    /**
     * 不保证线程安全，请使用{@link ParallelDispatchEvent#enqueueWork(Runnable)}. <br>
     * remember to subscribe {@link net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent}.
     */
    public static void registerCompostable(float chance, ItemLike itemIn) {
        ComposterBlock.COMPOSTABLES.put(itemIn.asItem(), chance);
    }

    /**
     * 不保证线程安全，请使用{@link ParallelDispatchEvent#enqueueWork(Runnable)}. <br>
     * remember to subscribe {@link net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent}.
     */
    public static void registerAxeStrip(Block oldState, Block newState) {
        BlockHelper.STRIPPABLES.put(oldState, newState);
    }

    /**
     * register wood entityType.
     */
    public static void registerWoodType(WoodType woodType){
        WoodType.register(woodType);
        WOOD_TYPES.add(woodType);
    }

    /**
     * {@link hungteen.htlib.client.ClientRegister#clientSetUp(FMLClientSetupEvent)}
     */
    public static List<WoodType> getWoodTypes(){
        return Collections.unmodifiableList(WOOD_TYPES);
    }

    /**
     * Can block be stripped by Axe.
     */
    public static boolean canBeStripped(Block block){
        return STRIPPABLES.containsKey(block);
    }

    /**
     * Get the stripped result block.
     */
    public static Block getStrippedBlock(Block block){
        return STRIPPABLES.getOrDefault(block, block);
    }

    /**
     * Set property only when there is a property for state.
     */
    public static <T extends Comparable<T>> BlockState setProperty(BlockState state, Property<T> property, T value) {
        return state.hasProperty(property) ? state.setValue(property, value) : state;
    }

    /* Common Methods */

    public static BlockHelper get(){
        return HELPER;
    }

    @Override
    public Registry<Block> getRegistry() {
        return Registry.BLOCK;
    }

}
