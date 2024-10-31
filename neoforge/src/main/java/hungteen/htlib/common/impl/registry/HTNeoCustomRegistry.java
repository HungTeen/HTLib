package hungteen.htlib.common.impl.registry;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.registry.HTCustomRegistry;
import hungteen.htlib.api.util.helper.HTVanillaRegistryHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/21 22:14
 **/
public class HTNeoCustomRegistry<T> extends HTCustomRegistryImpl<T> implements HTCustomRegistry<T> {

    private final ConcurrentHashMap<ResourceLocation, Supplier<? extends T>> registryMap = new ConcurrentHashMap<>();
    private final Supplier<RegistryBuilder<?>> registryFactory;
    protected final HTNeoRegistryHolder<T> registryHolder;
    private HTVanillaRegistryHelper<T> registryHelper;

    public HTNeoCustomRegistry(ResourceLocation registryName) {
        this(registryName, () -> new RegistryBuilder<T>(ResourceKey.createRegistryKey(registryName)).maxId(Integer.MAX_VALUE - 1));
    }

    public HTNeoCustomRegistry(ResourceLocation registryName, final Supplier<RegistryBuilder<T>> builderSup) {
        super(registryName);
        this.registryHolder = new HTNeoRegistryHolder<>(this.registryKey);
        this.registryFactory = builderSup::get;
    }

    public void addEntries(RegisterEvent event) {
        if (event.getRegistryKey().equals(this.getRegistryKey())) {
            this.registryMap.forEach((key, value) -> event.register(this.getRegistryKey(), key, () -> (T) value.get()));
            this.seenRegisterEvent = true;
        }
    }

    public void createRegistry(NewRegistryEvent event) {
        if (this.registryFactory != null) {
            event.create(this.registryFactory.get());
        }
    }

    public void clearEntries() {
        this.registryMap.clear();
    }

    @Override
    public Collection<T> getValues() {
        return this.canUseVanilla() ? Objects.requireNonNull(getRegistry()).stream().toList() : super.getValues();
    }

    @Override
    public Set<ResourceLocation> getKeys() {
        return this.canUseVanilla() ? Objects.requireNonNull(getRegistry()).keySet() : super.getKeys();
    }

    @Override
    public Set<Map.Entry<ResourceLocation, T>> getEntries() {
        return this.canUseVanilla() ? Objects.requireNonNull(getRegistry()).entrySet().stream()
                .map(entry -> Map.entry(entry.getKey().location(), entry.getValue()))
                .collect(Collectors.toSet()) : super.getEntries();
    }

    @Override
    public Optional<T> getValue(ResourceLocation type) {
        return this.canUseVanilla() ? Optional.ofNullable(Objects.requireNonNull(getRegistry()).get(type)) : super.getValue(type);
    }

    @Override
    public <I extends T> Optional<ResourceLocation> getKey(I type) {
        return this.canUseVanilla() ? Optional.ofNullable(Objects.requireNonNull(getRegistry()).getKey(type)) : super.getKey(type);
    }

    @Override
    public Codec<T> byNameCodec() {
        return getHelper().getCodec();
    }

    public Registry<T> getRegistry() {
        return this.registryHolder.get();
    }

    public HTVanillaRegistryHelper<T> getHelper(){
        if(this.registryHelper == null){
            this.registryHelper = this::getRegistry;
        }
        return this.registryHelper;
    }

}
