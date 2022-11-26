package hungteen.htlib.common.registry;

import net.minecraft.resources.ResourceLocation;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-25 22:20
 **/
public class HTRegistryHolder<T> {

    private final ResourceLocation location;
    private final T value;

    public HTRegistryHolder(ResourceLocation location, T value) {
        this.location = location;
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public ResourceLocation getLocation() {
        return location;
    }

}
