package hungteen.htlib.util.helper;

import hungteen.htlib.util.HTColor;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.material.MapColor;

import java.util.Optional;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-06 23:10
 **/
public interface ColorHelper {

    int MASK = 255;

    /* Red */

    HTColor RED = create(ChatFormatting.RED);

    HTColor DYE_RED = create(DyeColor.RED.getTextureDiffuseColor());

    HTColor FIREWORK_RED = create(DyeColor.RED.getFireworkColor());

    HTColor TEXT_RED = create(DyeColor.RED.getTextColor());

    HTColor RED_2 = create(15007744);

    HTColor DARK_RED = create(ChatFormatting.DARK_RED);

    HTColor DYE_MAGENTA = create(DyeColor.MAGENTA.getTextureDiffuseColor()); //品红。

    HTColor FIREWORK_MAGENTA = create(DyeColor.MAGENTA.getFireworkColor()); //品红。

    HTColor TEXT_MAGENTA = create(DyeColor.MAGENTA.getTextColor()); //品红。

    HTColor PINK = create(16747452);

    HTColor PIG_PINK = create(15771042);

    HTColor DYE_PINK = create(DyeColor.PINK.getTextureDiffuseColor());

    HTColor FIREWORK_PINK = create(DyeColor.PINK.getFireworkColor());

    HTColor TEXT_PINK = create(DyeColor.PINK.getTextColor());

    /* Orange */

    HTColor ORANGE = create(14653696);

    HTColor DYE_ORANGE = create(DyeColor.ORANGE.getTextureDiffuseColor());

    HTColor FIREWORK_ORANGE = create(DyeColor.ORANGE.getFireworkColor());

    HTColor TEXT_ORANGE = create(DyeColor.ORANGE.getTextColor());

    HTColor ORANGE_YELLOW = create(16167425);

    HTColor NUT = create(16762880);

    /* Yellow */

    HTColor YELLOW = create(ChatFormatting.YELLOW);

    HTColor DYE_YELLOW = create(DyeColor.YELLOW.getTextureDiffuseColor());

    HTColor FIREWORK_YELLOW = create(DyeColor.YELLOW.getFireworkColor());

    HTColor TEXT_YELLOW = create(DyeColor.YELLOW.getTextColor());

    HTColor POTATO = create(16777119);

    HTColor LITTLE_YELLOW1 = create(16775294);

    HTColor LITTLE_YELLOW2 = create(16513446);

    HTColor YELLOW_GREEN = create(14614272);

    HTColor GOLD = create(ChatFormatting.GOLD);

    HTColor GOLDEN_POPPY = create(14073088);

    /* Green */

    HTColor GREEN = create(ChatFormatting.GREEN);

    HTColor DYE_GREEN = create(DyeColor.GREEN.getTextureDiffuseColor());

    HTColor FIREWORK_GREEN = create(DyeColor.GREEN.getFireworkColor());

    HTColor TEXT_GREEN = create(DyeColor.GREEN.getTextColor());

    HTColor CREEPER_GREEN = create(894731);

    HTColor DARK_GREEN = create(ChatFormatting.DARK_GREEN);

    HTColor PEA_GREEN = create(10026878);

    HTColor SQUASH = create(14286676);

    HTColor TOXIC_GREEN = create(3368448);

    HTColor DYE_LIME = create(DyeColor.LIME.getTextureDiffuseColor());

    HTColor FIREWORK_LIME = create(DyeColor.LIME.getFireworkColor());

    HTColor TEXT_LIME = create(DyeColor.LIME.getTextColor());

    /* Aqua */

    HTColor AQUA = create(ChatFormatting.AQUA);

    HTColor LITTLE_AQUA = create(9433559);

    HTColor BORDER_AQUA = create(2138367); // World Border.

    HTColor DARK_AQUA = create(ChatFormatting.DARK_AQUA);

    HTColor DYE_CYAN = create(DyeColor.CYAN.getTextureDiffuseColor());

    HTColor FIREWORK_CYAN = create(DyeColor.CYAN.getFireworkColor());

    HTColor TEXT_CYAN = create(DyeColor.CYAN.getTextColor());

    /* Blue */

    HTColor BLUE = create(ChatFormatting.BLUE);

    HTColor ICE = create(55551);

    HTColor BLUE_ICE = create(47599);

    HTColor DOLPHIN_BLUE = create(13027301);

    HTColor BLUE_WHITE = create(15660031);

    HTColor DYE_BLUE = create(DyeColor.BLUE.getTextureDiffuseColor());

    HTColor FIREWORK_BLUE = create(DyeColor.BLUE.getFireworkColor());

    HTColor TEXT_BLUE = create(DyeColor.BLUE.getTextColor());

    HTColor DYE_LIGHT_BLUE = create(DyeColor.LIGHT_BLUE.getTextureDiffuseColor());
    HTColor FIREWORK_LIGHT_BLUE = create(DyeColor.LIGHT_BLUE.getFireworkColor());
    HTColor TEXT_LIGHT_BLUE = create(DyeColor.LIGHT_BLUE.getTextColor());

    HTColor DARK_BLUE = create(ChatFormatting.DARK_BLUE);

