package hungteen.htlib.common.impl.registry;

import hungteen.htlib.util.helper.StringHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/22 22:48
 **/
public class HTFabricVanillaRegistry<T> implements HTVanillaRegistry<T> {

    private final ResourceKey<Registry<T>> registryKey;
    private final Registry<T> registry;
    private final String modId;

    public HTFabricVanillaRegistry(ResourceKey<Registry<T>> registryKey, String modId) {
        this.registryKey = registryKey;
        this.modId = modId;
        this.registry = (Registry<T>) BuiltInRegistries.REGISTRY.get(registryKey.location());
    }

    @Override
    public ResourceKey<? extends Registry<T>> registryKey() {
        return registryKey;
    }

    @Override
    public <K extends T> Supplier<K> register(String name, Supplier<K> supplier) {
        K obj = Registry.register(registry, StringHelper.res(modId, name), supplier.get());
        return () -> obj;
    }

}
