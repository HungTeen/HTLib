package hungteen.htlib.util.helper;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-02 16:58
 **/
public class MathHelper {

    /**
     * use for render bar.
     */
    public static int getBarLen(int num, int maxNum, int maxLen) {
        if(maxNum != 0){
            final int percent = num * maxLen / maxNum;
            if(percent <= 0 && num != 0) {
                return 1;
            } else if(percent >= maxLen && num != maxNum) {
                return maxLen - 1;
            }
            return Mth.clamp(percent, 0, maxLen);
        }
        return 0;
    }

    public static boolean isInArea(int x, int y, int posX, int posY, int xLen, int yLen){
        return x >= posX && x <= posX + xLen && y >= posY && y <= posY + yLen;
    }

    /**
     * vector from a to b.
     */
    public static Vec3 subHorizontal(Vec3 from, Vec3 to) {
        return new Vec3(to.x - from.x, 0, to.z - from.z);
    }

    public static Vec3 toVec3(BlockPos pos) {
        return new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    public static BlockPos toBlockPos(Vec3 vec) {
        return new BlockPos(vec);
    }


}
