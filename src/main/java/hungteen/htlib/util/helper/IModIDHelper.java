package hungteen.htlib.util.helper;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

/**
 * Abstract Helper about namespace(mod id).
 * @author PangTeen
 * @program HTLib
 * @data 2023/6/13 14:55
 */
public interface IModIDHelper {

    String MC = "minecraft";
    String FORGE = "forge";

    String getModID();

    /* ResourceLocation Related */

    default ResourceLocation prefix(String name) {
        return StringHelper.res(getModID(), name);
    }

    default boolean in(ResourceLocation resourceLocation) {
        return StringHelper.in(resourceLocation, getModID());
    }

    /* Texture Location Related */

    /**
     * Already add suffix of extend name.
     */
    default ResourceLocation texture(String path){
        return StringHelper.res(getModID(), "textures/" + path + ".png");
    }

    default ResourceLocation guiTexture(String path){
        return texture("gui/" + path);
    }

    default ResourceLocation containerTexture(String path){
        return guiTexture("container/" + path);
    }

    default ResourceLocation overlayTexture(String path){
        return guiTexture("overlay/" + path);
    }

    default ResourceLocation blockTexture(String path){
        return texture("block/" + path);
    }

    default ResourceLocation itemTexture(String path){
        return texture("item/" + path);
    }

    default ResourceLocation entityTexture(String path){
        return texture("entity/" + path);
    }

    /* Lang Related Methods */

    default MutableComponent lang(String group, String lang, Object ... args){
        return StringHelper.translate(langKey(group, lang), args);
    }

    default String langKey(String group, String lang){
        return group + "." + getModID() + "." + lang;
    }

    default MutableComponent itemLang(String path, Object... args){
        return lang("item", path, args);
    }

    default MutableComponent blockLang(String path, Object... args){
        return lang("block", path, args);
    }

    default MutableComponent itemGroupLang(String path, Object... args){
        return lang("itemGroup", path, args);
    }

    default MutableComponent tooltipLang(String path, Object... args){
        return lang("tooltip", path, args);
    }

    default MutableComponent enchantLang(String path, Object... args){
        return lang("enchantment", path, args);
    }

    default MutableComponent keyBindLang(String modId, String path, Object... args){
        return lang("key", path, args);
    }
}
