package hungteen.htlib.api.registry;

import hungteen.htlib.api.interfaces.IHTResourceHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * A registry style before vanilla registry.
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-05 09:06
 **/
public interface HTRegistry<T> {

    /**
     * Register this registry.
     * @param modBus EventBus instance.
     */
    void register(IEventBus modBus);

    /**
     * Create resource key.
     * @param name location name.
     * @return resource key.
     */
    ResourceKey<T> createKey(ResourceLocation name);

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
