package hungteen.htlib.common.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import hungteen.htlib.HTLib;
import hungteen.htlib.api.interfaces.IHTCodecRegistry;
import hungteen.htlib.common.network.NetworkHandler;
import hungteen.htlib.common.network.SyncDatapackPacket;
import hungteen.htlib.util.helper.CodecHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DataPackRegistryEvent;

import java.util.function.Supplier;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/6/28 11:10
 */
public class HTCodecRegistry<V> extends HTRegistry<V> implements IHTCodecRegistry<V> {

    private final BiMap<ResourceLocation, V> syncMap = HashBiMap.create();
    private final Class<V> registryClass;
    private final Supplier<Codec<V>> codecSup;
    private final Supplier<Codec<V>> syncSup;
    private final boolean defaultSync;

    HTCodecRegistry(Class<V> registryClass, ResourceLocation registryName, Supplier<Codec<V>> codecSup, Supplier<Codec<V>> syncSup, boolean defaultSync) {
        super(registryName);
        this.registryClass = registryClass;
        this.codecSup = codecSup;
        this.syncSup = syncSup;
        this.defaultSync = defaultSync;
    }

    @Override
    public void register(IEventBus modBus){
        modBus.addListener(this::addRegistry);
    }

    public void syncToClient(ServerPlayer player) {
        if(! this.defaultSync && this.syncSup != null){
            this.getKeys(player.level()).forEach(key -> {
                this.getOptValue(player.level(), key).flatMap(value -> CodecHelper.encodeNbt(this.syncSup.get(), value)
                        .resultOrPartial(msg -> HTLib.getLogger().warn("HTCodecRegistry : + msg"))).ifPresent(tag -> {
                    if (tag instanceof CompoundTag nbt) {
                        NetworkHandler.sendToClient(player, new SyncDatapackPacket(this.getRegistryName(), key.location(), nbt));
                    }
                });
            });
        }
    }

    public void syncRegister(ResourceLocation key, Object value){
        if(this.customSync()){
            if (containKey(key)) {
                HTLib.getLogger().warn("HTCodecRegistry {} already registered {}", this.getRegistryName(), key);
            } else if (!this.getRegistryClass().isInstance(value)) {
                HTLib.getLogger().warn("HTCodecRegistry {} can not cast {} to correct entityType", this.getRegistryName(), key);
            }
            syncMap.put(key, this.getRegistryClass().cast(value));
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

    public boolean customSync(){
        return this.syncSup != null && ! this.defaultSync;
    }

    public boolean defaultSync(){
        return this.syncSup != null && this.defaultSync;
    }

    public Class<V> getRegistryClass() {
        return registryClass;
    }

    public boolean containKey(ResourceLocation name) {
        return syncMap.containsKey(name);
    }

    public Codec<V> getSyncCodec(){
        return this.syncSup == null ? null : this.syncSup.get();
    }

}
