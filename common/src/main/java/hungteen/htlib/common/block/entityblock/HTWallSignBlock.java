package hungteen.htlib.common.block.entityblock;

import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.properties.WoodType;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-13 22:19
 **/
public class HTWallSignBlock extends WallSignBlock {

    public HTWallSignBlock(Properties properties, WoodType woodType) {
        super(woodType, properties);
    }

//    @Override
//    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
//        return new HTSignBlockEntity(pos, state);
//    }

}
