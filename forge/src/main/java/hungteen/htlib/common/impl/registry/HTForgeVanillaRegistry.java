package hungteen.htlib.common.impl.registry;

import hungteen.htlib.api.registry.HTHolder;
import hungteen.htlib.api.registry.PTHolder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/22 22:48
 **/
public class HTForgeVanillaRegistry<T> implements HTVanillaRegistry<T> {

    private final DeferredRegister<T> deferredRegister;

    public HTForgeVanillaRegistry(ResourceKey<? extends Registry<T>> registryKey, String modId) {
        deferredRegister = DeferredRegister.create(registryKey, modId);
    }

    @Override
    public ResourceKey<? extends Registry<T>> registryKey() {
        return deferredRegister.getRegistryKey();
    }

    @Override
    public <K extends T> HTHolder<K> register(String name, Supplier<K> supplier) {
        return new HTForgeHolder<>(deferredRegister.register(name, supplier));
    }

    @Override
    public <K extends T> PTHolder<T> registerForHolder(String name, Supplier<K> supplier) {
        return new PTForgeHolder<>(deferredRegister.register(name, supplier));
    }

    public void register(IEventBus eventBus) {
        deferredRegister.register(eventBus);
    }

    @Override
    public void initialize() {
        throw new RuntimeException("Do not use this method to initialize registry. Please use ForgeHelper instead.");
    }
}
