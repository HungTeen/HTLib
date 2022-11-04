package hungteen.htlib.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-07 08:48
 **/
public class BlockUtil {

    /**
     * Axe Strip when {@link hungteen.htlib.event.HTBlockEvents#onToolModifyBlock(BlockEvent.BlockToolModificationEvent)}
     */
    public static final Map<Block, Block> STRIPPABLES = new HashMap<>();

    public static boolean stillValid(Player player, BlockEntity entity) {
        if (player.level.getBlockEntity(entity.getBlockPos()) != entity) {
            return false;
        } else {
            return player.distanceToSqr(MathUtil.toVector(entity.getBlockPos())) <= 64;
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
        BlockUtil.STRIPPABLES.put(oldState, newState);
    }

    /**
     * Set property only when there is a property for state.
     */
    public static <T extends Comparable<T>> BlockState setProperty(BlockState state, Property<T> property, T value) {
        return state.hasProperty(property) ? state.setValue(property, value) : state;
    }

    /**
     * get predicate blocks.
     */
    public static List<Block> getFilterBlocks(Predicate<Block> predicate) {
        return ForgeRegistries.BLOCKS.getValues().stream()
                .filter(predicate)
                .sorted(Comparator.comparing(ForgeRegistries.BLOCKS::getKey))
                .collect(Collectors.toList());
    }

}
