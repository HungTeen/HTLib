package hungteen.htlib.api;

import hungteen.htlib.api.registry.HTCodecRegistry;
import hungteen.htlib.api.registry.HTCustomRegistry;
import hungteen.htlib.common.impl.registry.HTVanillaRegistry;

/**
 * 一个用来兼容不同平台方法的接口，在不同的平台有不同的实现。
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/22 8:56
 */
public interface HTLibPlatformAPI {

    HTLibPlatformAPI INSTANCE = ServiceUtil.findService(HTLibPlatformAPI.class);

    /**
     * Obtain the Mod API, either a valid implementation if mod is present, else
     * a dummy instance instead if mod is absent.
     */
    static HTLibPlatformAPI get() {
        return INSTANCE;
    }

    /**
     * 不同平台根据自己的特性创建独有的 Codec 工厂。
     */
    HTCodecRegistry.HTCodecRegistryFactory createCodecRegistryFactory();

    /**
     * 不同平台根据自己的特性创建独有的 Custom 工厂。
     */
    HTCustomRegistry.HTCustomRegistryFactory createCustomRegistryFactory();

    /**
     * 不同平台根据自己的特性创建独有的 Vanilla 工厂。
     */
    HTVanillaRegistry.HTVanillaRegistryFactory createVanillaRegistryFactory();

}
