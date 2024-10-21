package hungteen.htlib.common.registry;

import hungteen.htlib.api.interfaces.IHTResourceHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/25 9:21
 */
public abstract class HTRegistryImpl<T> implements hungteen.htlib.api.registry.HTRegistry<T> {

    protected final ResourceKey<Registry<T>> registryKey;
    protected final HTRegistryHolder<T> registryHolder;
    protected final IHTResourceHelper<T> registryHelper;

    HTRegistryImpl(ResourceLocation registryName) {
        this.registryKey = ResourceKey.createRegistryKey(registryName);
        this.registryHolder = new HTRegistryHolder<>(this.registryKey);
        this.registryHelper = HTRegistryImpl.this::getRegistryKey;
    }

    @Override
    public ResourceKey<T> createKey(ResourceLocation name) {
        return ResourceKey.create(getRegistryKey(), name);
    }

    @Override
    public ResourceLocation getRegistryName() {
        return this.registryKey.location();
    }

    @Override
    public ResourceKey<Registry<T>> getRegistryKey() {
        return registryKey;
    }

    @Override
    public IHTResourceHelper<T> helper() {
        return registryHelper;
    }

}
