package hungteen.htlib.common.impl.registry;

import hungteen.htlib.api.registry.HTHolder;
import hungteen.htlib.api.registry.PTHolder;
import hungteen.htlib.util.helper.StringHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

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
    public <K extends T> HTHolder<K> register(String name, Supplier<K> supplier) {
        ResourceLocation registryName = StringHelper.res(modId, name);
        K obj = Registry.register(registry, registryName, supplier.get());
        return new HTFabricHolder<>(registryName, obj);
    }

    @Override
    public <K extends T> PTHolder<T> registerForHolder(String name, Supplier<K> supplier) {
        ResourceLocation registryName = StringHelper.res(modId, name);
        return new PTFabricHolder<>(registryName, Registry.registerForHolder(registry, registryName, supplier.get()));
    }

}
