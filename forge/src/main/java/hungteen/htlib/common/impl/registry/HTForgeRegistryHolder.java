package hungteen.htlib.common.impl.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

import java.util.function.Supplier;

/**
 * Copy from {@link DeferredRegister}.
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-25 22:20
 **/
public class HTForgeRegistryHolder<V> implements Supplier<IForgeRegistry<V>> {

    private final ResourceKey<? extends Registry<V>> registryKey;
    private IForgeRegistry<V> registry = null;

    HTForgeRegistryHolder(ResourceKey<? extends Registry<V>> registryKey) {
        this.registryKey = registryKey;
    }

    @Override
    public IForgeRegistry<V> get() {
        // Keep looking up the registry until it's not null
        if (this.registry == null)
            this.registry = RegistryManager.ACTIVE.getRegistry(this.registryKey);

        return this.registry;
    }
}
