package hungteen.htlib.api.interfaces;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import hungteen.htlib.util.helper.JavaHelper;
import hungteen.htlib.util.helper.StringHelper;
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
     * Get predicate registry objects.
     */
    default List<T> filterValues(Predicate<T> predicate) {
        return getRegistry().map(IForgeRegistry::getValues, r -> r.stream().toList()).stream()
                .filter(predicate)
                .sorted(Comparator.comparing((object) -> Objects.requireNonNullElseGet(getKey(object), () -> StringHelper.EMPTY_LOCATION)))
                .collect(Collectors.toList());
    }

    /**
     * Get predicate registry objects with key.
     */
    default List<Map.Entry<ResourceKey<T>, T>> filterEntries(Predicate<T> predicate) {
        return getRegistry().map(IForgeRegistry::getEntries, Registry::entrySet).stream()
                .filter(entry -> predicate.test(entry.getValue()))
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toList());
    }

    /**
     * Get all registered objects.
     */
    default Collection<T> values() {
        return filterValues(JavaHelper::alwaysTrue);
    }

    /**
     * Get all registered objects with keys.
     */
    default Collection<Map.Entry<ResourceKey<T>, T>> getWithKeys() {
        return filterEntries(JavaHelper::alwaysTrue);
    }

    /**
     * Get all registered objects with keys.
     */
    default Collection<ResourceLocation> keys() {
        return getRegistry().map(IForgeRegistry::getKeys, Registry::keySet);
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
