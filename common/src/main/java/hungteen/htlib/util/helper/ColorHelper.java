package hungteen.htlib.util.helper;

import hungteen.htlib.util.records.HTColor;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.material.MapColor;

import java.util.Optional;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-06 23:10
 **/
public class ColorHelper {

    private static final int MASK = 255;

    /* Red */

    public static final HTColor RED = create(ChatFormatting.RED);

    public static final HTColor DYE_RED = create(DyeColor.RED.getTextureDiffuseColors());

    public static final HTColor FIREWORK_RED = create(DyeColor.RED.getFireworkColor());

    public static final HTColor TEXT_RED = create(DyeColor.RED.getTextColor());

    public static final HTColor RED_2 = create(15007744);

    public static final HTColor DARK_RED = create(ChatFormatting.DARK_RED);

    public static final HTColor DYE_MAGENTA = create(DyeColor.MAGENTA.getTextureDiffuseColors()); //品红。

    public static final HTColor FIREWORK_MAGENTA = create(DyeColor.MAGENTA.getFireworkColor()); //品红。

    public static final HTColor TEXT_MAGENTA = create(DyeColor.MAGENTA.getTextColor()); //品红。

    public static final HTColor PINK = create(16747452);

    public static final HTColor PIG_PINK = create(15771042);

    public static final HTColor DYE_PINK = create(DyeColor.PINK.getTextureDiffuseColors());

    public static final HTColor FIREWORK_PINK = create(DyeColor.PINK.getFireworkColor());

    public static final HTColor TEXT_PINK = create(DyeColor.PINK.getTextColor());

    /* Orange */

    public static final HTColor ORANGE = create(14653696);

    public static final HTColor DYE_ORANGE = create(DyeColor.ORANGE.getTextureDiffuseColors());

    public static final HTColor FIREWORK_ORANGE = create(DyeColor.ORANGE.getFireworkColor());

    public static final HTColor TEXT_ORANGE = create(DyeColor.ORANGE.getTextColor());

    public static final HTColor ORANGE_YELLOW = create(16167425);

    public static final HTColor NUT = create(16762880);

    /* Yellow */

    public static final HTColor YELLOW = create(ChatFormatting.YELLOW);

    public static final HTColor DYE_YELLOW = create(DyeColor.YELLOW.getTextureDiffuseColors());

    public static final HTColor FIREWORK_YELLOW = create(DyeColor.YELLOW.getFireworkColor());

    public static final HTColor TEXT_YELLOW = create(DyeColor.YELLOW.getTextColor());

    public static final HTColor POTATO = create(16777119);

    public static final HTColor LITTLE_YELLOW1 = create(16775294);

    public static final HTColor LITTLE_YELLOW2 = create(16513446);

    public static final HTColor YELLOW_GREEN = create(14614272);

    public static final HTColor GOLD = create(ChatFormatting.GOLD);

    public static final HTColor GOLDEN_POPPY = create(14073088);

    /* Green */

    public static final HTColor GREEN = create(ChatFormatting.GREEN);

    public static final HTColor DYE_GREEN = create(DyeColor.GREEN.getTextureDiffuseColors());

    public static final HTColor FIREWORK_GREEN = create(DyeColor.GREEN.getFireworkColor());

    public static final HTColor TEXT_GREEN = create(DyeColor.GREEN.getTextColor());

    public static final HTColor CREEPER_GREEN = create(894731);

    public static final HTColor DARK_GREEN = create(ChatFormatting.DARK_GREEN);

    public static final HTColor PEA_GREEN = create(10026878);

    public static final HTColor SQUASH = create(14286676);

    public static final HTColor TOXIC_GREEN = create(3368448);

    public static final HTColor DYE_LIME = create(DyeColor.LIME.getTextureDiffuseColors());

    public static final HTColor FIREWORK_LIME = create(DyeColor.LIME.getFireworkColor());

    public static final HTColor TEXT_LIME = create(DyeColor.LIME.getTextColor());

    /* Aqua */

    public static final HTColor AQUA = create(ChatFormatting.AQUA);

    public static final HTColor LITTLE_AQUA = create(9433559);

    public static final HTColor BORDER_AQUA = create(2138367); // World Border.

    public static final HTColor DARK_AQUA = create(ChatFormatting.DARK_AQUA);

    public static final HTColor DYE_CYAN = create(DyeColor.CYAN.getTextureDiffuseColors());

    public static final HTColor FIREWORK_CYAN = create(DyeColor.CYAN.getFireworkColor());

    public static final HTColor TEXT_CYAN = create(DyeColor.CYAN.getTextColor());

    /* Blue */

    public static final HTColor BLUE = create(ChatFormatting.BLUE);

    public static final HTColor ICE = create(55551);

    public static final HTColor BLUE_ICE = create(47599);

    public static final HTColor DOLPHIN_BLUE = create(13027301);

    public static final HTColor BLUE_WHITE = create(15660031);

    public static final HTColor DYE_BLUE = create(DyeColor.BLUE.getTextureDiffuseColors());

    public static final HTColor FIREWORK_BLUE = create(DyeColor.BLUE.getFireworkColor());

    public static final HTColor TEXT_BLUE = create(DyeColor.BLUE.getTextColor());

