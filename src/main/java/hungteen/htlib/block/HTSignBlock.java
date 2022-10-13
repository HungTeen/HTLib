package hungteen.htlib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-13 22:19
 **/
public class HTSignBlock extends SignBlock {

    public HTSignBlock(Properties properties, WoodType woodType) {
        super(properties, woodType);
    }

}
