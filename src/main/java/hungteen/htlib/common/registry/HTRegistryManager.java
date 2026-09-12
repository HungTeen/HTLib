package hungteen.htlib.common.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import hungteen.htlib.HTLib;
import hungteen.htlib.api.interfaces.ISimpleEntry;
import hungteen.htlib.common.network.SyncDatapackPacket;
import hungteen.htlib.util.helper.JavaHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.registries.RegistryBuilder;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * HTLib 特殊注册系统的<b>工厂与调度中心</b>。 <br>
 *
 * <p>模组通过本类创建各种"特殊注册表"，并在此统一处理数据包注册表的
 * 客户端同步。所有 {@link HTCodecRegistry} 都会登记在 {@link #CODEC_REGISTRIES}（名称 → 注册表 的双向表）中。</p>
 *
 * <h3>创建 API（在 mod 构造函数中调用）</h3>
 * <ul>
 *     <li>{@link #createCommon} / {@link #createSimple}：
 *     创建一个"先于原版注册"的 Forge Registry，用于代码注册轻量对象；
 *     Simple 变体要求元素实现 {@link hungteen.htlib.api.interfaces.ISimpleEntry}。</li>
 *     <li>{@link #create(...)} 系列：
 *     创建一个"后于原版注册"的数据包注册表，条目来自数据包 JSON；
 *     通过 {@code syncSup}（同步 codec）、{@code clazz}（数据类）与 {@code requireCache}（是否需要缓存）
 *     控制同步方式（原版同步 / HTLib 自定义同步）。</li>
 * </ul>
 *
 * <h3>作用于哪个生命周期？</h3>
 * <ul>
 *     <li><b>Mod 构造函数</b>：工厂方法在此被调用，产出注册表对象并登记到 {@link #CODEC_REGISTRIES}。</li>
 *     <li><b>Mod 加载阶段</b>：mod 调用 {@code registry.register(modBus)} 挂接 Forge 事件
 *     （见 {@link hungteen.htlib.common.registry.HTCommonRegistry#register} 与
 *     {@link hungteen.htlib.common.registry.HTCodecRegistry#register}），注册表实体此时才建立。</li>
 *     <li><b>世界加载 / 数据包重载</b>：数据包注册表条目被解析。</li>
 *     <li><b>服务器启动 / 玩家加入</b>：{@link #syncToClient} 在 Forge 的
 *     {@link OnDatapackSyncEvent} 上被触发，把数据包条目同步给客户端。
 *     （HTLib 在 {@link HTLib#HTLib()} 中挂接。）</li>
 * </ul>
 *
 * <p><b>注意：</b>不要为同一个注册名创建多个注册表
 * （"do not create more than one registry for specific registry entityType"）。</p>
 *
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-25 21:27
 **/
public class HTRegistryManager {

    private static final BiMap<ResourceLocation, HTCodecRegistry<?>> CODEC_REGISTRIES =
        Maps.synchronizedBiMap(HashBiMap.create());

    /**
     * 在所有数据包注册表的数据包就绪后，把条目同步给客户端。
     *
     * <p>作用于 Forge 的 {@link OnDatapackSyncEvent}（服务器启动数据包加载完成、
     * 以及玩家加入服务器时触发），由 {@link HTLib#HTLib()} 挂接： 无玩家（event.getPlayer() == null，全服广播）时逐人同步，否则只同步给针对于该玩家。</p>
     */
    public static void syncToClient(OnDatapackSyncEvent event) {
        if (event.getPlayer() == null) {
            HTRegistryManager.getRegistries().forEach(registry -> {
                event.getPlayerList().getPlayers().forEach(registry::syncToClient);
            });
        } else {
            HTRegistryManager.getRegistries().forEach(registry -> {
                registry.syncToClient(event.getPlayer());
            });
        }
    }

    /**
     * 创建一个"先于原版注册"的通用注册表（Forge Registry）。
     *
     * <p>条目通过 {@link hungteen.htlib.api.interfaces.IHTCommonRegistry#register}
     * 在 Java 代码中注册，随 Forge 的 mod 加载事件真正填充到注册表中， 之后可直接通过 {@code byNameCodec()} 序列化为 ResourceLocation。</p>
     *
     * <p><b>注意：</b>不要为同一个注册名创建多个注册表。</p>
     */
    public static <T> HTCommonRegistry<T> createCommon(ResourceLocation registryName) {
        return new HTCommonRegistry<>(registryName);
    }

    public static <T> HTCommonRegistry<T> createCommon(ResourceLocation registryName,
        Supplier<RegistryBuilder<T>> builderSup) {
        return new HTCommonRegistry<>(registryName, builderSup);
    }

    /**
     * 创建一个"先于原版注册"的简单注册表。
     *
     * <p>{@link HTSimpleRegistry} 的条目需实现 {@link ISimpleEntry}
     * （自带名字/命名空间/注册名），可直接用对象本身注册，专为轻量"类型/选项" 类对象设计，方便后续用一个循环去注册其它常规注册。</p>
     *
     * <p><b>注意：</b>不要为同一个注册名创建多个注册表。</p>
     */
    public static <T extends ISimpleEntry> HTSimpleRegistry<T> createSimple(ResourceLocation registryName) {
        return new HTSimpleRegistry<>(registryName);
    }

    public static <T extends ISimpleEntry> HTSimpleRegistry<T> createSimple(ResourceLocation registryName,
        Supplier<RegistryBuilder<T>> builderSup) {
        return new HTSimpleRegistry<>(registryName, builderSup);
    }

    /**
     * 创建一个"后于原版注册"的数据包注册表（默认不进行同步）。
     *
     * <p>条目由玩家在数据包 JSON（<code>data/&lt;namespace&gt;/...json</code>）中定义，
     * 随世界加载被 {@code codecSup} 解析，不含同步 codec 时仅服务端可读。</p>
     */
    public static <T> HTCodecRegistry<T> create(ResourceLocation registryName, Supplier<Codec<T>> codecSup) {
        return create(registryName, codecSup, null, null, false);
    }

    /**
     * 创建一个"后于原版注册"的数据包注册表（默认不进行同步）。
     *
     * @param requireCache
     *     服务端是否需要缓存已加载条目的 key 列表（{@link hungteen.htlib.api.interfaces.IHTCodecRegistry#getCachedKeys}）。
     */
    public static <T> HTCodecRegistry<T> create(ResourceLocation registryName, Supplier<Codec<T>> codecSup,
        boolean requireCache) {
        return create(registryName, codecSup, null, null, requireCache);
    }

    /**
     * 创建数据包注册表，采用<b>HTLib 自定义同步</b>。
     *
     * <p>提供 {@code clazz} 后，同步走 {@link SyncDatapackPacket} 自定义数据包，
     * 客户端用同步 codec 反序列化并缓存到内存中（绕过原版同步方式）。</p>
     *
     * @param clazz
     *     数据类（T 的具体运行时类型），有 clazz 才说明采用 HTLib 方式同步而非原版同步。
     */
    public static <T> HTCodecRegistry<T> create(ResourceLocation registryName, Supplier<Codec<T>> codecSup,
        Class<T> clazz) {
        return create(registryName, codecSup, codecSup, clazz, false);
    }

    /**
     * 创建数据包注册表，采用<b>原版默认同步</b>。
     *
     * <p>提供 {@code syncSup}（同步格式，可与磁盘序列化格式不同）后，
     * Forge/原版会将该注册表纳入数据包注册体系，随数据包自然同步给客户端。</p>
     *
     * @param syncSup
     *     用于数据包的同步 codec。
     */
    public static <T> HTCodecRegistry<T> create(ResourceLocation registryName, Supplier<Codec<T>> codecSup,
        Supplier<Codec<T>> syncSup) {
        return create(registryName, codecSup, syncSup, null, false);
    }

    /**
     * 创建数据包注册表的完整控制接口。
     *
     * <p><b>注意：</b>不要为同一个注册名创建多个注册表。</p>
     *
     * @param syncSup
     *     用于数据包的同步 codec；为 null 表示不同步。
     * @param clazz
     *     数据类；有 clazz 表示采用 HTLib 的自定义方式同步数据，否则采用原版同步方法。
     * @param requireCache
     *     是否需要缓存数据（服务端），供客户端等场景查询已加载条目的 key 列表。
     */
    public static <T> HTCodecRegistry<T> create(ResourceLocation registryName, Supplier<Codec<T>> codecSup,
        @Nullable Supplier<Codec<T>> syncSup, Class<T> clazz, boolean requireCache) {
        final HTCodecRegistry<T> codecRegistry =
            new HTCodecRegistry<>(registryName, codecSup, syncSup, clazz, requireCache);
        CODEC_REGISTRIES.put(registryName, codecRegistry);
        return codecRegistry;
    }

    public static Optional<HTCodecRegistry<?>> get(ResourceLocation registryName) {
        return JavaHelper.getOpt(CODEC_REGISTRIES, registryName);
    }

    public static List<HTCodecRegistry<?>> getRegistries() {
        return CODEC_REGISTRIES.values().stream().toList();
    }

}
