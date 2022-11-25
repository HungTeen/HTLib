package hungteen.htlib.common.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import hungteen.htlib.HTLib;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/24 16:25
 *
 * It seems that forge can not support {type -> value} kind codec registry.
 */
public final class HTCodecRegistry<V, K extends ICodecRegistryType<V>> {

    private static final HashMap<ResourceLocation, HTCodecRegistry<?, ?>> codecRegistries = new HashMap<>();
    /**
     * Register by code.
     */
    private final BiMap<ResourceLocation, V> innerMap = HashBiMap.create();
    /**
     * Register by data pack.
     */
    private final BiMap<ResourceLocation, V> outerMap = HashBiMap.create();
    private final BiMap<ResourceLocation, K> codecMap = HashBiMap.create();
    private final ResourceLocation registryName;
    private final Codec<K> codec;

    public static HTCodecRegistry create(ResourceLocation registryName){
        if(codecRegistries.containsKey(registryName)){
            return codecRegistries.get(registryName);
        }
        HTCodecRegistry registry = new HTCodecRegistry(registryName);
        codecRegistries.put(registryName, registry);
        return registry;
    }

    public HTCodecRegistry(ResourceLocation registryName){
        this.registryName = registryName;
        this.codec = this.byNameCodec();
    }

    public void registerCodec(ResourceLocation name, K value) {
        if(this.codecMap.containsKey(name)){
            HTLib.getLogger().warn("HTCodecRegistry {} already registered codec {}", this.getRegistryName(), name);
        }
        this.codecMap.put(name, value);
    }

    public void innerRegister(ResourceLocation name, V value) {
        if(containKey(name)){
            HTLib.getLogger().warn("HTCodecRegistry {} already registered {}", this.getRegistryName(), name);
        }
        this.innerMap.put(name, value);
    }

    public void outerRegister(ResourceLocation name, V value) {
        if(containKey(name)){
            HTLib.getLogger().warn("HTCodecRegistry {} already registered {}", this.getRegistryName(), name);
        }
        this.outerMap.put(name, value);
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

    public Optional<V> getValue(String type) {
        return getValue(new ResourceLocation(type));
    }

    public Optional<V> getValue(ResourceLocation type) {
        return Optional.ofNullable(this.innerMap.containsKey(type) ? this.innerMap.get(type) : this.outerMap.getOrDefault(type, null));
    }

    public Optional<K> getCodecValue(ResourceLocation type) {
        return Optional.ofNullable(this.codecMap.getOrDefault(type, null));
    }

    public Optional<ResourceLocation> getKey(V value) {
        return Optional.ofNullable(this.containValue(value) ? this.innerMap.inverse().getOrDefault(value, this.outerMap.inverse().getOrDefault(value, null)) : null);
    }

    public Optional<ResourceLocation> getCodecKey(K value) {
        return Optional.ofNullable(this.codecMap.inverse().getOrDefault(value, null));
    }

    public ResourceLocation getRegistryName() {
        return registryName;
    }

    public Codec<K> byNameCodec() {
        return ResourceLocation.CODEC.flatXmap((location) -> {
            return this.getCodecValue(location).map(DataResult::success).orElseGet(() -> {
                return DataResult.error("Unknown registry key in " + this.getRegistryName() + ": " + location);
            });
        }, (value) -> {
            return this.getCodecKey(value).map(DataResult::success).orElseGet(() -> {
                return DataResult.error("Unknown registry element in " + this.getRegistryName() + ":" + value);
            });
        });
    }

}
