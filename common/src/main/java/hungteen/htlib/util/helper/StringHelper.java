package hungteen.htlib.util.helper;

import hungteen.htlib.util.HTColor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-07 08:42
 **/
public interface StringHelper {

    /* ResourceLocation Related Methods */

    /**
     * @return A resource location in the mod.
     */
    static ResourceLocation res(String modId, String name) {
        return ResourceLocation.fromNamespaceAndPath(modId, name);
    }

    /**
     * @return A resource location in the mod.
     */
    static String resString(String modId, String name) {
        return res(modId, name).toString();
    }

    /**
     * @return Whether the resource location is in the mod.
     */
    static boolean in(ResourceLocation location, String modId) {
        return location.getNamespace().equals(modId);
    }

    /**
     * @return Whether the resource key is in the mod.
     */
    static <T> boolean in(ResourceKey<T> resourceKey, String modId) {
        return in(resourceKey.location(), modId);
    }

    /**
     * 替换字符串。
     * @param location origin location.
     * @param oldString to be replaced.
     * @param newString to replace with.
     * @return new location.
     */
    static ResourceLocation replace(ResourceLocation location, String oldString, String newString){
        return res(location.getNamespace(), location.getPath().replace(oldString, newString));
    }

    /**
     * Add suffix to location.
     * @param location origin location.
     * @param suffix to be added.
     * @return new location, such as "minecraft:stone" -> "minecraft:stone_suffix".
     */
    static ResourceLocation suffix(ResourceLocation location, String suffix){
        return location.withSuffix("_" + suffix);
    }

    /**
     * Add prefix to location.
     * @param location origin location.
     * @param prefix to be added.
     * @return new location, such as "minecraft:stone" -> "minecraft:prefix_stone".
     */
    static ResourceLocation prefix(ResourceLocation location, String prefix){
        return location.withPrefix(prefix + "_");
    }

    /**
     * Add prefix and suffix to location.
     * @param location origin location.
     * @param prefix to be added.
     * @param suffix to be added.
     * @return new location, such as "minecraft:stone" -> "minecraft:prefix_stone_suffix".
     */
    static ResourceLocation expand(ResourceLocation location, String prefix, String suffix){
        return expand(location, prefix, suffix, "_");
    }

    /**
     * {@link #expand(ResourceLocation, String, String)} 的泛化版本。
     */
    static ResourceLocation expand(ResourceLocation location, String prefix, String suffix, String split){
        return res(location.getNamespace(), prefix + split + location.getPath() + split + suffix);
    }

    /**
     * @return A combination of {@link #expand(ResourceLocation, String, String)} and {@link #replace(ResourceLocation, String, String)}.
     */
    static ResourceLocation expandAndReplace(ResourceLocation location, String oldString, String prefix, String suffix){
        return expand(replace(location, oldString, ""), prefix, suffix);
    }

    /* Texture Location Related Methods */

    /**
     * Already add suffix of extend name.
     */
    static ResourceLocation texture(String modId, String path){
        return res(modId, "textures/" + path + ".png");
    }

    static ResourceLocation guiTexture(String modId, String path){
        return texture(modId, "gui/" + path);
    }

    static ResourceLocation containerTexture(String modId, String path){
        return guiTexture(modId, "container/" + path);
    }

    static ResourceLocation overlayTexture(String modId, String path){
        return guiTexture(modId, "overlay/" + path);
    }

    static ResourceLocation entityTexture(String modId, String path){
        return texture(modId, "entity/" + path);
    }

    /* Data Generator Related Methods */

    /**
     * Used in Item Model Gen.
     */
    static ResourceLocation itemTexture(ResourceLocation location, String suffix){
        return expand(location, "item/", suffix, "");
    }

    /**
     * Used in Item Model Gen.
     */
    static ResourceLocation itemTexture(ResourceLocation location){
        return itemTexture(location, "");
    }

    /**
     * Used in block State Gen.
     */
    static ResourceLocation blockTexture(ResourceLocation location, String suffix){
        return expand(location, "block/", suffix, "");
    }

    /**
     * Used in block State Gen.
     */
    static ResourceLocation blockTexture(ResourceLocation location){
        return blockTexture(location, "");
    }

    /* Lang Related Methods */

    /**
     * Get translated text.
     */
    static String translation(String key, Object... args) {
        return translate(key, args).getString();
    }

    static MutableComponent translate(String key, Object... args) {
        return Component.translatable(key, args);
    }

    static MutableComponent lang(String group, String modId, String lang, Object... args){
        return translate(langKey(group, modId, lang), args);
    }

    static String langKey(String group, String modId, String lang){
        return group + "." + modId + "." + lang;
    }

    static MutableComponent itemLang(String modId, String path, Object... args){
        return lang("item", modId, path, args);
    }

    static MutableComponent blockLang(String modId, String path, Object... args){
        return lang("block", modId, path, args);
    }

    static MutableComponent itemGroupLang(String modId, String path, Object... args){
        return lang("itemGroup", modId, path, args);
    }

    static MutableComponent tooltipLang(String modId, String path, Object... args){
        return lang("tooltip", modId, path, args);
    }

    static MutableComponent enchantLang(String modId, String path, Object... args){
        return lang("enchantment", modId, path, args);
    }

    static MutableComponent keyBindLang(String modId, String path, Object... args){
        return lang("key", modId, path, args);
    }

    static Style style(){
        return Style.EMPTY;
    }

    static Style colorStyle(HTColor color){
        return style().withColor(color.rgb());
    }

    /* Misc Methods */

    static FormattedCharSequence format(String text){
        return format(Component.literal(text));
    }

    static FormattedCharSequence format(Component text){
        return text.getVisualOrderText();
    }

    /**
     * Support 1 to 255.
     */
    static String toRomanString(int num){
        if(num > 0 && num <= 255){
            return translation("enchantment.level." + num);
        }
        return "Invalid" + num;
    }

}
