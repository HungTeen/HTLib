package hungteen.htlib.common.impl.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * Copy from {@link DeferredRegister}.
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-25 22:20
 **/
public class HTNeoRegistryHolder<V> implements Supplier<Registry<V>> {

    private final ResourceKey<? extends Registry<V>> registryKey;
    private Registry<V> registry = null;

    HTNeoRegistryHolder(ResourceKey<? extends Registry<V>> registryKey) {
        this.registryKey = registryKey;
    }

    @Override
    public Registry<V> get() {
        // Keep looking up the registry until it's not null
        if (this.registry == null)
            this.registry = (Registry<V>) BuiltInRegistries.REGISTRY.get(this.registryKey.location());

        return this.registry;
    }
}
