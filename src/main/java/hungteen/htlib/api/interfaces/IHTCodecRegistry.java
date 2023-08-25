package hungteen.htlib.api.interfaces;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A registry style after vanilla registry, 一种后于原版注册的注册方式。 <br>
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-05 09:45
 **/
public interface IHTCodecRegistry<V> extends IHTRegistry<V>{

    default Codec<Holder<V>> getHolderCodec(Codec<V> directCodec){
        return RegistryFileCodec.create(getRegistryKey(), directCodec);
    }

    default Codec<HolderSet<V>> getListCodec(Codec<V> directCodec){
        return RegistryCodecs.homogeneousList(getRegistryKey(), directCodec);
    }

    default HolderLookup.RegistryLookup<V> lookup(Level level){
        return level.registryAccess().lookupOrThrow(getRegistryKey());
    }

    /**
     * Get all registered entries, 获取所有注册项。
     * @return all kinds of entries registered.
     */
    default List<V> getValues(Level level){
        return customSync() ? getClientValues() : lookup(level).listElements().map(Holder::get).toList();
    }

    /**
     * 获取客户端数据。
     * @return client values.
     */
    default List<V> getClientValues(){
        return List.of();
    }

    /**
     * Get keys of all registered entries, 获取所有注册名。
     * @return all keys of registered entries.
     */
    default Set<ResourceKey<V>> getKeys(Level level){
        return customSync() ? getClientKeys() : lookup(level).listElementIds().collect(Collectors.toSet());
    }

    /**
     * 获取客户端Key。
     * @return client keys.
     */
    default Set<ResourceKey<V>> getClientKeys(){
        return Set.of();
    }

    /**
     * Get holder by key, 根据key获取注册项。
     * @param key the resource key to query.
     * @return throw error if no key is find, otherwise return entry with the given key.
     */
    default Holder.Reference<V> getHolder(Level level, ResourceKey<V> key){
        return lookup(level).getOrThrow(key);
    }

    /**
     * Get holder by key, 根据key获取注册项。
     * @param key the resource key to query.
     * @return empty if no key is find, otherwise return entry with the given key.
     */
    default Optional<Holder.Reference<V>> getOptHolder(Level level, ResourceKey<V> key){
        return lookup(level).get(key);
    }

    /**
     * Get entry by key, 根据key获取注册项。
     * @param key the resource key to query.
     * @return throw error if no key is find, otherwise return entry with the given key.
     */
    default V getValue(Level level, ResourceKey<V> key){
        return getHolder(level, key).get();
    }

    /**
     * Get entry by key, 根据key获取注册项。
     * @param key the resource key to query.
     * @return empty if no key is find, otherwise return entry with the given key.
     */
    default Optional<V> getOptValue(Level level, ResourceKey<V> key){
        return customSync() ? getClientOptValue(key) : getOptHolder(level, key).map(Holder::get);
    }

    /**
     * 根据Key获取客户端数据。
     * @return client data.
     */
    default Optional<V> getClientOptValue(ResourceKey<V> key){
        return Optional.empty();
    }

    /**
     * Get holder set by key, 根据key获取注册项。
     * @param key the resource key to query.
     * @return throw error if no key is find, otherwise return entry with the given key.
     */
    default HolderSet.Named<V> getHolderSet(Level level, TagKey<V> key){
        return lookup(level).getOrThrow(key);
    }

    /**
     * Get holder set by key, 根据key获取注册项。
     * @param key the resource key to query.
     * @return empty if no key is find, otherwise return entry with the given key.
     */
    default Optional<HolderSet.Named<V>> getOptHolderSet(Level level, TagKey<V> key){
        return lookup(level).get(key);
    }

    /**
     * Sync data from server to client if value is present.
     * @return Sync codec.
     */
    default Optional<Codec<V>> getSyncCodec(){
        return Optional.empty();
    }

    /**
     * 是否使用原版方式同步数据。
     * @return true if using default method to sync data.
     */
    boolean defaultSync();

    /**
     * 是否使用HTLib方式同步数据。
     * @return true if using HTLib's method to sync data.
     */
    boolean customSync();

    /**
     * Whether this registry need sync data.
     * @return true if needed.
     */
    default boolean requireSync(){
        return getSyncCodec().isPresent();
    }
}
