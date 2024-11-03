package hungteen.htlib.common.block.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * TODO StemBlock 多平台不好兼容。
 * Copy from {@link StemBlock}}.
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-06 22:38
 **/
public abstract class HTStemBlock extends BushBlock implements BonemealableBlock {

    private final Supplier<Item> seedSupplier;

    public HTStemBlock(Supplier<Item> seedSupplier, BlockBehaviour.Properties properties) {
        super(properties);
        this.seedSupplier = seedSupplier;
        this.registerDefaultState(this.stateDefinition.any().setValue(getAgeProperty(), 0));
    }

//    @Override
//    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource random) {
//        // Forge: prevent loading unloaded chunks when checking neighbor's light.
//        if (!serverLevel.isAreaLoaded(blockPos, 1)) {
//            return;
//        }
//        if (serverLevel.getRawBrightness(blockPos, 0) >= 9) {
//            final float f = getGrowSpeed(serverLevel, blockState, blockPos);
//            if (f > 0 && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(serverLevel, blockPos, blockState, random.nextInt((int) (25.0F / f) + 1) == 0)) {
//                final int age = getAge(blockState);
//                if (age < getMaxAge()) {
//                    serverLevel.setBlock(blockPos, setAge(blockState, age + 1), 2);
//                } else {
//                    grow(serverLevel, blockState, blockPos, random);
//                }
//                net.minecraftforge.common.ForgeHooks.onCropsGrowPost(serverLevel, blockPos, blockState);
//            }
//        }
//    }
//
//    public void grow(ServerLevel serverLevel, BlockState blockState, BlockPos blockPos, RandomSource random) {
//        final Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
//        final BlockPos blockpos = blockPos.relative(direction);
//        final BlockState blockstate = serverLevel.getBlockState(blockpos.below());
//        final Block block = blockstate.getBlock();
//        if (serverLevel.isEmptyBlock(blockpos) && (blockstate.canSustainPlant(serverLevel, blockpos.below(), Direction.UP, this) || block == Blocks.FARMLAND || block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.PODZOL || block == Blocks.GRASS_BLOCK)) {
//            getResultFruit(random).ifPresent(resultFruit -> {
//                serverLevel.setBlockAndUpdate(blockpos, BlockHelper.setProperty(resultFruit.defaultBlockState(), HorizontalDirectionalBlock.FACING, direction));
//                serverLevel.setBlockAndUpdate(blockPos, BlockHelper.setProperty(resultFruit.getAttachedStem().defaultBlockState(), HorizontalDirectionalBlock.FACING, direction));
//            });
//        }
//    }
//
//    protected float getGrowSpeed(ServerLevel serverLevel, BlockState blockState, BlockPos blockPos) {
//        return CropBlock.getGrowthSpeed(this, serverLevel, blockPos);
//    }

    protected Optional<HTStemGrownBlock> getResultFruit(RandomSource random) {
        return Optional.empty();
    }

    public abstract IntegerProperty getAgeProperty();

    public abstract int getMaxAge();

    public BlockState maxAgeState(){
        return setAge(defaultBlockState(), getMaxAge());
    }

    public BlockState setAge(BlockState state, int age) {
        return state.setValue(getAgeProperty(), age);
    }

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
        final int nextAge = Math.min(getMaxAge(), getAge(oldState) + Mth.nextInt(serverLevel.random, 2, 5));
        BlockState newState = setAge(oldState, nextAge);
        serverLevel.setBlock(blockPos, newState, 2);
        if (nextAge == getMaxAge()) {
            newState.randomTick(serverLevel, blockPos, serverLevel.random);
        }
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader reader, BlockPos blockPos, BlockState blockState) {
        return new ItemStack(this.seedSupplier.get());
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos blockPos, BlockState state) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_57040_) {
        p_57040_.add(getAgeProperty());
    }

}
