package hungteen.htlib.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/4/14 9:09
 */
public class HTHangingSignBlockEntity extends HTSignBlockEntity {

    public HTHangingSignBlockEntity(BlockPos pos, BlockState state) {
        super(HTLibBlockEntities.HANGING_SIGN.get(), pos, state);
    }

    public int getTextLineHeight() {
        return 9;
    }

    public int getMaxTextLineWidth() {
        return 50;
    }

}
