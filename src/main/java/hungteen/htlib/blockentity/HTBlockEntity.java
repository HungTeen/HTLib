package hungteen.htlib.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-09 09:33
 **/
public class HTBlockEntity extends BlockEntity {

    public HTBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState state) {
        super(blockEntityType, blockPos, state);
    }

    public RandomSource getRandom(){
        return this.level.getRandom();
    }

}
