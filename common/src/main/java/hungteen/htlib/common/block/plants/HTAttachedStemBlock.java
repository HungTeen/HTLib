package hungteen.htlib.common.block.plants;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.HitResult;

import java.util.function.Supplier;

/**
 * Copy from {@link AttachedStemBlock}}.
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
        return direction == blockState.getValue(FACING) && ! isGrownFruit(blockState, nearbyState) ? getStemState() : super.updateShape(blockState, direction, nearbyState, levelAccessor, blockPos, pos);
    }

    protected abstract boolean isGrownFruit(BlockState selfState, BlockState nearbyState);

    protected abstract BlockState getStemState();

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter blockGetter, BlockPos blockPos) {
        return state.is(Blocks.FARMLAND);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
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
    public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation direction) {
        return state.setValue(FACING, direction.rotate(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(FACING);
    }
}
