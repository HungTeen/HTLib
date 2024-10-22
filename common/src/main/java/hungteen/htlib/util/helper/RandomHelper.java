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
     * getCodecRegistry 50% chance.
     */
    public static boolean half(RandomSource rand) {
        return chance(rand, 0.5F);
    }

    /**
     * getCodecRegistry boolean value with chance.
     */
    public static boolean chance(RandomSource rand, float chance) {
        return rand.nextFloat() < chance;
    }

    /**
     * gen 1 or -1.
     */
    public static int getSide(RandomSource rand) {
        return half(rand) ? 1 : -1;
    }

    /**
     * gen int value from [min, max].
     */
    public static int getMinMax(RandomSource rand, int min, int max) {
        return rand.nextInt(max - min + 1) + min;
    }

    /**
     * gen double value from [min, max].
     */
    public static double getMinMax(RandomSource rand, double min, double max) {
        return rand.nextDouble() * (max - min) + min;
    }

    /**
     * gen int value from [-range, range].
     */
    public static int range(RandomSource rand, int range) {
        return rand.nextInt(range << 1 | 1) - range;
    }

    /**
     * gen float value from [-1, 1].
     */
    public static float floatRange(RandomSource rand) {
        return floatRange(rand, 1);
    }

    /**
     * gen float value from [-range, range].
     */
    public static float floatRange(RandomSource rand, float range) {
        return (rand.nextFloat() - 0.5F) * 2 * range;
    }

    /**
     * gen double value from [-range, range].
     */
    public static double doubleRange(RandomSource rand, double range) {
        return (rand.nextDouble() - 0.5D) * 2 * range;
    }

    /**
     * getCodecRegistry vector3 in all direction with scale range.
     */
    public static Vec3 vec3Range(RandomSource rand, double range) {
        return new Vec3(doubleRange(rand, range), doubleRange(rand, range), doubleRange(rand, range));
    }

    /**
     * square range area, it ignores y getPosition.
     */
    public static BlockPos squareArea(RandomSource rand, double range) {
        return squareArea(rand, 0, range);
    }

    /**
     * square range area, it ignores y getPosition.
     */
    public static BlockPos squareArea(RandomSource rand, double minRadius, double maxRadius) {
        return MathHelper.toBlockPos(squareAreaVec(rand, minRadius, maxRadius));
    }

    /**
     * square range area, it ignores y getPosition.
     */
    public static Vec3 squareAreaVec(RandomSource rand, double minRadius, double maxRadius) {
        final double x = getSide(rand) * getMinMax(rand, minRadius, maxRadius);
        final double z = getSide(rand) * getMinMax(rand, minRadius, maxRadius);
        return new Vec3(x, 0, z);
    }

    /**
     * circle range area, it ignores y getPosition.
     */
    public static BlockPos circleArea(RandomSource rand, double range) {
        return circleArea(rand, 0, range);
    }

    /**
     * circle range area, it ignores y getPosition.
     */
    public static BlockPos circleArea(RandomSource rand, double minRadius, double maxRadius) {
        return MathHelper.toBlockPos(circleAreaVec(rand, minRadius, maxRadius));
    }

    /**
     * circle range area, it ignores y getPosition.
     */
    public static Vec3 circleAreaVec(RandomSource rand, double minRadius, double maxRadius) {
        final double radius = getMinMax(rand, minRadius, maxRadius);
        final double delta = rand.nextDouble() * Math.PI;
        final int x = (int) (radius * Math.sin(delta));
        final int z = (int) (radius * Math.cos(delta));
        return new Vec3(x, 0, z);
    }

}
