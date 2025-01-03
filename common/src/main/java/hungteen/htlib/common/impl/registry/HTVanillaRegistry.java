package hungteen.htlib.common.impl.registry;

import hungteen.htlib.api.HTLibAPI;
import hungteen.htlib.api.registry.HTHolder;
import hungteen.htlib.api.registry.PTHolder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/22 22:44
 **/
public interface HTVanillaRegistry<T> {

    /**
     * Do nothing, just make the specific class being loaded.
     */
    default void initialize(){
        HTLibAPI.logger().debug("Initialize vanilla registry {}", registryKey().location());
    }

    ResourceKey<? extends Registry<T>> registryKey();

    /**
     * 注册一个条目。
     * @param name 注册名
     * @param supplier 条目提供器
     * @return 返回注册的条目
     */
    <K extends T> HTHolder<K> register(String name, Supplier<K> supplier);

    <K extends T> PTHolder<T> registerForHolder(String name, Supplier<K> supplier);

    /**
     * 不同平台都有自己的构建方式，需要在此抽象。
     */
    @FunctionalInterface
    interface HTVanillaRegistryFactory {

        <T> HTVanillaRegistry<T> create(ResourceKey<Registry<T>> registryKey, String modId);

    }

}
