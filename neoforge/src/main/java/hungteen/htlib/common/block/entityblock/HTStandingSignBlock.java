package hungteen.htlib.common.block.entityblock;

import hungteen.htlib.common.blockentity.HTSignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-13 22:26
 **/
public class HTStandingSignBlock extends StandingSignBlock {

    public HTStandingSignBlock(Properties properties, WoodType woodType) {
        super(woodType, properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HTSignBlockEntity(pos, state);
    }

}
