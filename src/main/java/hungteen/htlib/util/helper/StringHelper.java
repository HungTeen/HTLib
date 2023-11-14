package hungteen.htlib.util.helper;

import hungteen.htlib.util.records.HTColor;
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
public class StringHelper {

    public static final String EMPTY_STRING = "";
    public static final ResourceLocation EMPTY_LOCATION = new ResourceLocation("empty");

    @Deprecated(forRemoval = true, since = "1.0.0")
    public static IModIDHelper get(){
        return HTLibHelper.get();
    }

    @Deprecated(forRemoval = true, since = "1.0.0")
    public static IModIDHelper mc(){
        return VanillaHelper.get();
    }

    @Deprecated(forRemoval = true, since = "1.0.0")
    public static IModIDHelper forge(){
        return ForgeHelper.get();
    }

    /* ResourceLocation Related Methods */

    public static ResourceLocation res(String modId, String name) {
        return new ResourceLocation(modId, name);
    }

    @Deprecated(forRemoval = true, since = "1.0.0")
    public static ResourceLocation mcPrefix(String name) {
        return mc().prefix(name);
    }

    @Deprecated(forRemoval = true, since = "1.0.0")
    public static ResourceLocation forgePrefix(String name) {
        return forge().prefix(name);
    }

    @Deprecated(forRemoval = true, since = "1.0.0")
    public static ResourceLocation prefix(String name) {
        return get().prefix(name);
    }

    public static boolean in(ResourceLocation location, String modId) {
        return location.getNamespace().equals(modId);
    }

    public static <T> boolean in(ResourceKey<T> resourceKey, String modId) {
        return in(resourceKey.location(), modId);
    }

    @Deprecated(forRemoval = true, since = "1.0.0")
    public static boolean in(ResourceLocation location) {
        return in(location, get().getModID());
    }

    /**
     * 替换字符串。
     * @param location origin location.
     * @param oldString to be replaced.
     * @param newString to replace with.
     * @return new location.
     */
    public static ResourceLocation replace(ResourceLocation location, String oldString, String newString){
        return res(location.getNamespace(), location.getPath().replace(oldString, newString));
    }

    public static ResourceLocation suffix(ResourceLocation location, String suffix){
        return location.withSuffix("_" + suffix);
    }

    public static ResourceLocation prefix(ResourceLocation location, String prefix){
        return location.withPrefix(prefix + "_");
    }

    public static ResourceLocation expand(ResourceLocation location, String prefix, String suffix){
        return expand(location, prefix, suffix, "_");
    }

    public static ResourceLocation expand(ResourceLocation location, String prefix, String suffix, String split){
        return res(location.getNamespace(), prefix + split + location.getPath() + split + suffix);
    }

    public static ResourceLocation expandAndReplace(ResourceLocation location, String oldString, String prefix, String suffix){
        return expand(replace(location, oldString, EMPTY_STRING), prefix, suffix);
    }

    /* Texture Location Related Methods */

    /**
     * Already add suffix of extend name.
     */
    public static ResourceLocation texture(String modId, String path){
        return res(modId, "textures/" + path + ".png");
    }

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

    /* Data Generator Related Methods */

    /**
     * Used in Item Model Gen.
     */
    public static ResourceLocation itemTexture(ResourceLocation location, String suffix){
        return expand(location, "item/", suffix, "");
    }

    /**
     * Used in Item Model Gen.
     */
    public static ResourceLocation itemTexture(ResourceLocation location){
        return itemTexture(location, EMPTY_STRING);
    }

    /**
     * Used in block State Gen.
     */
    public static ResourceLocation blockTexture(ResourceLocation location, String suffix){
        return expand(location, "block/", suffix, "");
    }

    /**
     * Used in block State Gen.
     */
    public static ResourceLocation blockTexture(ResourceLocation location){
        return blockTexture(location, EMPTY_STRING);
    }

    /* Lang Related Methods */

    /**
     * Get translated text.
     */
    public static String translation(String key, Object ... args) {
        return translate(key, args).getString();
    }

    public static MutableComponent translate(String key, Object ... args) {
        return Component.translatable(key, args);
    }

    public static MutableComponent lang(String group, String modId, String lang, Object ... args){
        return translate(langKey(group, modId, lang), args);
    }

    public static String langKey(String group, String modId, String lang){
        return group + "." + modId + "." + lang;
    }

    public static MutableComponent itemLang(String modId, String path, Object... args){
        return lang("item", modId, path, args);
    }

    public static MutableComponent blockLang(String modId, String path, Object... args){
        return lang("block", modId, path, args);
    }

    public static MutableComponent itemGroupLang(String modId, String path, Object... args){
        return lang("itemGroup", modId, path, args);
    }

    public static MutableComponent tooltipLang(String modId, String path, Object... args){
        return lang("tooltip", modId, path, args);
    }

    public static MutableComponent enchantLang(String modId, String path, Object... args){
        return lang("enchantment", modId, path, args);
    }

    public static MutableComponent keyBindLang(String modId, String path, Object... args){
        return lang("key", modId, path, args);
    }

    public static Style style(){
        return Style.EMPTY;
    }

    public static Style colorStyle(HTColor color){
        return style().withColor(color.rgb());
    }

    /* Misc Methods */

    public static FormattedCharSequence format(String text){
        return format(Component.literal(text));
    }

    public static FormattedCharSequence format(Component text){
        return text.getVisualOrderText();
    }

    /**
     * Support 1 to 255.
     */
    public static String toRomanString(int num){
        if(num > 0 && num <= 255){
            return translation("enchantment.level." + num);
        }
        return "Invalid" + num;
    }

}
