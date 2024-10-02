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
     * @param registryName 注册名，决定了数据的路径。
     * @param codecSup 序列化格式。
     * @param syncSup 同步格式。
     * @param registryClass 数据类。
     */
    HTCodecRegistry(ResourceLocation registryName, Supplier<Codec<V>> codecSup, @Nullable Supplier<Codec<V>> syncSup, @Nullable Class<V> registryClass, boolean requireCache) {
        super(registryName);
        this.codecSup = codecSup;
        this.syncSup = syncSup;
        this.registryClass = registryClass;
        this.requireCache = requireCache;
    }

    @Override
    public void register(IEventBus modBus){
        modBus.addListener(this::addRegistry);
    }

    public void syncToClient(ServerPlayer player) {
        if(this.customSync()){
            this.getKeys(player.level()).forEach(key -> {
                this.getOptValue(player.level(), key).flatMap(value -> CodecHelper.encodeNbt(this.syncSup.get(), value)
                        .resultOrPartial(msg -> HTLib.getLogger().warn("HTCodecRegistry : + msg"))).ifPresent(tag -> {
                    if (tag instanceof CompoundTag nbt) {
                        NetworkHandler.sendToClient(player, new SyncDatapackPacket(this.getRegistryName(), key.location(), nbt));
                    }
                });
            });
        }
        if(requireCache()){
            this.cacheIds = this.getKeys(player.level()).stream().map(ResourceKey::location).toList();
        }
    }

    public void syncRegister(ResourceLocation name, Object value){
        final ResourceKey<V> key = ResourceKey.create(this.getRegistryKey(), name);
        if(this.customSync() && this.getRegistryClass().isPresent()){
            if (syncMap.containsKey(key)) {
                HTLib.getLogger().warn("HTCodecRegistry {} already registered {}", this.getRegistryName(), name);
            } else if (!this.getRegistryClass().get().isInstance(value)) {
                HTLib.getLogger().warn("HTCodecRegistry {} can not cast {} to correct entityType", this.getRegistryName(), name);
            }
            syncMap.put(key, this.getRegistryClass().get().cast(value));
        }
    }

    /**
     * Codec<Holder<T>> 不适用于原版的同步方法，故自己绕过。
     */
    private void addRegistry(DataPackRegistryEvent.NewRegistry event){
        if(this.defaultSync()){
            event.dataPackRegistry(this.getRegistryKey(), this.codecSup.get(), this.syncSup.get());
        } else {
            event.dataPackRegistry(this.getRegistryKey(), this.codecSup.get());
        }
    }

    @Override
    public Optional<Codec<V>> getSyncCodec(){
        return this.syncSup == null ? Optional.empty() : Optional.ofNullable(this.syncSup.get());
    }

    @Override
    public boolean customSync(){
        return this.requireSync() && this.getRegistryClass().isPresent();
    }

    @Override
    public boolean defaultSync(){
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
