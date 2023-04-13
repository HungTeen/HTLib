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
    public static final ResourceLocation EMPTY_LOCATION = prefix("empty");
    private static final ModIDHelper HELPER = new ModIDHelper(){
        @Override
        public String getModID() {
            return HTLib.MOD_ID;
        }
    };

    private static final ModIDHelper VANILLA_HELPER = new ModIDHelper(){
        @Override
        public String getModID() {
            return "minecraft";
        }
    };

    private static final ModIDHelper FORGE_HELPER = new ModIDHelper(){
        @Override
        public String getModID() {
            return "forge";
        }
    };

    public static ModIDHelper get(){
        return HELPER;
    }

    public static ModIDHelper mc(){
        return VANILLA_HELPER;
    }

    public static ModIDHelper forge(){
        return FORGE_HELPER;
    }

    /* ResourceLocation Related Methods */

    public static ResourceLocation res(String modId, String name) {
        return new ResourceLocation(modId, name);
    }

    public static ResourceLocation mcPrefix(String name) {
        return mc().prefix(name);
    }

    public static ResourceLocation forgePrefix(String name) {
        return forge().prefix(name);
    }

    public static ResourceLocation prefix(String name) {
        return get().prefix(name);
    }

    public static boolean in(ResourceLocation resourceLocation, String modId) {
        return resourceLocation.getNamespace().equals(modId);
    }

    public static boolean in(ResourceLocation resourceLocation) {
        return in(resourceLocation, HTLib.MOD_ID);
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
    public static ResourceLocation itemTexture(ResourceLocation location){
        return itemTexture(location, EMPTY_STRING);
    }

    /**
     * Used in Item Model Gen.
     */
    public static ResourceLocation itemTexture(ResourceLocation location, String suffix){
        return res(location.getNamespace(), "item/" + location.getPath() + suffix);
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
        return res(location.getNamespace(), "block/" + location.getPath() + suffix);
    }

    public static ResourceLocation replace(ResourceLocation location, String oldString, String newString){
        return new ResourceLocation(location.getNamespace(), location.getPath().replace(oldString, newString));
    }

    public static ResourceLocation suffix(ResourceLocation location, String suffix){
        return new ResourceLocation(location.getNamespace(), location.getPath() + "_" + suffix);
    }

    public static ResourceLocation prefix(ResourceLocation location, String prefix){
        return new ResourceLocation(location.getNamespace(), prefix + "_" + location.getPath());
    }

    public static ResourceLocation update(ResourceLocation location, String prefix, String suffix){
        return new ResourceLocation(location.getNamespace(), prefix + "_" + location.getPath() + "_" + suffix);
    }

    public static ResourceLocation replaceAndUpdate(ResourceLocation location, String oldString, String prefix, String suffix){
        return update(replace(location, oldString, EMPTY_STRING), prefix, suffix);
    }

    /* Component Related Methods */

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

    /* Misc Methods */

    /**
     * Support 1 to 255.
     */
    public static String toRomanString(int num){
        if(num > 0 && num <= 255){
            return translation("enchantment.level." + num);
        }
        return "Invalid" + num;
    }

    /**
     * Everything about modId.
     */
    public static abstract class ModIDHelper {

        public abstract String getModID();

        /* ResourceLocation Related */

        public ResourceLocation prefix(String name) {
            return StringHelper.res(getModID(), name);
        }

        public boolean in(ResourceLocation resourceLocation) {
            return StringHelper.in(resourceLocation, getModID());
        }

        /* Texture Location Related */

        /**
         * Already add suffix of extend name.
         */
        public ResourceLocation texture(String path){
            return StringHelper.res(getModID(), "textures/" + path + ".png");
        }

        public ResourceLocation guiTexture(String path){
            return texture("gui/" + path);
        }

        public ResourceLocation containerTexture(String path){
            return guiTexture("container/" + path);
        }

        public ResourceLocation overlayTexture(String path){
            return guiTexture("overlay/" + path);
        }

        public ResourceLocation blockTexture(String path){
            return texture("block/" + path);
        }

        public ResourceLocation itemTexture(String path){
            return texture("item/" + path);
        }

        public ResourceLocation entityTexture(String path){
            return texture("entity/" + path);
        }

        /* Lang Related Methods */

        public static MutableComponent itemLang(String path, Object... args){
            return lang("item", get().getModID(), path, args);
        }

        public static MutableComponent blockLang(String path, Object... args){
            return lang("block", get().getModID(), path, args);
        }

        public static MutableComponent itemGroupLang(String path, Object... args){
            return lang("itemGroup", get().getModID(), path, args);
        }

        public static MutableComponent tooltipLang(String path, Object... args){
            return lang("tooltip", get().getModID(), path, args);
        }

        public static MutableComponent enchantLang(String path, Object... args){
            return lang("enchantment", get().getModID(), path, args);
        }

        public static MutableComponent keyBindLang(String path, Object... args){
            return lang("key", get().getModID(), path, args);
        }

    }

}
