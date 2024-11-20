package hungteen.htlib.common.impl.registry;

import hungteen.htlib.api.registry.PTHolder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/11/19 21:13
 **/
public class PTFabricHolder<T> implements PTHolder<T> {

    private final ResourceLocation registryName;
    private final Holder<T> holder;

    public PTFabricHolder(ResourceLocation registryName, Holder<T> holder) {
        this.registryName = registryName;
        this.holder = holder;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return registryName;
    }

    @Override
    public Holder<T> holder() {
        return holder;
    }
}
