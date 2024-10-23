package hungteen.htlib.common.item;

import hungteen.htlib.common.entity.HTBoat;
import hungteen.htlib.common.entity.HTChestBoat;
import hungteen.htlib.common.entity.HasHTBoatType;
import hungteen.htlib.util.interfaces.BoatType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

/**
 * Modify from {@link net.minecraft.core.dispenser.BoatDispenseItemBehavior}
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-14 09:13
 **/
public class HTBoatDispenseItemBehavior extends DefaultDispenseItemBehavior {

    private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();
    private final BoatType type;
    private final boolean hasChest;

    public HTBoatDispenseItemBehavior(BoatType type, boolean hasChest) {
        this.type = type;
        this.hasChest = hasChest;
    }

    @Override
    public ItemStack execute(BlockSource blockSource, ItemStack stack) {
        Direction direction = blockSource.state().getValue(DispenserBlock.FACING);
        Level level = blockSource.level();
        double d0 = blockSource.pos().getX() + (double)((float)direction.getStepX() * 1.125F);
        double d1 = blockSource.pos().getY() + (double)((float)direction.getStepY() * 1.125F);
        double d2 = blockSource.pos().getZ() + (double)((float)direction.getStepZ() * 1.125F);
        BlockPos blockpos = blockSource.pos().relative(direction);
        double d3;
        if (level.getFluidState(blockpos).is(FluidTags.WATER)) {
            d3 = 1.0D;
        } else {
            if (!level.getBlockState(blockpos).isAir() || !level.getFluidState(blockpos.below()).is(FluidTags.WATER)) {
                return this.defaultDispenseItemBehavior.dispense(blockSource, stack);
            }

            d3 = 0.0D;
        }

        Boat boat = hasChest ? new HTChestBoat(level, d0, d1 + d3, d2) : new HTBoat(level, d0, d1 + d3, d2);
        HasHTBoatType htBoat = (HasHTBoatType) boat;
        htBoat.setHTBoatType(this.type);
        boat.setYRot(direction.toYRot());
        level.addFreshEntity(boat);
        stack.shrink(1);
        return stack;
    }

    @Override
    protected void playSound(BlockSource blockSource) {
        blockSource.level().levelEvent(1000, blockSource.pos(), 0);
    }

}
