package hungteen.htlib.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-07 09:05
 **/
public class HTFenceGateBlock extends FenceGateBlock {


    public HTFenceGateBlock(Properties properties, SoundEvent closeSound, SoundEvent openSound) {
        super(properties, closeSound, openSound);
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return 5;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return 20;
    }
}
