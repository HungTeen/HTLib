package hungteen.htlib.api.interfaces;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * HTLib 特殊注册系统的顶层接口。<br>
 *
 * <p>它描述一种"先于/独立于原版常规注册"的注册方式（A registry style before vanilla registry）：
 * 模组通过 {@link hungteen.htlib.common.registry.HTRegistryManager} 创建自有的注册表，
 * 并在 mod 生命周期早期与 Forge 事件挂接。两大实现分支见：</p>
 * <ul>
 *     <li>{@link hungteen.htlib.common.registry.HTCommonRegistry}（含
 *     {@link hungteen.htlib.common.registry.HTSimpleRegistry}）——先于原版注册的通用注册（代码注册）。</li>
 *     <li>{@link hungteen.htlib.common.registry.HTCodecRegistry} 与
 *     {@link IHTCodecRegistry}——后于原版注册的数据包注册（JSON 数据驱动）。</li>
 * </ul>
 *
 * <p><b>生命周期：</b>Mod 构造函数中创建 → {@link #register(IEventBus)} 挂接 mod bus 事件
 * → Forge mod 加载（建表/填表/清缓存）→ 数据包分支随世界加载填充并同步客户端。
 * 详见具体实现类注释。</p>
 *
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-05 09:06
 **/
public interface IHTRegistry<T> {

    /**
     * Register this registry.
     * @param modBus EventBus instance.
     */
    void register(IEventBus modBus);

    /**
     * Create resource key.
     * @param name location name.
     * @return resource key.
     */
    ResourceKey<T> createKey(ResourceLocation name);

    /**
     * 获取帮助类。
     * @return Helper instance.
     */
    IHTResourceHelper<T> helper();

    /**
     * Get the registry name, 获取该注册的注册名。
     * @return registry name.
     */
    ResourceLocation getRegistryName();

    /**
     * Get the registry key, 获取该注册的注册名。
     * @return registry key.
     */
    ResourceKey<Registry<T>> getRegistryKey();

    /**
     * Get the codec for this ResourceKey, 获取该注册的编解码器。
     * @return codec.
     */
    default Codec<ResourceKey<T>> getKeyCodec(){
        return ResourceKey.codec(getRegistryKey());
    }

}
