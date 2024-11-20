package hungteen.htlib.common.impl.registry;

import hungteen.htlib.api.registry.PTHolder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegistryObject;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/11/19 21:14
 **/
public class PTForgeHolder<T> implements PTHolder<T> {

    private final RegistryObject<T> object;

    public PTForgeHolder(RegistryObject<T> object) {
        this.object = object;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return object.getId();
    }

    @Override
    public Holder<T> holder() {
        return object.getHolder().orElseThrow(() -> new RuntimeException("Holder is not present."));
    }
}
