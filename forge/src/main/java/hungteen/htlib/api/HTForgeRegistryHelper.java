package hungteen.htlib.api;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.util.helper.HTRegistryHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.*;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/23 22:56
 **/
public interface HTForgeRegistryHelper<T> extends HTRegistryHelper<T> {

    /**
     * 据此来获取Registry层操作.
     */
    IForgeRegistry<T> getRegistry();

    @Override
    default ResourceKey<? extends Registry<T>> resourceKey(){
        return getRegistry().getRegistryKey();
    }

    /* Tag Methods */

    @Override
    default List<T> getTagList(TagKey<T> tagKey){
        return Objects.requireNonNull(getRegistry().tags()).getTag(tagKey).stream().toList();

    }

    /* Common Methods */

    /**
     * Get all registered objects.
     */
    @Override
    default List<T> values() {
        return getRegistry().getValues().stream().toList();
    }

    @Override
    default Set<ResourceLocation> keys() {
        return getRegistry().getKeys();
    }

    @Override
    default Set<Map.Entry<ResourceKey<T>, T>> entries() {
        return getRegistry().getEntries();
    }

    /**
     * Get registered objects by key.
     */
    @Override
    default Optional<T> get(ResourceLocation location) {
        return Optional.ofNullable(getRegistry().getValue(location));
    }

    /**
     * Get key of specific object.
     */
    @Override
    default ResourceLocation getKey(T object) {
        return getRegistry().getKey(object);
    }

    /**
     * Get key of specific object.
     */
    @Override
    default Optional<ResourceKey<T>> getResourceKey(T object) {
        return getRegistry().getResourceKey(object);
    }

    @Override
    default Codec<T> getCodec(){
        return getRegistry().getCodec();
    }

}
