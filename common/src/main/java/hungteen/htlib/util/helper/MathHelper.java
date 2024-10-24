package hungteen.htlib.util.helper;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-02 16:58
 **/
public interface MathHelper {

    static Vec3 rotate(Vec3 vec, double horizontalDegree, double verticalDegree) {
        final double horizontalRadians = Math.toRadians(horizontalDegree);
        final double verticalRadians = Math.toRadians(verticalDegree);
        final double horizontalLength = Math.sqrt(vec.x() * vec.x() + vec.z() * vec.z());
        // Rotate horizontally.
        final double x = vec.x() * Math.cos(horizontalRadians) - vec.z() * Math.sin(horizontalRadians);
        final double z = vec.z() * Math.cos(horizontalRadians) + vec.x() * Math.sin(horizontalRadians);
        // Rotate vertically.
        final double xz = horizontalLength * Math.cos(verticalRadians) - vec.y() * Math.sin(verticalRadians);
        final double y = vec.y() * Math.cos(verticalRadians) + horizontalLength * Math.sin(verticalRadians);
        return new Vec3(x / horizontalLength * xz, y, z / horizontalLength * xz);
    }

    static double smooth(double from, double to, int tick, int cd){
        return smooth(from, to, cd == 0 ? 1F : tick * 1F / cd);
    }

    static double smooth(double from, double to, double percent){
        return from + (to - from) * percent;
    }

    /**
     * getCodecRegistry expand collide box.
     */
    static AABB getAABB(Vec3 pos, double radius, double height) {
        return new AABB(pos.x() - radius, pos.y() - height, pos.z() - radius, pos.x() + radius, pos.y() + height, pos.z() + radius);
    }

    /**
     * use for render bar.
     */
    static int getBarLen(int num, int maxNum, int maxLen) {
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

    static boolean isInArea(int x, int y, int posX, int posY, int xLen, int yLen){
        return x >= posX && x <= posX + xLen && y >= posY && y <= posY + yLen;
    }

    /**
     * vector from a to b.
     */
    static Vec3 subHorizontal(Vec3 from, Vec3 to) {
        return new Vec3(to.x - from.x, 0, to.z - from.z);
    }

    static Vec3 toVec3(BlockPos pos) {
        return new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    static BlockPos toBlockPos(Vec3 vec) {
        return BlockPos.containing(vec);
    }

    static AABB getBlockAABB(BlockPos pos) {
        return new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
    }
}
