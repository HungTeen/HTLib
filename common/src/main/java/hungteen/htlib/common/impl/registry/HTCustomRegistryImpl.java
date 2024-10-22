package hungteen.htlib.common.impl.registry;

import hungteen.htlib.api.HTLibAPI;
import hungteen.htlib.api.interfaces.IHTResourceHelper;
import hungteen.htlib.api.registry.HTCustomRegistry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/21 22:14
 **/
public abstract class HTCustomRegistryImpl<T> extends HTRegistryImpl<T> implements HTCustomRegistry<T> {

    private final Map<ResourceLocation, Supplier<? extends T>> registryMap = new ConcurrentHashMap<>();
    protected boolean seenRegisterEvent = false;

    HTCustomRegistryImpl(ResourceLocation registryName) {
        super(registryName);
    }

    @Override
    public <I extends T> I register(ResourceLocation name, @NotNull I type) {
        if (seenRegisterEvent) {
            throw new IllegalStateException("Cannot register new entries to HTVanillaRegistry after RegisterEvent has been fired.");
        }
        if (registryMap.containsKey(name)) {
            HTLibAPI.logger().warn("HTVanillaRegistry {} already registered {}", this.getRegistryName(), name);
        }
        registryMap.put(name, () -> type);
        return type;
    }

    @Override
    public Collection<T> getValues() {
        return registryMap.values().stream().map(Supplier::get).map(t -> (T) t).toList();
    }

    @Override
    public Set<ResourceLocation> getKeys() {
        return registryMap.keySet();
    }

    @Override
    public Set<Map.Entry<ResourceLocation, T>> getEntries() {
        return registryMap.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), (T) entry.getValue().get()))
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<T> getValue(ResourceLocation type) {
        return Optional.ofNullable(registryMap.getOrDefault(type, () -> (T) null).get());
    }

    @Override
    public <I extends T> Optional<ResourceLocation> getKey(I type) {
        // Do not support reverse lookup.
        throw new UnsupportedOperationException("HTVanillaRegistry does not support reverse lookup before vanilla registry finish.");
    }

    @Override
    public IHTResourceHelper<T> helper() {
        return registryHelper;
    }

    /**
     * 在注册完成之前，只能使用缓存的注册。
     */
    public boolean canUseVanilla() {
        return this.seenRegisterEvent;
    }

}
