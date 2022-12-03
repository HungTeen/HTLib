package hungteen.htlib.util.helper;

import hungteen.htlib.util.Triple;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-06 23:10
 **/
public class ColorHelper {

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
    public static final int YELLOW = 16776960;
    public static final int POTATO = 16777119;
    public static final int LITTLE_YELLOW1 = 16775294;
    public static final int LITTLE_YELLOW2 = 16513446;
    public static final int YELLOW_GREEN = 14614272;
    public static final int GOLD_YELLOW = 16768768;
    public static final int GOLD = 13413376;
    public static final int GOLDEN_POPPY = 14073088;

    /* Green */
    public static final int CREEPER_GREEN = 894731;
    public static final int GREEN = 65280;
    public static final int DARK_GREEN = 39168;
    public static final int PEA_GREEN = 10026878;
    public static final int SQUASH = 14286676;
    public static final int TOXIC_GREEN = 3368448;

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
    public static final int ELECTRIC_BLUE = 8191999;
    public static final int IRIS_BLUE = 44504;

    /* Purple */
    public static final int PURPLE = 12665047;
    public static final int LIGHT_PURPLE = 16711935;

    /* Black */
    public static final int BLACK = 0;
    public static final int BAT_BLACK = 4996656;

    /* White */
    public static final int WHITE = 16777215;
    public static final int BONE = 16448150;

    /* Misc */
    public static final int ZOMBIE_SKIN = 5931634;
    public static final int SILVER_FISH = 7237230;
    public static final int BORDER_GROWING = 4259712;
    public static final int BORDER_SHRINKING = 16724016;
    public static final int BORDER_AQUA = 2138367;
    public static final int METAL_ROOT = toRGB(242, 215, 149);
    public static final int WOOD_ROOT = toRGB(21, 112, 39);
    public static final int WATER_ROOT = toRGB(51, 71, 117);
    public static final int FIRE_ROOT = toRGB(255, 47, 110);
    public static final int EARTH_ROOT = toRGB(161, 106, 22);
    public static final int WIND_ROOT = toRGB(208, 255, 250);
    public static final int ELECTRIC_ROOT = toRGB(195, 166, 255);
    public static final int DRUG_ROOT = toRGB(158, 190, 58);
    public static final int ICE_ROOT = toRGB(69, 213, 228);

    public static int toRGB(int red, int green, int blue){
        return toRGB(Triple.of(red, green, blue));
    }

    public static int toRGB(Triple<Integer, Integer, Integer> triple){
        return (triple.getLeft() << 16) | (triple.getMid() << 8) | triple.getRight();
    }

    public static Triple<Integer, Integer, Integer> getRGB(int rgb){
        return Triple.of((rgb >> 16) & 255, (rgb >> 8) & 255, rgb & 255);
    }

}
