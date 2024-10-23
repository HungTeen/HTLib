package hungteen.htlib.api.interfaces;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/6/30 9:13
 */
public interface HTVanillaRegistryHelper<T> extends HTRegistryHelper<T> {

    /**
     * 据此来获取Registry层操作.
     */
    Registry<T> getRegistry();

    @Override
    default ResourceKey<? extends Registry<T>> resourceKey() {
        return getRegistry().key();
    }

    /* Tag Methods */

    @Override
    default List<T> getTagList(TagKey<T> tagKey) {
        return getRegistry().getTag(tagKey).map(ImmutableList::copyOf)
                .map(t -> t.stream().map(Holder::value).collect(Collectors.toList())).orElse(ImmutableList.of());
    }

    /* Common Methods */

    /**
     * Get all registered objects.
     */
    @Override
    default List<T> values() {
        return getRegistry().stream().toList();
    }

    @Override
    default Set<ResourceLocation> keys() {
        return getRegistry().keySet();
    }

    @Override
    default Set<Map.Entry<ResourceKey<T>, T>> entries() {
        return getRegistry().entrySet();
    }

    /**
     * Get registered objects by key.
     */
    @Override
    default Optional<T> get(ResourceLocation location) {
        return Optional.ofNullable(getRegistry().get(location));
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
    default Codec<T> getCodec() {
        return getRegistry().byNameCodec();
    }

}
