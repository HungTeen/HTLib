package hungteen.htlib.api.registry;

import net.minecraft.resources.ResourceLocation;

/**
 * 自定义实现的基于原版注册系统的注册类型，通过 SPI 在不同的平台有不同的实现。
 * @author PangTeen
 * @program HTLib
 * @data 2023/6/28 14:32
 */
public interface HTCustomRegistry<T> extends HTCommonRegistry<T> {

    /**
     * Single register. <br>
     * Note: invoke before register event, 建议在注册事件发生前注册。
     * @param name Register name of this entry.
     * @param type The entry to be registered.
     */
    <I extends T> I register(ResourceLocation name, I type);

    /**
     * 不同平台都有自己的构建方式，需要在此抽象。
     */
    @FunctionalInterface
    interface HTCustomRegistryFactory {

        <T> HTCustomRegistry<T> create(Class<T> clazz, ResourceLocation registryName);

    }

}