package hungteen.htlib.common.impl.registry;

import hungteen.htlib.api.registry.HTHolder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/22 22:48
 **/
public class HTNeoVanillaRegistry<T> implements HTVanillaRegistry<T> {

    private final DeferredRegister<T> deferredRegister;

    public HTNeoVanillaRegistry(ResourceKey<? extends Registry<T>> registryKey, String modId) {
        deferredRegister = DeferredRegister.create(registryKey, modId);
    }

    @Override
    public ResourceKey<? extends Registry<T>> registryKey() {
        return deferredRegister.getRegistryKey();
    }

    @Override
    public <K extends T> HTHolder<K> register(String name, Supplier<K> supplier) {
        return new HTNeoHolder<>(deferredRegister.register(name, supplier));
    }

    public void register(IEventBus eventBus) {
        deferredRegister.register(eventBus);
    }

}
