package hungteen.htlib.api.registry;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * 暂时不好优雅的解决 getHolder，因为要引入新的泛型，导致代码变丑。<br>
 * 对于不需要显式指明类型的注册项，且需要经常访问 Holder，可以使用 {@link PTHolder} 代替。
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/11/3 21:45
 **/
public interface HTHolder<T> extends Supplier<T> {

    /**
     * 用来通过 Registry 获取注册的 Holder，{@link net.minecraft.core.Registry#getHolder(ResourceLocation)}。
     * @return 获取注册名。
     */
    ResourceLocation getRegistryName();

    /**
     * Forge 里面的 getId 方法。
     */
    default ResourceLocation getId(){
        return getRegistryName();
    }

}
