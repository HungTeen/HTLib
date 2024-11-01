package hungteen.htlib.common.impl.registry;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.HTLibAPI;
import hungteen.htlib.api.registry.HTSimpleRegistry;
import hungteen.htlib.api.registry.SimpleEntry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 注册项会被放入 Map 中管理。<br>
 * 建议在自身mod的构造函数中注册，使用 {@link ConcurrentHashMap} 防止线程不安全。<br>
 * @author PangTeen
 * @program HTLib
 * @data 2023/6/28 14:36
 */
public final class HTSimpleRegistryImpl<T extends SimpleEntry> extends HTRegistryImpl<T> implements HTSimpleRegistry<T> {

    private final Map<ResourceLocation, Optional<? extends T>> registryMap = new ConcurrentHashMap<>();

    public HTSimpleRegistryImpl(ResourceLocation registryName) {
        super(registryName);
    }

    @Override
    public <I extends T> I register(@NotNull I type) {
        if(registryMap.containsKey(type.getLocation())){
            HTLibAPI.logger().warn("HTSimpleRegistry {} already registered {}", this.getRegistryName(), type.getLocation());
        }
        registryMap.put(type.getLocation(), Optional.of(type));
        return type;
    }

    @Override
    public List<T> getValues() {
        return registryMap.values().stream().filter(Optional::isPresent).map(Optional::get).map(t -> (T)t).toList();
    }

    @Override
    public Set<ResourceLocation> getKeys() {
        return registryMap.keySet();
    }

    @Override
    public Set<Map.Entry<ResourceLocation, T>> getEntries() {
        return registryMap.entrySet().stream()
                .filter(entry -> entry.getValue().isPresent())
                .map(entry -> Map.entry(entry.getKey(), (T) entry.getValue().get()))
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<T> getValue(ResourceLocation type) {
        return registryMap.getOrDefault(type, Optional.empty()).map(t -> (T)t);
    }

    @Override
    public <I extends T> Optional<ResourceLocation> getKey(I type) {
        return Optional.ofNullable(type.getLocation());
    }

    @Override
    public Codec<T> byNameCodec() {
        return HTSimpleRegistry.super.byNameCodec();
    }
}
