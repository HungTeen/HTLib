package hungteen.htlib.block.entityblock;

import hungteen.htlib.blockentity.HTNameableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.naming.Name;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-09 09:45
 **/
public abstract class HTNameableEntityBlock extends HTEntityBlock{

    public HTNameableEntityBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack stack) {
        if (stack.hasCustomHoverName()) {
            BlockEntity blockentity = level.getBlockEntity(blockPos);
            if (blockentity instanceof HTNameableBlockEntity) {
                ((HTNameableBlockEntity) blockentity).setCustomName(stack.getHoverName());
            }
        }
    }
}
