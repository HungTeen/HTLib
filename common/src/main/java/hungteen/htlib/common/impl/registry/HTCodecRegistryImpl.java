package hungteen.htlib.common.impl.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import hungteen.htlib.api.HTLibAPI;
import hungteen.htlib.api.registry.HTCodecRegistry;
import hungteen.htlib.api.util.helper.HTResourceHelper;
import hungteen.htlib.common.network.packet.SyncDatapackPacket;
import hungteen.htlib.platform.HTLibPlatformAPI;
import hungteen.htlib.util.helper.CodecHelper;
import hungteen.htlib.util.helper.JavaHelper;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/26 22:50
 **/
public class HTCodecRegistryImpl<V> extends HTRegistryImpl<V> implements HTCodecRegistry<V> {

    /**
     * 理论上是单线程被访问的，所以不需要线程安全。
     */
    private final BiMap<ResourceKey<V>, V> syncMap = HashBiMap.create();
    private final HTResourceHelper<V> helper;
    private List<ResourceLocation> cacheIds = Lists.newArrayList();
    private final Class<V> registryClass;
    protected final Supplier<Codec<V>> codecSup;
    protected final Supplier<Codec<V>> syncSup;
    private final boolean requireCache;

    /**
     * @param registryName  注册名，决定了数据的路径。
     * @param codecSup      序列化格式。
     * @param syncSup       同步格式。
     * @param registryClass 数据类。
     */
    public HTCodecRegistryImpl(ResourceLocation registryName, Supplier<Codec<V>> codecSup, @Nullable Supplier<Codec<V>> syncSup, @Nullable Class<V> registryClass, boolean requireCache) {
        super(registryName);
        this.helper = this::getRegistryKey;
        this.codecSup = codecSup;
        this.syncSup = syncSup;
        this.registryClass = registryClass;
        this.requireCache = requireCache;
    }

    @Override
    public void syncToClient(List<ServerPlayer> players) {
        if (this.customSync() && !players.isEmpty()) {
            ServerLevel level = players.getFirst().serverLevel();
            Stream<Holder.Reference<V>> holders = this.getHolders(level);
            players.forEach(player -> {
//                CodecHelper.encodeNbt(getHolderCodec(codecSup.get()).listOf(), holders)
//                        .resultOrPartial(msg -> HTLibAPI.logger().warn("HTCodecRegistry : {}", msg))
//                        .ifPresent(tag -> {
//                            if (tag instanceof CompoundTag nbt) {
//                                HTLibPlatformAPI.get().sendToClient(player, new SyncDatapackPacket(this.getRegistryName(), nbt));
//                            }
//                        });
                holders.forEach(holder -> {
                    CodecHelper.encodeNbt(this.syncSup.get(), holder.value())
                            .resultOrPartial(msg -> HTLibAPI.logger().warn("HTCodecRegistry : {}", msg))
                            .ifPresent(tag -> {
                                if (tag instanceof CompoundTag nbt) {
                                    HTLibPlatformAPI.get().sendToClient(player, new SyncDatapackPacket(this.getRegistryName(), holder.key().location(), nbt));
                                }
                            });
                });
            });
        }
        if (requireCache() && !players.isEmpty()) {
            ServerLevel level = players.getFirst().serverLevel();
            this.cacheIds = this.getKeys(level).map(ResourceKey::location).toList();
        }
    }

    @Override
    public void syncRegister(ResourceLocation name, Object value) {
        final ResourceKey<V> key = ResourceKey.create(this.getRegistryKey(), name);
        if (this.customSync() && this.getRegistryClass().isPresent()) {
            if (syncMap.containsKey(key)) {
                HTLibAPI.logger().warn("HTCodecRegistry {} already registered {}", this.getRegistryName(), name);
            } else if (!this.getRegistryClass().get().isInstance(value)) {
                HTLibAPI.logger().warn("HTCodecRegistry {} can not cast {} to correct entityType", this.getRegistryName(), name);
            }
            syncMap.put(key, this.getRegistryClass().get().cast(value));
        }
    }

    @Override
    public Optional<Codec<V>> getCodec() {
        return this.codecSup == null ? Optional.empty() : Optional.ofNullable(this.codecSup.get());
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

    @Override
    public HTResourceHelper<V> helper() {
        return helper;
    }
}
