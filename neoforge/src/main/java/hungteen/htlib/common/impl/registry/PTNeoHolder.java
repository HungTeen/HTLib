package hungteen.htlib.common.impl.registry;

import hungteen.htlib.api.registry.PTHolder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/11/19 21:16
 **/
public class PTNeoHolder<T> implements PTHolder<T> {

    private final ResourceLocation registryName;
    private final Supplier<Holder<T>> supplier;

    public PTNeoHolder(ResourceLocation registryName, Supplier<Holder<T>> supplier) {
        this.registryName = registryName;
        this.supplier = supplier;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return registryName;
    }

    @Override
    public Holder<T> holder() {
        return supplier.get();
    }
}
