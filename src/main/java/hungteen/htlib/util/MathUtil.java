package hungteen.htlib.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-02 16:58
 **/
public class MathUtil {

    /**
     * use for render bar.
     */
    public static int getBarLen(int num, int maxNum, int maxLen) {
        final int percent = num * maxLen / maxNum;
        if(percent == 0 && num != 0) {
            return 1;
        } else if(percent == maxLen && num != maxNum) {
            return maxLen - 1;
        }
        return percent;
    }

    /**
     * gen random from min to max.
     */
    public static int getRandomMinMax(Random rand, int min, int max) {
        return rand.nextInt(max - min + 1) + min;
    }

    /**
     * get random from - range to range.
     */
    public static int getRandomInRange(Random rand, int range) {
        return rand.nextInt(range << 1 | 1) - range;
    }

    /**
     * get random from - 1 to 1.
     */
    public static float getRandomFloat(Random rand) {
        return (rand.nextFloat() - 0.5F) * 2;
    }

    /**
     * get random Vec3.
     */
    public static Vec3 getRandomVec3(Random rand, double scale) {
        final double x = getRandomFloat(rand) * scale;
        final double y = getRandomFloat(rand) * scale;
        final double z = getRandomFloat(rand) * scale;
        return new Vec3(x, y, z);
    }

    /**
     * it ignores y position.
     */
    public static BlockPos getRandomRangePos(Random rand, int range) {
        return getRandomRangePos(rand, 0, range);
    }

    /**
     * it ignores y position.
     */
    public static BlockPos getRandomRangePos(Random rand, int minR, int maxR) {
        final int x = (rand.nextInt(2) == 0 ? -1 : 1) * getRandomMinMax(rand, minR, maxR);
        final int z = (rand.nextInt(2) == 0 ? -1 : 1) * getRandomMinMax(rand, minR, maxR);
        return new BlockPos(x, 0, z);
    }

    public static boolean randDouble(Random rand, double value) {
        return rand.nextDouble() < value;
    }

    public static boolean isInArea(int x, int y, int posX, int posY, int xLen, int yLen){
        return x >= posX && x <= posX + xLen && y >= posY && y <= posY + yLen;
    }

    /**
     * vector from a to b.
     */
    public static Vec3 getHorizontalVec(Vec3 from, Vec3 to) {
        return new Vec3(to.x - from.x, 0, to.z - from.z);
    }

    public static Vec3 toVector(BlockPos pos) {
        return new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
    }


}
