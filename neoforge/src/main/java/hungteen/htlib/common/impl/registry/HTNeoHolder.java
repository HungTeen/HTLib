package hungteen.htlib.common.impl.registry;

import hungteen.htlib.api.registry.HTHolder;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/11/3 22:11
 **/
public class HTNeoHolder<T, K extends T> implements HTHolder<K> {

    private final DeferredHolder<T, K> holder;

    public HTNeoHolder(DeferredHolder<T, K> holder) {
        this.holder = holder;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return holder.getId();
    }

    @Override
    public K get() {
        return holder.value();
    }
}
