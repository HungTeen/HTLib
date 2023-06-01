package hungteen.htlib.common.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/6/1 10:49
 */
public class HTBlock extends Block {

    public HTBlock(BlockBehaviour block) {
        super(Properties.copy(block));
    }

    public HTBlock(Properties properties) {
        super(properties);
    }

}
