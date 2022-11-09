package hungteen.htlib.block.plants;

import hungteen.htlib.util.helper.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.function.Supplier;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-06 22:38
 *
 * Copy from {@link StemBlock}}.
 **/
public abstract class HTStemBlock extends BushBlock implements BonemealableBlock {

    public static final int MAX_AGE = 7;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_7;
    private final Supplier<Item> seedSupplier;

    public HTStemBlock(Supplier<Item> seedSupplier, BlockBehaviour.Properties properties) {
        super(properties);
        this.seedSupplier = seedSupplier;
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource random) {
        if (!serverLevel.isAreaLoaded(blockPos, 1)){
            return; // Forge: prevent loading unloaded chunks when checking neighbor's light
        }
        if (serverLevel.getRawBrightness(blockPos, 0) >= 9) {
            final float f = CropBlock.getGrowthSpeed(this, serverLevel, blockPos);
            if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(serverLevel, blockPos, blockState, random.nextInt((int) (25.0F / f) + 1) == 0)) {
                final int i = blockState.getValue(AGE);
                if (i < 7) {
                    serverLevel.setBlock(blockPos, blockState.setValue(AGE, i + 1), 2);
                } else {
                    final Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                    final BlockPos blockpos = blockPos.relative(direction);
                    final BlockState blockstate = serverLevel.getBlockState(blockpos.below());
                    final Block block = blockstate.getBlock();
                    if (serverLevel.isEmptyBlock(blockpos) && (blockstate.canSustainPlant(serverLevel, blockpos.below(), Direction.UP, this) || block == Blocks.FARMLAND || block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.PODZOL || block == Blocks.GRASS_BLOCK)) {
                        final HTStemGrownBlock resultFruit = getResultFruit(random);
                        serverLevel.setBlockAndUpdate(blockpos, BlockHelper.setProperty(resultFruit.defaultBlockState(), HorizontalDirectionalBlock.FACING, direction));
                        serverLevel.setBlockAndUpdate(blockPos, BlockHelper.setProperty(resultFruit.getAttachedStem().defaultBlockState(), HorizontalDirectionalBlock.FACING, direction));
                    }
                }
                net.minecraftforge.common.ForgeHooks.onCropsGrowPost(serverLevel, blockPos, blockState);
            }
        }
    }

    protected abstract HTStemGrownBlock getResultFruit(RandomSource random);

    BlockState setAge(BlockState state, int age){
        return state.setValue(AGE, age);
    }

    protected int getAge(BlockState state){
        return state.getValue(AGE);
    }

    protected int getMaxAge(){
        return MAX_AGE;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter blockGetter, BlockPos blockPos) {
        return state.is(Blocks.FARMLAND);
    }

    public boolean isValidBonemealTarget(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, boolean p_57033_) {
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

    public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
        return new ItemStack(this.seedSupplier.get());
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos blockPos, BlockState state) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_57040_) {
        p_57040_.add(AGE);
    }

    //FORGE START
    @Override
    public net.minecraftforge.common.PlantType getPlantType(BlockGetter world, BlockPos pos) {
        return net.minecraftforge.common.PlantType.CROP;
    }
}
