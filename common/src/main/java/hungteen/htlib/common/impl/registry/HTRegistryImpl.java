package hungteen.htlib.common.impl.registry;

import hungteen.htlib.api.registry.HTRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/25 9:21
 */
public abstract class HTRegistryImpl<T> implements HTRegistry<T> {

    protected final ResourceKey<Registry<T>> registryKey;

    public HTRegistryImpl(ResourceLocation registryName) {
        this.registryKey = ResourceKey.createRegistryKey(registryName);
    }

    @Override
    public ResourceKey<Registry<T>> getRegistryKey() {
        return registryKey;
    }


}
