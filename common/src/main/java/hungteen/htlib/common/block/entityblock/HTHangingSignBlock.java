package hungteen.htlib.common.block.entityblock;

import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.state.properties.WoodType;

/**
 * TODO 告示牌方块实体
 * @author PangTeen
 * @program HTLib
 * @data 2023/4/13 15:49
 */
public class HTHangingSignBlock extends CeilingHangingSignBlock {

    public HTHangingSignBlock(Properties properties, WoodType woodType) {
        super(woodType, properties);
    }

//    @Override
//    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
//        return new HTHangingSignBlockEntity(pos, state);
//    }
}
