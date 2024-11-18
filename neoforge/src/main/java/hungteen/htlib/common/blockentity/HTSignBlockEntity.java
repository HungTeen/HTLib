package hungteen.htlib.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/4/14 8:52
 */
public class HTSignBlockEntity extends SignBlockEntity {

    public HTSignBlockEntity(BlockPos pos, BlockState state) {
        super(HTLibBlockEntities.SIGN.get(), pos, state);
    }

    public HTSignBlockEntity(BlockEntityType dataType, BlockPos pos, BlockState state) {
        super(dataType, pos, state);
    }

}
