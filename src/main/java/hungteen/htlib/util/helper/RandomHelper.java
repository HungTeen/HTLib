package hungteen.htlib.util.helper;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-08 22:03
 **/
public class RandomHelper {

    /**
     * gen RandomSource from min to max.
     */
    public static int getMinMax(RandomSource rand, int min, int max) {
        return rand.nextInt(max - min + 1) + min;
    }

    /**
     * get RandomSource from - range to range.
     */
    public static int range(RandomSource rand, int range) {
        return rand.nextInt(range << 1 | 1) - range;
    }

    /**
     * get RandomSource from - 1 to 1.
     */
    public static float floatRange(RandomSource rand) {
        return floatRange(rand, 1);
    }

    /**
     * get RandomSource from - range to range.
     */
    public static float floatRange(RandomSource rand, float range) {
        return (rand.nextFloat() - 0.5F) * 2 * range;
    }

    /**
     * get RandomSource from - range to range.
     */
    public static double doubleRange(RandomSource rand, double range) {
        return (rand.nextDouble() - 0.5D) * 2 * range;
    }

    /**
     * get RandomSource Vec3.
     */
    public static Vec3 vec3Range(RandomSource rand, double range) {
        return new Vec3(doubleRange(rand, range), doubleRange(rand, range), doubleRange(rand, range));
    }

    /**
     * it ignores y position.
     */
    public static BlockPos blockPosRange(RandomSource rand, int range) {
        return blockPosRange(rand, 0, range);
    }

    /**
     * it ignores y position.
     */
    public static BlockPos blockPosRange(RandomSource rand, int minRadius, int maxRadius) {
        final int x = (rand.nextInt(2) == 0 ? -1 : 1) * getMinMax(rand, minRadius, maxRadius);
        final int z = (rand.nextInt(2) == 0 ? -1 : 1) * getMinMax(rand, minRadius, maxRadius);
        return new BlockPos(x, 0, z);
    }

}
