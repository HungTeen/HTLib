package hungteen.htlib.common.block.entityblock;

import hungteen.htlib.common.blockentity.HTHangingSignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/4/13 15:49
 */
public class HTHangingSignBlock extends CeilingHangingSignBlock {

    public HTHangingSignBlock(Properties properties, WoodType woodType) {
        super(properties, woodType);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HTHangingSignBlockEntity(pos, state);
    }
}
