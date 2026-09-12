package hungteen.htlib.common.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import hungteen.htlib.HTLib;
import hungteen.htlib.api.interfaces.IHTCodecRegistry;
import hungteen.htlib.common.network.NetworkHandler;
import hungteen.htlib.common.network.SyncDatapackPacket;
import hungteen.htlib.util.helper.CodecHelper;
import hungteen.htlib.util.helper.JavaHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DataPackRegistryEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * HTLib 特殊注册系统的"后于原版注册"分支（数据包注册 / DataPack Registry 实现）。 <br>
 *
 * <h3>这是什么？</h3>
 * <p>与 {@link HTCommonRegistry}（代码注册、加载期填充）不同，本类的条目
 * <b>不在 Java 里注册</b>，而是由玩家在数据包 JSON
 * （<code>data/&lt;namespace&gt;/.../&lt;条目名&gt;.json</code>）中定义， 每个注册名对应一个文件，随世界加载 / 数据包重载被 {@link #codecSup} 解析成对象。
 * 适用于内容型、可数据驱动配置的数据（如 HTLib 的 raid / wave / spawn / result 组件 与 raid_item）。</p>
 *
 * <h3>作用于哪个生命周期？</h3>
 * <ol>
 *     <li><b>Mod 构造函数</b>：由 {@link HTRegistryManager#create(...)} 创建并登记到管理器。</li>
 *     <li><b>{@link net.minecraftforge.registries.DataPackRegistryEvent.NewRegistry}</b>（mod 加载阶段）：
 *     {@link #addRegistry} 把注册键与其序列化 codec 交给原版数据包注册体系；之后无论服务端
 *     还是客户端，条目都只在<b>世界加载（数据包重载）</b>时才出现。</li>
 *     <li><b>服务器启动 / 玩家加入（OnDatapackSyncEvent）</b>：{@link #syncToClient} 把条目同步给客户端，
 *     触发 {@link #requireCache()} 时还会把已加载的 key 列表缓存到 {@link #cacheIds}，
 *     供后续服务端随时按名查询。</li>
 * </ol>
 *
 * <h3>两种同步方式</h3>
 * <ul>
 *     <li><b>原版默认同步（{@link #defaultSync()}）</b>：未提供 {@code clazz} 且提供了 syncSup，
 *     直接用 {@code event.dataPackRegistry(key, codec, syncSup)}，条目作为可下载的数据包随原版机制同步。</li>
 *     <li><b>HTLib 自定义同步（{@link #customSync()}）</b>：提供了 {@code clazz} 且提供了 syncSup，
 *     因为 <code>Codec&lt;Holder&lt;V&gt;&gt;</code> 形式不适合原版同步方法，故由本类用
 *     {@link SyncDatapackPacket} 逐条目发给客户端，客户端在 {@link #syncMap} 中缓存反序列化结果，
 *     通过 {@link #getClientValues()} / {@link #getClientOptValue} 读取。</li>
 * </ul>
 *
 * @author PangTeen
 * @program HTLib
 * @data 2023/6/28 11:10
 */
public class HTCodecRegistry<V> extends HTRegistry<V> implements IHTCodecRegistry<V> {

    private final BiMap<ResourceKey<V>, V> syncMap = HashBiMap.create();
    private List<ResourceLocation> cacheIds = Lists.newArrayList();
    private final Class<V> registryClass;
    private final Supplier<Codec<V>> codecSup;
    private final Supplier<Codec<V>> syncSup;
    private final boolean requireCache;

    /**
     * @param registryName
     *     注册名，决定了数据的路径。
     * @param codecSup
     *     序列化格式。
     * @param syncSup
     *     同步格式。
     * @param registryClass
     *     数据类。
     */
    HTCodecRegistry(ResourceLocation registryName, Supplier<Codec<V>> codecSup, @Nullable Supplier<Codec<V>> syncSup,
        @Nullable Class<V> registryClass, boolean requireCache) {
        super(registryName);
        this.codecSup = codecSup;
        this.syncSup = syncSup;
        this.registryClass = registryClass;
        this.requireCache = requireCache;
    }

    @Override
    public void register(IEventBus modBus) {
        modBus.addListener(this::addRegistry);
    }

    public void syncToClient(ServerPlayer player) {
        if (this.customSync()) {
            this.getKeys(player.level()).forEach(key -> {
                this.getOptValue(player.level(), key).flatMap(value -> CodecHelper.encodeNbt(this.syncSup.get(), value)
                    .resultOrPartial(msg -> HTLib.getLogger().warn("HTCodecRegistry : " + msg))).ifPresent(tag -> {
                    if (tag instanceof CompoundTag nbt) {
                        NetworkHandler.sendToClient(player,
                            new SyncDatapackPacket(this.getRegistryName(), key.location(), nbt));
                    }
                });
            });
        }
        if (requireCache()) {
            this.cacheIds = this.getKeys(player.level()).stream().map(ResourceKey::location).toList();
        }
    }

    public void syncRegister(ResourceLocation name, Object value) {
        final ResourceKey<V> key = ResourceKey.create(this.getRegistryKey(), name);
        if (this.customSync() && this.getRegistryClass().isPresent()) {
            if (syncMap.containsKey(key)) {
                HTLib.getLogger().warn("HTCodecRegistry {} already registered {}", this.getRegistryName(), name);
            } else if (!this.getRegistryClass().get().isInstance(value)) {
                HTLib.getLogger()
                    .warn("HTCodecRegistry {} can not cast {} to correct entityType", this.getRegistryName(), name);
            }
            syncMap.put(key, this.getRegistryClass().get().cast(value));
        }
    }

    /**
     * Codec<Holder<T>> 不适用于原版的同步方法，故自己绕过。
     */
    private void addRegistry(DataPackRegistryEvent.NewRegistry event) {
        if (this.defaultSync()) {
            event.dataPackRegistry(this.getRegistryKey(), this.codecSup.get(), this.syncSup.get());
        } else {
            event.dataPackRegistry(this.getRegistryKey(), this.codecSup.get());
        }
    }

    /**
     * 获取数据包序列化格式的 codec。
     */
    public Codec<V> getCodec() {
        return this.codecSup.get();
    }

    @Override
    public Optional<Codec<V>> getSyncCodec() {
        return this.syncSup == null ? Optional.empty() : Optional.ofNullable(this.syncSup.get());
    }

    @Override
    public boolean customSync() {
        return this.requireSync() && this.getRegistryClass().isPresent();
    }

    @Override
    public boolean defaultSync() {
        return this.requireSync() && this.getRegistryClass().isEmpty();
    }

    @Override
    public boolean requireCache() {
        return requireCache;
    }

    @Override
    public List<V> getClientValues() {
        return this.syncMap.values().stream().toList();
    }

    @Override
    public Set<ResourceKey<V>> getClientKeys() {
        return this.syncMap.keySet();
    }

    @Override
    public List<ResourceLocation> getCachedKeys() {
        return this.cacheIds;
    }

    @Override
    public Optional<V> getClientOptValue(ResourceKey<V> key) {
        return JavaHelper.getOpt(this.syncMap, key);
    }

    public Optional<Class<V>> getRegistryClass() {
        return Optional.ofNullable(registryClass);
    }

}
