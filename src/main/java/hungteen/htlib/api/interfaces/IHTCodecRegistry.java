package hungteen.htlib.api.interfaces;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A registry style after vanilla registry, 一种后于原版注册的注册方式。 <br>
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-05 09:45
 **/
public interface IHTCodecRegistry<V> {

    /**
     * Get all registered entries, 获取所有注册项。
     * @return all kinds of entries registered.
     */
    List<V> getValues();

    /**
     * Get keys of all registered entries, 获取所有注册名。
     * @return all keys of registered entries.
     */
    List<ResourceLocation> getIds();

    /**
     * Get key-value pairs of all registered entries, 获取已注册的键值对。
     * @return all key-value pairs of registered entries.
     */
    List<Map.Entry<ResourceLocation, V>> getEntries();

    /**
     * Get entry by key, 根据key获取注册项。
     * @param type the key to query.
     * @return empty if no key is find, otherwise return entry with the given key.
     */
    Optional<V> getValue(ResourceLocation type);

    /**
     * Get entry by string key, 根据字符串key获取注册项。<br>
     * Note: ensure the string is correct before using it, 请自行确定字符串的格式。
     * @param type the key to query.
     * @return empty if no key is find, otherwise return entry with the given key.
     */
    default Optional<V> getValue(String type){
        return getValue(new ResourceLocation(type));
    }

    /**
     * Get the key of specific type.
     * @param type the query type.
     * @return empty if no key is find, otherwise return the key.
     */
    Optional<ResourceLocation> getKey(V type);

    /**
     * Get the Codec of the registry, 获取该注册的Codec。<br>
     * This is one reason why I code the lib, 这是我写这个Lib的原因之一。<br>
     * @return Codec.
     */
    Codec<V> getCodec();

    /**
     * Get the registry name, 获取该注册的注册名。
     * @return registry name.
     */
    String getRegistryName();
}
