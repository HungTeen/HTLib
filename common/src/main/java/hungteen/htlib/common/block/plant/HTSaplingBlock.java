package hungteen.htlib.common.block.plant;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-11 22:33
 **/
public class HTSaplingBlock extends SaplingBlock {

    public HTSaplingBlock(TreeGrower treeGrower) {
        super(treeGrower, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING));
    }

    public HTSaplingBlock(TreeGrower treeGrower, Properties properties) {
        super(treeGrower, properties);
    }

}
