package hungteen.htlib.api.interfaces;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * A registry style before vanilla registry, 一种先于原版注册的方式。 <br>
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-05 09:06
 **/
public interface IHTRegistry<T> {

    /**
     * Register this registry.
     * @param modBus EventBus instance.
     */
    void register(IEventBus modBus);

    /**
     * Single register. <br>
     * Note: invoke before register event, 建议在注册事件发生前注册。
     * @param name Register name of this entry.
     * @param type The entry to be registered.
     */
    <I extends T> I register(ResourceLocation name, I type);

    /**
     * Get all registered entries, 获取所有注册项。
     * @return all kinds of entries registered.
     */
    Collection<T> getValues();

    /**
     * Get keys of all registered entries, 获取所有注册名。
     * @return all keys of registered entries.
     */
    @Deprecated(since = "0.9.3")
    default Set<ResourceLocation> getIds(){
        return getKeys();
    }

    /**
     * Get keys of all registered entries, 获取所有注册名。
     * @return all keys of registered entries.
     */
    Set<ResourceLocation> getKeys();

    /**
     * Get key-value pairs of all registered entries, 获取已注册的键值对。
     * @return all key-value pairs of registered entries.
     */
    Set<Map.Entry<ResourceLocation, T>> getEntries();

    /**
     * Get entry by key, 根据key获取注册项。
     * @param type the key to query.
     * @return empty if no key is find, otherwise return entry with the given key.
     */
    Optional<T> getValue(ResourceLocation type);

    /**
     * Get entry by string key, 根据字符串key获取注册项。<br>
     * Note: ensure the string is correct before using it, 请自行确定字符串的格式。
     * @param type the key to query.
     * @return empty if no key is find, otherwise return entry with the given key.
     */
    default Optional<T> getValue(String type){
        return getValue(new ResourceLocation(type));
    }

    /**
     * Get the key of specific type. <br>
     * Only use after common set up !!!
     * @param type the query type.
     * @return empty if no key is find, otherwise return the key.
     */
    <I extends T> Optional<ResourceLocation> getKey(I type);

    /**
     * Create resource key.
     * @param name location name.
     * @return resource key.
     */
    ResourceKey<T> createKey(ResourceLocation name);

    /**
     * Get the Codec of the registry, 获取该注册的Codec。<br>
     * This is one reason why I code the lib, 这是我写这个Lib的原因之一。<br>
     * @return Codec.
     */
    Codec<T> byNameCodec();

    /**
     * 获取帮助类。
     * @return Helper instance.
     */
    IHTResourceHelper<T> helper();

    /**
     * Get the registry name, 获取该注册的注册名。
     * @return registry name.
     */
    ResourceLocation getRegistryName();

    /**
     * Get the registry key, 获取该注册的注册名。
     * @return registry key.
     */
    ResourceKey<Registry<T>> getRegistryKey();

}
