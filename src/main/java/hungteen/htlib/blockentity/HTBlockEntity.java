package hungteen.htlib.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-09 09:33
 **/
public class HTBlockEntity extends BlockEntity {

    public HTBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState state) {
        super(blockEntityType, blockPos, state);
    }

    public Random getRandom(){
        return this.level.getRandom();
    }

}
