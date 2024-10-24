package hungteen.htlib.common.block.wood;

import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/23 22:05
 **/
public class HTButtonBlock extends ButtonBlock {

    /**
     * public constructor of ButtonBlock.
     */
    public HTButtonBlock(BlockSetType blockSetType, int stayTicks, Properties properties) {
        super(blockSetType, stayTicks, properties);
    }
}
