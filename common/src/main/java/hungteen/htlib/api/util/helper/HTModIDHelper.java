package hungteen.htlib.api.util.helper;

import hungteen.htlib.util.helper.StringHelper;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Abstract Helper about namespace(mod id).
 * @author PangTeen
 * @program HTLib
 * @data 2023/6/13 14:55
 */
public interface HTModIDHelper {

    String MC = "minecraft";

    String getModID();

    /* ResourceLocation Related */

    default ResourceLocation prefix(String name) {
        return StringHelper.res(getModID(), name);
    }

    default String prefixName(String name) {
        return StringHelper.resString(getModID(), name);
    }

    default boolean in(ResourceLocation resourceLocation) {
        return StringHelper.in(resourceLocation, getModID());
    }

    default <T> boolean isIn(ResourceKey<T> resourceKey) {
        return in(resourceKey.location());
    }

    /* Registry Related */

    default <T> List<ResourceKey<T>> filterKeys(HTRegistryHelper<T> helper, Predicate<T> predicate) {
        return helper.filterKeys(this::isIn, predicate);
    }

    default <T> List<T> filterValues(HTRegistryHelper<T> helper, Predicate<T> predicate) {
        return helper.filterEntries(this::isIn, predicate).stream().map(Map.Entry::getValue).toList();
    }

    default <T> Set<Map.Entry<ResourceKey<T>, T>> filterEntries(HTRegistryHelper<T> helper, Predicate<T> predicate) {
        return helper.entries(this::isIn, predicate);
    }

    /* Texture Location Related */

    /**
     * Already add suffix of extend name.
     */
    default ResourceLocation texture(String path) {
        return StringHelper.res(getModID(), "textures/" + path + ".png");
    }

    default ResourceLocation guiTexture(String path) {
        return texture("gui/" + path);
    }

    default ResourceLocation containerTexture(String path) {
        return guiTexture("container/" + path);
    }

    default ResourceLocation overlayTexture(String path) {
        return guiTexture("overlay/" + path);
    }

    default ResourceLocation blockTexture(String path) {
        return texture("block/" + path);
    }

    default ResourceLocation itemTexture(String path) {
        return texture("item/" + path);
    }

    default ResourceLocation entityTexture(String path) {
        return texture("entity/" + path);
    }

    /* Lang Related Methods */

    default MutableComponent lang(String group, String lang, Object... args) {
        return StringHelper.translate(langKey(group, lang), args);
    }

    default String langKey(String group, String lang) {
        return group + "." + getModID() + "." + lang;
    }

    default MutableComponent itemLang(String path, Object... args) {
        return lang("item", path, args);
    }

    default MutableComponent blockLang(String path, Object... args) {
        return lang("block", path, args);
    }

    default MutableComponent itemGroupLang(String path, Object... args) {
        return lang("itemGroup", path, args);
    }

    default MutableComponent tooltipLang(String path, Object... args) {
        return lang("tooltip", path, args);
    }

    default MutableComponent enchantLang(String path, Object... args) {
        return lang("enchantment", path, args);
    }

    default MutableComponent keyBindLang(String modId, String path, Object... args) {
        return lang("key", path, args);
    }
}
