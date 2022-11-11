package hungteen.htlib.block.plants;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-11 22:33
 **/
public class HTSaplingBlock extends SaplingBlock {

    public HTSaplingBlock(AbstractTreeGrower treeGrower) {
        super(treeGrower, BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING));
    }

    public HTSaplingBlock(AbstractTreeGrower treeGrower, Properties properties) {
        super(treeGrower, properties);
    }

}