    public static final HTColor DYE_LIGHT_BLUE = create(DyeColor.LIGHT_BLUE.getTextureDiffuseColors());
    public static final HTColor FIREWORK_LIGHT_BLUE = create(DyeColor.LIGHT_BLUE.getFireworkColor());
    public static final HTColor TEXT_LIGHT_BLUE = create(DyeColor.LIGHT_BLUE.getTextColor());

    public static final HTColor DARK_BLUE = create(ChatFormatting.DARK_BLUE);

    public static final HTColor ELECTRIC_BLUE = create(8191999);

    public static final HTColor IRIS_BLUE = create(44504);

    /* Purple */

    public static final HTColor PURPLE = create(12665047);

    public static final HTColor LIGHT_PURPLE = create(ChatFormatting.LIGHT_PURPLE);

    public static final HTColor DARK_PURPLE = create(ChatFormatting.DARK_PURPLE);

    public static final HTColor DYE_PURPLE = create(DyeColor.PURPLE.getTextureDiffuseColors());

    public static final HTColor FIREWORK_PURPLE = create(DyeColor.PURPLE.getFireworkColor());

    public static final HTColor TEXT_PURPLE = create(DyeColor.PURPLE.getTextColor());

    /* Black */

    public static final HTColor BLACK = create(ChatFormatting.BLACK);

    public static final HTColor DYE_BLACK = create(DyeColor.BLACK.getTextureDiffuseColors());

    public static final HTColor FIREWORK_BLACK = create(DyeColor.BLACK.getFireworkColor());

    public static final HTColor TEXT_BLACK = create(DyeColor.BLACK.getTextColor());

    public static final HTColor BAT_BLACK = create(4996656);

    /* White */

    public static final HTColor WHITE = create(ChatFormatting.WHITE);

    public static final HTColor DYE_WHITE = create(DyeColor.WHITE.getTextureDiffuseColors());

    public static final HTColor FIREWORK_WHITE = create(DyeColor.WHITE.getFireworkColor());

    public static final HTColor TEXT_WHITE = create(DyeColor.WHITE.getTextColor());

    public static final HTColor BONE = create(16448150);

    /* Gray */

    public static final HTColor GRAY = create(ChatFormatting.GRAY);

    public static final HTColor DARK_GRAY = create(ChatFormatting.DARK_GRAY);

    public static final HTColor DYE_GRAY = create(DyeColor.GRAY.getTextureDiffuseColors());

    public static final HTColor FIREWORK_GRAY = create(DyeColor.GRAY.getFireworkColor());

    public static final HTColor TEXT_GRAY = create(DyeColor.GRAY.getTextColor());

    public static final HTColor DYE_LIGHT_GRAY = create(DyeColor.LIGHT_GRAY.getTextureDiffuseColors());

    public static final HTColor FIREWORK_LIGHT_GRAY = create(DyeColor.LIGHT_GRAY.getFireworkColor());

    public static final HTColor TEXT_LIGHT_GRAY = create(DyeColor.LIGHT_GRAY.getTextColor());

    /* Brown */

    public static final HTColor DYE_BROWN = create(DyeColor.BROWN.getTextureDiffuseColors());

    public static final HTColor FIREWORK_BROWN = create(DyeColor.BROWN.getFireworkColor());

    public static final HTColor TEXT_BROWN = create(DyeColor.BROWN.getTextColor());

    /* Misc */

    public static final int ZOMBIE_SKIN = 5931634;
    public static final int SILVER_FISH = 7237230;
    public static final int BORDER_GROWING = 4259712;
    public static final int BORDER_SHRINKING = 16724016;
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
        return (red << 16) | (green << 8) | blue;
    }

    public static int getRedFromRGB(int rgb){
        return (rgb >> 16) & MASK;
    }

    public static int getGreenFromRGB(int rgb){
        return (rgb >> 8) & MASK;
    }

    public static int getBlueFromRGB(int rgb){
        return rgb & MASK;
    }

    public static float to(int value){
        return value * 1F / MASK;
    }

    public static int from(float value){
        return Math.round(value * MASK);
    }

    public static HTColor create(float[] values){
        final int[] list = new int[]{0, 0, 0, 0};
        for(int i = 0; i < Math.min(4, values.length); i++){
            list[i] = from(values[i]);
        }
        if(values.length <= 3){
            return create(list[0], list[1], list[2]);
        }
        return create(list[0], list[1], list[2], list[3]);
    }

    public static HTColor create(MapColor color, MapColor.Brightness brightness){
        return create(color.calculateRGBColor(brightness));
    }

    private static HTColor create(ChatFormatting formatting){
        return create(Optional.ofNullable(formatting.getColor()).orElse(0));
    }

    public static HTColor create(int red, int green, int blue){
        return new HTColor(red, green, blue, 0, false);
    }

    public static HTColor create(int red, int green, int blue, int alpha){
        return new HTColor(red, green, blue, alpha, false);
    }

    public static HTColor create(int rgb){
        return create(getRedFromRGB(rgb), getGreenFromRGB(rgb), getBlueFromRGB(rgb));
    }

    public static HTColor create(int rgb, int alpha){
        return create(getRedFromRGB(rgb), getGreenFromRGB(rgb), getBlueFromRGB(rgb), alpha);
    }

}
