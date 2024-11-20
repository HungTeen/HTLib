package hungteen.htlib.api.registry;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/11/3 21:45
 **/
public interface PTHolder<T> {

    /**
     * @return 获取注册名。
     */
    ResourceLocation getRegistryName();

    /**
     * Forge 里面的 getId 方法。
     */
    default ResourceLocation getId(){
        return getRegistryName();
    }

    Holder<T> holder();

}