    HTColor ELECTRIC_BLUE = create(8191999);

    HTColor IRIS_BLUE = create(44504);

    /* Purple */

    HTColor PURPLE = create(12665047);

    HTColor LIGHT_PURPLE = create(ChatFormatting.LIGHT_PURPLE);

    HTColor DARK_PURPLE = create(ChatFormatting.DARK_PURPLE);

    HTColor DYE_PURPLE = create(DyeColor.PURPLE.getTextureDiffuseColor());

    HTColor FIREWORK_PURPLE = create(DyeColor.PURPLE.getFireworkColor());

    HTColor TEXT_PURPLE = create(DyeColor.PURPLE.getTextColor());

    /* Black */

    HTColor BLACK = create(ChatFormatting.BLACK);

    HTColor DYE_BLACK = create(DyeColor.BLACK.getTextureDiffuseColor());

    HTColor FIREWORK_BLACK = create(DyeColor.BLACK.getFireworkColor());

    HTColor TEXT_BLACK = create(DyeColor.BLACK.getTextColor());

    HTColor BAT_BLACK = create(4996656);

    /* White */

    HTColor WHITE = create(ChatFormatting.WHITE);

    HTColor DYE_WHITE = create(DyeColor.WHITE.getTextureDiffuseColor());

    HTColor FIREWORK_WHITE = create(DyeColor.WHITE.getFireworkColor());

    HTColor TEXT_WHITE = create(DyeColor.WHITE.getTextColor());

    HTColor BONE = create(16448150);

    /* Gray */

    HTColor GRAY = create(ChatFormatting.GRAY);

    HTColor DARK_GRAY = create(ChatFormatting.DARK_GRAY);

    HTColor DYE_GRAY = create(DyeColor.GRAY.getTextureDiffuseColor());

    HTColor FIREWORK_GRAY = create(DyeColor.GRAY.getFireworkColor());

    HTColor TEXT_GRAY = create(DyeColor.GRAY.getTextColor());

    HTColor DYE_LIGHT_GRAY = create(DyeColor.LIGHT_GRAY.getTextureDiffuseColor());

    HTColor FIREWORK_LIGHT_GRAY = create(DyeColor.LIGHT_GRAY.getFireworkColor());

    HTColor TEXT_LIGHT_GRAY = create(DyeColor.LIGHT_GRAY.getTextColor());

    /* Brown */

    HTColor DYE_BROWN = create(DyeColor.BROWN.getTextureDiffuseColor());

    HTColor FIREWORK_BROWN = create(DyeColor.BROWN.getFireworkColor());

    HTColor TEXT_BROWN = create(DyeColor.BROWN.getTextColor());

    /* Misc */

    int ZOMBIE_SKIN = 5931634;
    int SILVER_FISH = 7237230;
    int BORDER_GROWING = 4259712;
    int BORDER_SHRINKING = 16724016;
    int METAL_ROOT = toRGB(242, 215, 149);
    int WOOD_ROOT = toRGB(21, 112, 39);
    int WATER_ROOT = toRGB(51, 71, 117);
    int FIRE_ROOT = toRGB(255, 47, 110);
    int EARTH_ROOT = toRGB(161, 106, 22);
    int WIND_ROOT = toRGB(208, 255, 250);
    int ELECTRIC_ROOT = toRGB(195, 166, 255);
    int DRUG_ROOT = toRGB(158, 190, 58);
    int ICE_ROOT = toRGB(69, 213, 228);

    static int toRGB(int red, int green, int blue){
        return (red << 16) | (green << 8) | blue;
    }

    static int getRedFromRGB(int rgb){
        return (rgb >> 16) & MASK;
    }

    static int getGreenFromRGB(int rgb){
        return (rgb >> 8) & MASK;
    }

    static int getBlueFromRGB(int rgb){
        return rgb & MASK;
    }

    static float to(int value){
        return value * 1F / MASK;
    }

    static int from(float value){
        return Math.round(value * MASK);
    }

    static HTColor create(float[] values){
        final int[] list = new int[]{0, 0, 0, 0};
        for(int i = 0; i < Math.min(4, values.length); i++){
            list[i] = from(values[i]);
        }
        if(values.length <= 3){
            return create(list[0], list[1], list[2]);
        }
        return create(list[0], list[1], list[2], list[3]);
    }

    static HTColor create(MapColor color, MapColor.Brightness brightness){
        return create(color.calculateRGBColor(brightness));
    }

    private static HTColor create(ChatFormatting formatting){
        return create(Optional.ofNullable(formatting.getColor()).orElse(0));
    }

    static HTColor create(int red, int green, int blue){
        return new HTColor(red, green, blue, 0, false);
    }

    static HTColor create(int red, int green, int blue, int alpha){
        return new HTColor(red, green, blue, alpha, false);
    }

    static HTColor create(int rgb){
        return create(getRedFromRGB(rgb), getGreenFromRGB(rgb), getBlueFromRGB(rgb));
    }

    static HTColor create(int rgb, int alpha){
        return create(getRedFromRGB(rgb), getGreenFromRGB(rgb), getBlueFromRGB(rgb), alpha);
    }

}
