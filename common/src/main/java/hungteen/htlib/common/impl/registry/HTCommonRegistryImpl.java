package hungteen.htlib.common.impl.registry;

import hungteen.htlib.api.registry.HTCommonRegistry;
import net.minecraft.resources.ResourceLocation;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/7/11 9:38
 */
public abstract class HTCommonRegistryImpl<T> extends HTRegistryImpl<T> implements HTCommonRegistry<T> {

    public HTCommonRegistryImpl(ResourceLocation registryName) {
        super(registryName);
    }
}
