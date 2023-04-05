package hungteen.htlib.common.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import hungteen.htlib.HTLib;
import hungteen.htlib.api.interfaces.IHTSimpleRegistry;
import hungteen.htlib.api.interfaces.ISimpleEntry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 主要是用于先于常规注册的一些东西，先注册这些可以更方便的一个循环来注册常规注册。<br>
 * 建议在自身mod的构造函数中注册，如{@link HTLib#HTLib()}。
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/25 9:21
 */
public final class HTSimpleRegistry<T extends ISimpleEntry> implements IHTSimpleRegistry<T> {

    private final BiMap<ResourceLocation, Optional<? extends T>> registryMap = Maps.synchronizedBiMap(HashBiMap.create());
    private final ResourceLocation registryName;

    HTSimpleRegistry(ResourceLocation registryName){
        this.registryName = registryName;
    }

    @Override
    public <I extends T> I register(@NotNull I type) {
        if(registryMap.containsKey(type.getLocation())){
            HTLib.getLogger().warn("HTSimpleRegistry {} already registered {}", this.getRegistryName(), type.getLocation());
        }
        registryMap.put(type.getLocation(), Optional.of(type));
        return type;
    }

    @Override
    public List<T> getValues() {
        return registryMap.values().stream().filter(Optional::isPresent).map(Optional::get).map(t -> (T)t).toList();
    }

    @Override
    public List<ResourceLocation> getIds() {
        return registryMap.keySet().stream().toList();
    }

    @Override
    public List<Map.Entry<ResourceLocation, T>> getEntries() {
        return registryMap.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().map(tmp -> (T)tmp)))
                .filter(entry -> entry.getValue().isPresent())
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().get()))
                .toList();
    }

    @Override
    public Optional<T> getValue(ResourceLocation type) {
        return registryMap.getOrDefault(type, Optional.empty()).map(t -> (T)t);
    }

    @Override
    public <I extends T> Optional<ResourceLocation> getKey(I type) {
        return Optional.ofNullable(registryMap.inverse().getOrDefault(Optional.ofNullable(type), null));
    }

    @Override
    public ResourceLocation getRegistryName() {
        return registryName;
    }

    @Override
    public Codec<T> byNameCodec() {
        return ResourceLocation.CODEC.flatXmap((location) -> {
            return this.getValue(location).map(DataResult::success).orElseGet(() -> {
                return DataResult.error("Unknown registry key in " + this.getRegistryName() + ": " + location);
            });
        }, (value) -> {
            return this.getKey(value).map(DataResult::success).orElseGet(() -> {
                return DataResult.error("Unknown registry element in " + this.getRegistryName() + ": " + value);
            });
        });
    }

}
