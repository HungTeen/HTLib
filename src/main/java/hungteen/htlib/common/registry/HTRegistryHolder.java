package hungteen.htlib.common.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

import java.util.function.Supplier;

/**
 * 对 {@link IForgeRegistry} 的惰性引用，Copy from {@link DeferredRegister}。 <br>
 *
 * <p>Forge 注册表要在 NewRegistryEvent 阶段创建完成后才存在，因此在 mod 构造函数
 * 早期无法直接拿到。本类把"获取注册表"推迟到首次调用 {@link #get()}：
 * 此时从 {@link RegistryManager#ACTIVE}（Forge 已创建全部注册表）按注册键查询并缓存，
 * 之后直接复用。生命周期上对应 <b>NewRegistryEvent 之后、任何一次实际读取时</b>。</p>
 *
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-25 22:20
 **/
public class HTRegistryHolder<V> implements Supplier<IForgeRegistry<V>> {

    private final ResourceKey<? extends Registry<V>> registryKey;
    private IForgeRegistry<V> registry = null;

    HTRegistryHolder(ResourceKey<? extends Registry<V>> registryKey) {
        this.registryKey = registryKey;
    }

    @Override
    public IForgeRegistry<V> get() {
        // Keep looking up the registry until it's not null
        if (this.registry == null)
            this.registry = RegistryManager.ACTIVE.getRegistry(this.registryKey);

        return this.registry;
    }
}
