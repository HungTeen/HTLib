package hungteen.htlib.common.impl.registry;

import hungteen.htlib.api.registry.HTHolder;
import net.minecraft.resources.ResourceLocation;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/11/3 21:47
 **/
public class HTFabricHolder<T> implements HTHolder<T> {

    private final ResourceLocation registryKey;
    private final T obj;

    public HTFabricHolder(ResourceLocation registryKey, T obj) {
        this.registryKey = registryKey;
        this.obj = obj;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return registryKey;
    }

    @Override
    public T get() {
        return obj;
    }
}
