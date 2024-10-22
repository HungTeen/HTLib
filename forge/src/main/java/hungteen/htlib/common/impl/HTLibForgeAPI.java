package hungteen.htlib.common.impl;

import hungteen.htlib.api.HTLibPlatformAPI;
import hungteen.htlib.api.registry.HTCodecRegistry;
import hungteen.htlib.api.registry.HTCustomRegistry;
import hungteen.htlib.common.impl.registry.*;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/22 15:13
 **/
public class HTLibForgeAPI implements HTLibPlatformAPI {

    @Override
    public HTCodecRegistry.HTCodecRegistryFactory createCodecRegistryFactory() {
        return HTCodecRegistryImpl::new;
    }

    @Override
    public HTCustomRegistry.HTCustomRegistryFactory createCustomRegistryFactory() {
        return HTForgeCustomRegistry::new;
    }

    @Override
    public HTVanillaRegistry.HTVanillaRegistryFactory createVanillaRegistryFactory() {
        return HTForgeVanillaRegistry::new;
    }

}
