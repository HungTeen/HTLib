package hungteen.htlib.common.impl.registry;

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
     * 注册一个条目。
     * @param name 注册名
     * @param supplier 条目提供器
     * @return 返回注册的条目
     */
    <K extends T> Supplier<K> register(String name, Supplier<K> supplier);

    /**
     * 不同平台都有自己的构建方式，需要在此抽象。
     */
    @FunctionalInterface
    interface HTVanillaRegistryFactory {

        <T> HTVanillaRegistry<T> create(ResourceKey<? extends Registry<T>> registryKey, String modId);

    }

}
