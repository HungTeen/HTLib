package hungteen.htlib.api.registry;

import hungteen.htlib.api.HTLibAPI;
import hungteen.htlib.api.util.helper.HTResourceHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

/**
 * 将多种不同的注册形式统一抽象的注册接口。<br>
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-05 09:06
 **/
public interface HTRegistry<T> {

    /**
     * Do nothing, just make the specific class being loaded.
     */
    default void initialize(){
        HTLibAPI.logger().debug("Initialize registry: {}", getRegistryName());
    }

    /**
     * Create resource key.
     * @param name location name.
     * @return resource key.
     */
    default ResourceKey<T> createKey(ResourceLocation name) {
        return ResourceKey.create(getRegistryKey(), name);
    }

    /**
     * 获取帮助类。
     * @return Helper instance.
     */
    HTResourceHelper<T> helper();

    /**
     * Get the registry name, 获取该注册的注册名。
     * @return registry name.
     */
    default ResourceLocation getRegistryName(){
        return getRegistryKey().location();
    }

    /**
     * Get the registry key, 获取该注册的注册名。
     * @return registry key.
     */
    ResourceKey<Registry<T>> getRegistryKey();

}
