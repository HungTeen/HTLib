package hungteen.htlib.util;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-06 23:10
 **/
public class ColorUtil {

    /* Red */
    public static final int RED = 16711680;
    public static final int RED_2 = 15007744;
    public static final int DARK_RED = 11013646;
    public static final int PINK = 16747452;
    public static final int PIG_PINK = 15771042;

    /* Orange */
    public static final int ORANGE = 14653696;
    public static final int ORANGE_YELLOW = 16167425;
    public static final int NUT = 16762880;

    /* Yellow */
    public static final int HONNEY_YELLOW = 15582019;
    public static final int YELLOW = 16776960;
    public static final int POTATO = 16777119;
    public static final int LITTLE_YELLOW1 = 16775294;
    public static final int LITTLE_YELLOW2 = 16513446;
    public static final int YELLOW_GREEN = 14614272;
    public static final int GOLD_YELLOW = 16768768;

    /* Green */
    public static final int CREEPER_GREEN = 894731;
    public static final int GREEN = 65280;
    public static final int DARK_GREEN = 39168;
    public static final int PEA_GREEN = 10026878;
    public static final int SQUASH = 14286676;

    /* Aqua */
    public static final int LITTLE_AQUA = 9433559;
    public static final int DARK_AQUA = 28065;

    /* Blue */
    public static final int BLUE = 255;
    public static final int ICE = 55551;
    public static final int BLUE_ICE = 47599;
    public static final int DOLPHIN_BLUE = 13027301;
    public static final int BLUE_WHITE = 15660031;
    public static final int DARK_BLUE = 96;

    /* Purple */
    public static final int PURPLE = 12665047;
    public static final int LIGHT_PURPLE = 16711935;

    /* Black */
    public static final int BLACK = 0;
    public static final int BAT_BLACK = 4996656;

    /* Misc */
    public static final int ZOMBIE_SKIN = 5931634;
    public static final int STONE =10592673;
    public static final int LIT_PINK =16764917;
    public static final int SILVER = 14936296;
    public static final int BONE = 16448150;
    public static final int BRIGHT_TURQUOISE = 58861;
    public static final int BROWN = 9593401;
    public static final int CYAN = 65535;
    public static final int DARK_GRAY = 1973526;
    public static final int DARK_VIOLET = 8519858;
    public static final int DE_YORK = 8699004;
    public static final int DEEP_PINK = 16711794;
    public static final int ELECTRIC_BLUE = 8191999;
    public static final int ELECTRIC_LIME = 13622528;
    public static final int GOLD = 13413376;
    public static final int GOLDEN_POPPY = 14073088;
    public static final int HELIOTROPE = 12732927;
    public static final int IRIS_BLUE = 44504;
    public static final int LAVENDER_BLUSH = 16769787;
    public static final int LIGHT_CORAL = 15698295;
    public static final int MANGO_TANGO = 14509824;
    public static final int MISTY_ROSE = 16771304;
    public static final int OLIVE = 5924864;
    public static final int PIGMENT_GREEN = 37698;
    public static final int TANGERINE_YELLOW = 15257600;
    public static final int TOXIC_GREEN = 3368448;
    public static final int TYRIAN_PURPLE = 7012434;
    public static final int WHITE = 16777215;
    public static final int SILVER_FISH = 7237230;

    public static int toRGB(int r, int g, int b){
        return toRGB(Triple.of(r, g, b));
    }

    public static int toRGB(Triple<Integer, Integer, Integer> triple){
        return triple.getLeft() * 255 * 255 + triple.getMid() * 255 + triple.getRight();
    }

    public static Triple<Integer, Integer, Integer> getRGB(int rgb){
        return Triple.of(rgb / 255 / 255, rgb / 255 % 255, rgb % 255);
    }

}