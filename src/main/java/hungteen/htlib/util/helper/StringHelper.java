package hungteen.htlib.util.helper;

import com.mojang.serialization.Codec;
import hungteen.htlib.HTLib;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-07 08:42
 **/
public class StringHelper {

    public static final Codec<MutableComponent> CODEC = Codec.STRING.xmap(Component.Serializer::fromJson, Component.Serializer::toJson);
    public static final String EMPTY_STRING = "";
    public static final ResourceLocation EMPTY_LOCATION = HTLib.prefix("empty");
//    private static final List<String> ROMAN_NUMBERS = Arrays.asList("I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X");

    public static ResourceLocation guiTexture(String modId, String path){
        return texture(modId, "gui/" + path);
    }

    public static ResourceLocation containerTexture(String modId, String path){
        return guiTexture(modId, "container/" + path);
    }

    public static ResourceLocation overlayTexture(String modId, String path){
        return guiTexture(modId, "overlay/" + path);
    }

    public static ResourceLocation entityTexture(String modId, String path){
        return texture(modId, "entity/" + path);
    }

    /**
     * Already add suffix of extend name.
     */
    public static ResourceLocation texture(String modId, String path){
        return HTLib.res(modId, "textures/" + path + ".png");
    }

    /**
     * Used in Item Model Gen.
     */
    public static ResourceLocation itemTexture(ResourceLocation location){
        return itemTexture(location, EMPTY_STRING);
    }

    /**
     * Used in Item Model Gen.
     */
    public static ResourceLocation itemTexture(ResourceLocation location, String suffix){
        return HTLib.res(location.getNamespace(), "item/" + location.getPath() + suffix);
    }

    /**
     * Used in block State Gen.
     */
    public static ResourceLocation blockTexture(ResourceLocation location){
        return blockTexture(location, EMPTY_STRING);
    }

    /**
     * Used in block State Gen.
     */
    public static ResourceLocation blockTexture(ResourceLocation location, String suffix){
        return HTLib.res(location.getNamespace(), "block/" + location.getPath() + suffix);
    }

    public static ResourceLocation replace(ResourceLocation location, String oldString, String newString){
        return new ResourceLocation(location.getNamespace(), location.getPath().replace(oldString, newString));
    }

    public static ResourceLocation suffix(ResourceLocation location, String suffix){
        return new ResourceLocation(location.getNamespace(), location.getPath() + "_" + suffix);
    }

    /**
     * Get translated text.
     */
    public static String translate(String key, Object ... args) {
        return Component.translatable(key, args).getString();
    }

    /**
     * Support 1 to 255.
     */
    public static String toRomanString(int num){
        if(num > 0 && num <= 255){
            return translate("enchantment.level." + num);
        }
        return "Invalid" + num;
    }

}
