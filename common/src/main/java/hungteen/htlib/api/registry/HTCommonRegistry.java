package hungteen.htlib.api.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/7/11 9:38
 */
public interface HTCommonRegistry<T> extends HTRegistry<T> {

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
        return getValue(ResourceLocation.parse(type));
    }

    /**
     * Get the key of specific dataType. <br>
     * Only use after common set up !!!
     * @param type the query dataType.
     * @return empty if no key is find, otherwise return the key.
     */
    <I extends T> Optional<ResourceLocation> getKey(I type);

    /**
     * Get the Codec of the registry, 获取该注册的Codec。<br>
     * This is one reason why I code the lib, 这是我写这个Lib的原因之一。<br>
     * @return Codec.
     */
    default Codec<T> byNameCodec(){
        return ResourceLocation.CODEC.flatXmap((location) -> {
            return this.getValue(location).map(DataResult::success).orElseGet(() -> {
                return DataResult.error(() -> "Unknown registry key in " + this.getRegistryName() + ": " + location);
            });
        }, (value) -> {
            return this.getKey(value).map(DataResult::success).orElseGet(() -> {
                return DataResult.error(() -> "Unknown registry element in " + this.getRegistryName() + ": " + value);
            });
        });
    }
}
