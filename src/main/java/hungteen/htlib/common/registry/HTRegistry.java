package hungteen.htlib.common.registry;

import hungteen.htlib.api.interfaces.IHTRegistry;
import hungteen.htlib.api.interfaces.IHTResourceHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

/**
 * HTLib 特殊注册系统（IHTRegistry 系列）的抽象基类。 <br>
 *
 * <h3>这是什么？</h3>
 * Minecraft/Forge 常规的注册体系（方块、物品、生物、附魔等）都由 Forge 的 {@code RegisterEvent}
 * 统一驱动，且不可在运行时追加。HTLib 的"特殊注册"则是<b>模组自有的独立注册表</b>，
 * 用于注册那些既不是原版常规类别、又需要统一管理/序列化/同步的数据。它分为两大分支：<br>
 * <ul>
 *     <li><b>先于原版注册（通用注册）</b>：{@link HTCommonRegistry} / {@link HTSimpleRegistry}，
 *     底层为一个自定义的 Forge Registry，在 mod 加载阶段（NewRegistryEvent → RegisterEvent）创建并填充，
 *     常用来注册轻量的"类型/选项"对象（如 HTLib 的 raid / wave / spawn / result 类型）。</li>
 *     <li><b>后于原版注册（数据包注册）</b>：{@link HTCodecRegistry}，
 *     底层为一个数据包注册表（DataPack Registry），条目并不是在 Java 里注册，
 *     而是由玩家在 <code>data/&lt;namespace&gt;.json</code> 中定义、随世界加载（数据包重载）被解析，
 *     再同步到客户端。常用来注册内容型的数据（如 raid 的具体配置）。</li>
 * </ul>
 *
 * <h3>作用于哪个生命周期？</h3>
 * <ol>
 *     <li><b>Mod 构造函数</b>：通过 {@link HTRegistryManager} 的工厂方法创建本对象，
 *     此时只生成注册键（{@code registryKey}）与惰性引用（{@link HTRegistryHolder}），尚未真正注册。</li>
 *     <li><b>Mod 构造函数/启动早期</b>：必须显式调用 {@link HTRegistry#register(IEventBus)}，
 *     把各分支对应的事件监听挂到 mod bus 上，进入 Forge 的加载生命周期。
 *     （HTLib 自身在 {@code HTLib#register} 中一次性完成。）</li>
 * </ol>
 * 各分支具体走哪些事件、何时填充条目、何时同步，见对应实现类的注释。
 *
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/25 9:21
 */
public abstract class HTRegistry<T> implements IHTRegistry<T> {

    protected final ResourceKey<Registry<T>> registryKey;
    protected final HTRegistryHolder<T> registryHolder;
    protected final IHTResourceHelper<T> registryHelper;

    HTRegistry(ResourceLocation registryName) {
        this.registryKey = ResourceKey.createRegistryKey(registryName);
        this.registryHolder = new HTRegistryHolder<>(this.registryKey);
        this.registryHelper = HTRegistry.this::getRegistryKey;
    }

    @Override
    public ResourceKey<T> createKey(ResourceLocation name) {
        return ResourceKey.create(getRegistryKey(), name);
    }

    @Override
    public ResourceLocation getRegistryName() {
        return this.registryKey.location();
    }

    @Override
    public ResourceKey<Registry<T>> getRegistryKey() {
        return registryKey;
    }

    @Override
    public IHTResourceHelper<T> helper() {
        return registryHelper;
    }

}
