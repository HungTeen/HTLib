package hungteen.htlib.common.impl.registry;

import hungteen.htlib.api.registry.HTHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegistryObject;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/11/3 22:00
 **/
public class HTForgeHolder<T> implements HTHolder<T> {

    private final RegistryObject<T> object;

    public HTForgeHolder(RegistryObject<T> object) {
        this.object = object;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return object.getId();
    }

    @Override
    public T get() {
        return object.get();
    }
}
