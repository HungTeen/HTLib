package hungteen.htlib.common.block.plant;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-07 10:04
 **/
public abstract class HTStemGrownBlock extends HorizontalDirectionalBlock {

    public HTStemGrownBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public abstract HTAttachedStemBlock getAttachedStem();

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(FACING);
    }

}
