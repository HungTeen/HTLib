package hungteen.htlib.api.interfaces;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A registry style before vanilla registry, 一种先于原版注册的方式。 <br>
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-05 09:06
 **/
public interface IHTSimpleRegistry<T extends ISimpleEntry> {

    /**
     * Single register. <br>
     * Note: invoke before register event, 建议在注册事件发生前注册。
     * @param type the entry to be registered.
     */
    <I extends T> void register(I type);

    /**
     * Multiple register. <br>
     * Note: invoke before register event, 建议在注册事件发生前注册。
     * @param types registry list.
     */
    default <I extends T> void register(List<I> types){
        types.forEach(this::register);
    }

    /**
     * Get all registered entries, 获取所有注册项。
     * @return all kinds of entries registered.
     */
    List<T> getValues();

    /**
     * Get keys of all registered entries, 获取所有注册名。
     * @return all keys of registered entries.
     */
    List<ResourceLocation> getIds();

    /**
     * Get key-value pairs of all registered entries, 获取已注册的键值对。
     * @return all key-value pairs of registered entries.
     */
    List<Map.Entry<ResourceLocation, T>> getEntries();

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
     * Get the key of specific type.
     * @param type the query type.
     * @return empty if no key is find, otherwise return the key.
     */
    <I extends T> Optional<ResourceLocation> getKey(I type);

    /**
     * Get the Codec of the registry, 获取该注册的Codec。<br>
     * This is one reason why I code the lib, 这是我写这个Lib的原因之一。<br>
     * @return Codec.
     */
    Codec<T> byNameCodec();

    /**
     * Get the registry name, 获取该注册的注册名。
     * @return registry name.
     */
    ResourceLocation getRegistryName();

}
