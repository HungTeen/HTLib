package hungteen.htlib.common.block.entityblock;

import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-09 09:42
 **/
public abstract class HTEntityBlock extends BaseEntityBlock {

    public HTEntityBlock(Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
