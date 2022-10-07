package hungteen.htlib.block.plants;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-07 10:03
 **/
public abstract class HTAttachedStemBlock extends BushBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private final Supplier<Item> seedSupplier;

    public HTAttachedStemBlock(Supplier<Item> itemSupplier, BlockBehaviour.Properties properties) {
        super(properties);
        this.seedSupplier = itemSupplier;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState nearbyState, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos pos) {
        return direction == blockState.getValue(FACING) && ! isGrownFruit(nearbyState) ? getStemState() : super.updateShape(blockState, direction, nearbyState, levelAccessor, blockPos, pos);
    }

    protected abstract boolean isGrownFruit(BlockState blockState);

    protected abstract BlockState getStemState();

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter blockGetter, BlockPos blockPos) {
        return state.is(Blocks.FARMLAND);
    }

    public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
        return new ItemStack(this.seedSupplier.get());
    }

    @Override
    public BlockState rotate(BlockState blockState, Rotation rotation) {
        return blockState.setValue(FACING, rotation.rotate(blockState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState blockState, Mirror mirror) {
        return blockState.rotate(mirror.getRotation(blockState.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(FACING);
    }
}
