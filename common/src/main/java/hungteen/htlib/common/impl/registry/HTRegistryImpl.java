package hungteen.htlib.common.impl.registry;

import hungteen.htlib.api.util.helper.HTResourceHelper;
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
//    protected final HTRegistryHolder<T> registryHolder;
    protected final HTResourceHelper<T> registryHelper;


    public HTRegistryImpl(ResourceLocation registryName) {
        this.registryKey = ResourceKey.createRegistryKey(registryName);
//        this.registryHolder = new HTRegistryHolder<>(this.registryKey);
        this.registryHelper = HTRegistryImpl.this::getRegistryKey;
    }

    @Override
    public ResourceKey<Registry<T>> getRegistryKey() {
        return registryKey;
    }

    @Override
    public HTResourceHelper<T> helper() {
        return registryHelper;
    }

}
