package hungteen.htlib.util.algorithm;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2023-01-17 15:59
 **/
public class BitHelper {

    /**
     * set value's specific bit position to flag(0 or 1).
     */
    public static int setBit(int value, int pos, boolean flag) {
        if(flag) return setBitOne(value, pos);
        return setBitZero(value, pos);
    }

    /**
     * has bit 1 at value's position or not.
     */
    public static boolean hasBitOne(int value, int pos) {
        return ((value >> pos) & 1) == 1;
    }

    private static int setBitOne(int value, int pos) {
        return (value | (1 << pos));
    }

    private static int setBitZero(int value, int pos) {
        if(hasBitOne(value, pos)) return value - (1 << pos);
        return value;
    }

}
