package hungteen.htlib.common.item;

import hungteen.htlib.common.WoodIntegrations;
import hungteen.htlib.common.entity.HTBoat;
import hungteen.htlib.common.entity.HTChestBoat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-14 09:13
 *
 * Modify from {@link net.minecraft.core.dispenser.BoatDispenseItemBehavior}
 **/
public class HTBoatDispenseItemBehavior extends DefaultDispenseItemBehavior {

    private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();
    private final WoodIntegrations.IBoatType type;
    private final boolean hasChest;

    public HTBoatDispenseItemBehavior(WoodIntegrations.IBoatType type, boolean hasChest) {
        this.type = type;
        this.hasChest = hasChest;
    }

    @Override
    public ItemStack execute(BlockSource blockSource, ItemStack stack) {
        Direction direction = blockSource.getBlockState().getValue(DispenserBlock.FACING);
        Level level = blockSource.getLevel();
        double d0 = blockSource.x() + (double)((float)direction.getStepX() * 1.125F);
        double d1 = blockSource.y() + (double)((float)direction.getStepY() * 1.125F);
        double d2 = blockSource.z() + (double)((float)direction.getStepZ() * 1.125F);
        BlockPos blockpos = blockSource.getPos().relative(direction);
        double d3;
        if (level.getFluidState(blockpos).is(FluidTags.WATER)) {
            d3 = 1.0D;
        } else {
            if (!level.getBlockState(blockpos).isAir() || !level.getFluidState(blockpos.below()).is(FluidTags.WATER)) {
                return this.defaultDispenseItemBehavior.dispense(blockSource, stack);
            }

            d3 = 0.0D;
        }

        HTBoat boat = hasChest ? new HTChestBoat(level, d0, d1 + d3, d2) : new HTBoat(level, d0, d1 + d3, d2);
        boat.setHTBoatType(this.type);
        boat.setYRot(direction.toYRot());
        level.addFreshEntity(boat);
        stack.shrink(1);
        return stack;
    }

    @Override
    protected void playSound(BlockSource blockSource) {
        blockSource.getLevel().levelEvent(1000, blockSource.getPos(), 0);
    }

}
