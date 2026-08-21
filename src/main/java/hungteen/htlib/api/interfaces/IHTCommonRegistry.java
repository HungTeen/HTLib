package hungteen.htlib.api.interfaces;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * "先于原版注册"的通用注册表接口（{@link hungteen.htlib.common.registry.HTCommonRegistry}
 * 的 API 定义）。 <br>
 *
 * <p>底层为自定义的 Forge Registry，条目在 mod 加载阶段（NewRegistryEvent → RegisterEvent）
 * 由代码批量注册，注册后可通过 {@link #getValues}/{@link #getValue}/{@link #getKey}
 * 读取，并借助 {@link #byNameCodec()} 与 ResourceLocation / 字符串互相转换。
 * 常用来批量注册"重置级"依赖的轻量对象，再用一个循环驱动方块、物品、实体的常规注册。</p>
 *
 * @author PangTeen
 * @program HTLib
 * @data 2023/7/11 9:38
 */
public interface IHTCommonRegistry<T> extends IHTRegistry<T>{

    /**
     * Single register. <br>
     * Note: invoke before register event, 建议在注册事件发生前注册。
     * @param name Register name of this entry.
     * @param type The entry to be registered.
     */
    <I extends T> I register(ResourceLocation name, I type);

    /**
     * Get forge registry.
     * @return register.
     */
    IForgeRegistry<T> getRegistry();

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
     * Get the Codec of the registry, 获取该注册的Codec。<br>
     * This is one reason why I code the lib, 这是我写这个Lib的原因之一。<br>
     * @return Codec.
     */
    Codec<T> byNameCodec();
}
