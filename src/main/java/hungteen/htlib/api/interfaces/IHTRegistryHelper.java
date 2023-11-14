package hungteen.htlib.api.interfaces;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/6/30 9:13
 */
public interface IHTRegistryHelper<T> extends IHTResourceHelper<T>{

    /**
     * 据此来获取Registry层操作.
     */
    Either<IForgeRegistry<T>, Registry<T>> getRegistry();

    @Override
    default ResourceKey<? extends Registry<T>> resourceKey(){
        return getRegistry().map(IForgeRegistry::getRegistryKey, Registry::key);
    }

    /* Packet Related Methods */

    default void write(FriendlyByteBuf buf, T type){
        getRegistry().ifLeft(l -> {
            buf.writeRegistryIdUnsafe(l, type);
        }).ifRight(r -> {
            buf.writeId(r, type);
        });
    }

    default T read(FriendlyByteBuf buf){
        return getRegistry().map(
                buf::readRegistryIdUnsafe,
                buf::readById
        );
    }

    /* Tag Methods */

    default List<T> getTagList(TagKey<T> tagKey){
        return getRegistry().map(
                l -> Objects.requireNonNull(l.tags()).getTag(tagKey).stream().toList(),
                r -> r.getTag(tagKey).map(ImmutableList::copyOf)
                        .map(t -> t.stream().map(Holder::get).collect(Collectors.toList())).orElse(ImmutableList.of())
        );
    }

    /* Common Methods */

    /**
     * Get all registered objects.
     */
    default List<T> values() {
        return getRegistry().map(l -> l.getValues().stream().toList(), r -> r.stream().toList());
    }

    /**
     * Get all registered objects with keys.
     */
    @Deprecated(forRemoval = true, since = "1.1.0")
    default Set<Map.Entry<ResourceKey<T>, T>> getWithKeys() {
        return getRegistry().map(IForgeRegistry::getEntries, Registry::entrySet);
    }

    default Set<ResourceLocation> keys() {
        return getRegistry().map(IForgeRegistry::getKeys, Registry::keySet);
    }

    default Set<Map.Entry<ResourceKey<T>, T>> entries() {
        return getRegistry().map(IForgeRegistry::getEntries, Registry::entrySet);
    }

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
    default Optional<T> get(ResourceLocation location) {
        return Optional.ofNullable(getRegistry().map(l -> l.getValue(location), r -> r.get(location)));
    }

    /**
     * Get key of specific object.
     */
    default ResourceLocation getKey(T object) {
        return getRegistry().map(l -> l.getKey(object), r -> r.getKey(object));
    }

    /**
     * Get key of specific object.
     */
    default Optional<ResourceKey<T>> getResourceKey(T object) {
        return getRegistry().map(l -> l.getResourceKey(object), r -> r.getResourceKey(object));
    }

    default Codec<T> getCodec(){
        return getRegistry().map(IForgeRegistry::getCodec, Registry::byNameCodec);
    }

}
