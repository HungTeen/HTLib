package hungteen.htlib.util.helper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-07 22:55
 **/
public class WorldHelper {

    /**
     * @param pos center position.
     * @param minR minimum distance.
     * @param maxR maximum distance.
     * @return result position.
     */
    public static BlockPos getSuitableHeightRandomPos(Level world, BlockPos pos, int minR, int maxR) {
        BlockPos offset = RandomHelper.blockPosRange(world.random, minR, maxR);
        return getSuitableHeightPos(world, pos.offset(offset.getX(), 0, offset.getZ()));
    }

    public static BlockPos getSuitableHeightRandomPos(Level world, BlockPos pos, int maxR) {
        return getSuitableHeightRandomPos(world, pos, 0, maxR);
    }

    public static BlockPos getSuitableHeightPos(Level world, BlockPos pos) {
        int y = world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ());
        return new BlockPos(pos.getX(), y, pos.getZ());
    }
}
