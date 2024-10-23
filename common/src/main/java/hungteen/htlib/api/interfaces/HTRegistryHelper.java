package hungteen.htlib.api.interfaces;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/6/30 9:13
 */
public interface HTRegistryHelper<T> extends HTResourceHelper<T> {

    /* Tag Methods */

    List<T> getTagList(TagKey<T> tagKey);

    /* Common Methods */

    List<T> values();

    Set<ResourceLocation> keys();

    Set<Map.Entry<ResourceKey<T>, T>> entries();

    default List<ResourceKey<T>> filterKeys(Predicate<T> predicate) {
        return filterEntries(predicate).stream().map(Map.Entry::getKey).toList();
    }

    default List<ResourceKey<T>> filterKeys(Predicate<ResourceKey<T>> keyPredicate, Predicate<T> valuePredicate) {
        return filterEntries(keyPredicate, valuePredicate).stream().map(Map.Entry::getKey).toList();
    }

    default List<T> filterValues(Predicate<T> predicate) {
        return filterEntries(predicate).stream().map(Map.Entry::getValue).toList();
    }

    default List<T> filterValues(Predicate<ResourceKey<T>> keyPredicate, Predicate<T> valuePredicate) {
        return filterEntries(keyPredicate, valuePredicate).stream().map(Map.Entry::getValue).toList();
    }

    default List<Map.Entry<ResourceKey<T>, T>> filterEntries(Predicate<T> valuePredicate) {
        return filterEntries(key -> true, valuePredicate);
    }

    default List<Map.Entry<ResourceKey<T>, T>> filterEntries(Predicate<ResourceKey<T>> keyPredicate, Predicate<T> valuePredicate) {
        return entries(keyPredicate, valuePredicate).stream().toList();
    }

    default Set<Map.Entry<ResourceKey<T>, T>> entries(Predicate<ResourceKey<T>> keyPredicate, Predicate<T> valuePredicate) {
        return entries().stream()
                .filter(entry -> keyPredicate.test(entry.getKey()) && valuePredicate.test(entry.getValue()))
                .collect(Collectors.toSet());
    }

    /**
     * Get registered objects by key.
     */
    Optional<T> get(ResourceLocation location);

    /**
     * Get key of specific object.
     */
    ResourceLocation getKey(T object);

    /**
     * Get key of specific object.
     */
    Optional<ResourceKey<T>> getResourceKey(T object);

    Codec<T> getCodec();

    default Codec<TagKey<T>> getTagCodec() {
        return TagKey.codec(resourceKey());
    }

}
