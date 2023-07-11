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

import java.util.Collection;
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
    default Collection<V> getValues(Level level){
        return lookup(level).listElements().map(Holder::get).toList();
    }

    /**
     * Get keys of all registered entries, 获取所有注册名。
     * @return all keys of registered entries.
     */
    default Set<ResourceKey<V>> getKeys(Level level){
        return lookup(level).listElementIds().collect(Collectors.toSet());
    }

    /**
     * Get entry by key, 根据key获取注册项。
     *
     * @param key the resource key to query.
     * @return empty if no key is find, otherwise return entry with the given key.
     */
    default Holder.Reference<V> getHolder(Level level, ResourceKey<V> key){
        return lookup(level).getOrThrow(key);
    }

    /**
     * Get entry by key, 根据key获取注册项。
     * @param key the resource key to query.
     * @return empty if no key is find, otherwise return entry with the given key.
     */
    default V getValue(Level level, ResourceKey<V> key){
        return getHolder(level, key).get();
    }

    /**
     * Get entry by key, 根据key获取注册项。
     * @param key the resource key to query.
     * @return empty if no key is find, otherwise return entry with the given key.
     */
    default HolderSet.Named<V> getHolderSet(Level level, TagKey<V> key){
        return lookup(level).getOrThrow(key);
    }

}
