package hungteen.htlib.common.block;

import hungteen.htlib.common.block.plant.HTStemGrownBlock;
import hungteen.htlib.util.helper.impl.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.neoforged.neoforge.common.CommonHooks;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Copy from {@link StemBlock}}.
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-06 22:38
 **/
public abstract class HTStemBlock extends CropBlock {

    private final Supplier<Item> seedSupplier;

    public HTStemBlock(Supplier<Item> seedSupplier, BlockBehaviour.Properties properties) {
        super(properties);
        this.seedSupplier = seedSupplier;
        this.registerDefaultState(this.stateDefinition.any().setValue(getAgeProperty(), 0));
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource random) {
        // Forge: prevent loading unloaded chunks when checking neighbor's light.
        if (!serverLevel.isAreaLoaded(blockPos, 1)) {
            return;
        }
        if (serverLevel.getRawBrightness(blockPos, 0) >= 9) {
            final float f = getGrowSpeed(serverLevel, blockState, blockPos);
            if (f > 0 && CommonHooks.canCropGrow(serverLevel, blockPos, blockState, random.nextInt((int) (25.0F / f) + 1) == 0)) {
                final int age = getAge(blockState);
                if (age < getMaxAge()) {
                    serverLevel.setBlock(blockPos, setAge(blockState, age + 1), 2);
                } else {
                    grow(serverLevel, blockState, blockPos, random);
                }
                CommonHooks.fireCropGrowPost(serverLevel, blockPos, blockState);
            }
        }
    }

    /**
     * Grow fruit.
     */
    public void grow(ServerLevel serverLevel, BlockState blockState, BlockPos blockPos, RandomSource random) {
        final Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        final BlockPos blockpos = blockPos.relative(direction);
        final BlockState belowBlockstate = serverLevel.getBlockState(blockpos.below());
        final Block belowBlock = belowBlockstate.getBlock();
        if (serverLevel.isEmptyBlock(blockpos) && (belowBlock instanceof FarmBlock || belowBlockstate.is(BlockTags.DIRT))) {
            getResultFruit(random).ifPresent(resultFruit -> {
                serverLevel.setBlockAndUpdate(blockpos, BlockHelper.setProperty(resultFruit.defaultBlockState(), HorizontalDirectionalBlock.FACING, direction));
                serverLevel.setBlockAndUpdate(blockPos, BlockHelper.setProperty(resultFruit.getAttachedStem().defaultBlockState(), HorizontalDirectionalBlock.FACING, direction));
            });
        }
    }

    protected float getGrowSpeed(ServerLevel serverLevel, BlockState blockState, BlockPos blockPos) {
        return CropBlock.getGrowthSpeed(blockState, serverLevel, blockPos);
    }

    protected Optional<HTStemGrownBlock> getResultFruit(RandomSource random) {
        return Optional.empty();
    }

    @Override
    public abstract IntegerProperty getAgeProperty();

    @Override
    public abstract int getMaxAge();

    public BlockState maxAgeState(){
        return setAge(defaultBlockState(), getMaxAge());
    }

    public BlockState setAge(BlockState state, int age) {
        return state.setValue(getAgeProperty(), age);
    }

    @Override
    public int getAge(BlockState state) {
        return state.getValue(getAgeProperty());
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter blockGetter, BlockPos blockPos) {
        return state.is(Blocks.FARMLAND);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader blockGetter, BlockPos blockPos, BlockState blockState) {
        return getAge(blockState) < getMaxAge();
    }

    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource random, BlockPos blockPos, BlockState oldState) {
        final int nextAge = Math.min(getMaxAge(), getAge(oldState) + getBonemealAgeIncrease(serverLevel));
        BlockState newState = setAge(oldState, nextAge);
        serverLevel.setBlock(blockPos, newState, 2);
        if (nextAge == getMaxAge()) {
            newState.randomTick(serverLevel, blockPos, serverLevel.random);
        }
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return super.isRandomlyTicking(state);
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return this.seedSupplier.get();
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos blockPos, BlockState state) {
        return true;
    }

    @Override
    protected int getBonemealAgeIncrease(Level level) {
        return Mth.nextInt(level.random, 2, 5);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_57040_) {
        p_57040_.add(getAgeProperty());
    }

}
