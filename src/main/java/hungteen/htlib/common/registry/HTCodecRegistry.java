package hungteen.htlib.common.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import hungteen.htlib.HTLib;
import hungteen.htlib.common.network.DataPackPacket;
import hungteen.htlib.common.network.NetworkHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/24 16:25
 * <p>
 * It seems that forge can not support {type -> value} kind codec registry.
 */
public final class HTCodecRegistry<V> {

    /**
     * Register by code.
     */
    private final BiMap<ResourceLocation, V> innerMap = HashBiMap.create();
    /**
     * Register by data pack.
     */
    private final BiMap<ResourceLocation, V> outerMap = HashBiMap.create();
    private final String registryName;
    private final Class<V> registryClass;
    private final Supplier<Codec<V>> supplier;

    HTCodecRegistry(Class<V> registryClass, String registryName, Supplier<Codec<V>> supplier) {
        this.registryClass = registryClass;
        this.registryName = registryName;
        this.supplier = supplier;
    }

    public HTRegistryHolder<V> innerRegister(ResourceLocation name, V value) {
        if (containKey(name)) {
            HTLib.getLogger().warn("HTCodecRegistry {} already registered {}", this.getRegistryName(), name);
        }
        this.innerMap.put(name, value);
        return new HTRegistryHolder<>(name, value);
    }

    public void outerRegister(ResourceLocation name, Object value) {
        if (containKey(name)) {
            HTLib.getLogger().warn("HTCodecRegistry {} already registered {}", this.getRegistryName(), name);
        } else if (!this.getRegistryClass().isInstance(value)) {
            HTLib.getLogger().warn("HTCodecRegistry {} can not cast {} to correct type", this.getRegistryName(), name);
        }
        this.outerMap.put(name, this.getRegistryClass().cast(value));
    }

    public boolean containKey(ResourceLocation name) {
        return innerMap.containsKey(name) || outerMap.containsKey(name);
    }

    public boolean containValue(V value) {
        return innerMap.inverse().containsKey(value) || outerMap.inverse().containsKey(value);
    }

    public List<V> getAll() {
        return Stream.concat(this.innerMap.values().stream(), this.outerMap.values().stream()).toList();
    }

    public void syncToClient(ServerPlayer player) {
        this.outerMap.forEach((res, data) -> {
            this.getCodec()
                    .encodeStart(JsonOps.INSTANCE, data)
                    .resultOrPartial(msg -> HTLib.getLogger().error(msg))
                    .ifPresent(json -> {
                        NetworkHandler.sendToClient(player, new DataPackPacket(this.getRegistryName(), res, json));
                    });
        });
    }

    public void clearOutRegistries() {
        this.outerMap.clear();
    }

    public int getOuterCount() {
        return this.outerMap.size();
    }

    public Optional<V> getValue(String type) {
        return getValue(new ResourceLocation(type));
    }

    public Optional<V> getValue(ResourceLocation type) {
        return Optional.ofNullable(this.innerMap.containsKey(type) ? this.innerMap.get(type) : this.outerMap.getOrDefault(type, null));
    }

    public Optional<ResourceLocation> getKey(V value) {
        return Optional.ofNullable(this.containValue(value) ? this.innerMap.inverse().getOrDefault(value, this.outerMap.inverse().getOrDefault(value, null)) : null);
    }

    public String getRegistryName() {
        return registryName;
    }

    @Nonnull
    public Codec<V> getCodec() {
        return this.supplier.get();
    }

    public Class<V> getRegistryClass() {
        return registryClass;
    }

}
