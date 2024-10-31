package hungteen.htlib.common.impl.registry;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.registry.HTCustomRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/21 22:14
 **/
public class HTForgeCustomRegistry<T> extends HTCustomRegistryImpl<T> implements HTCustomRegistry<T> {

    private final ConcurrentHashMap<ResourceLocation, Supplier<? extends T>> registryMap = new ConcurrentHashMap<>();
    private final Supplier<RegistryBuilder<?>> registryFactory;
    protected final HTForgeRegistryHolder<T> registryHolder;

    public HTForgeCustomRegistry(ResourceLocation registryName) {
        this(registryName, () -> new RegistryBuilder<T>().setName(registryName).setMaxID(Integer.MAX_VALUE - 1).disableSaving().hasTags());
    }

    public HTForgeCustomRegistry(ResourceLocation registryName, final Supplier<RegistryBuilder<T>> builderSup) {
        super(registryName);
        this.registryHolder = new HTForgeRegistryHolder<>(this.registryKey);
        this.registryFactory = () -> builderSup.get().setName(registryName);
    }

    public void addEntries(RegisterEvent event) {
        if (event.getRegistryKey().equals(this.getRegistryKey())) {
            this.registryMap.forEach((key, value) -> event.register(this.getRegistryKey(), key, () -> (T) value.get()));
            this.seenRegisterEvent = true;
        }
    }

    public void createRegistry(NewRegistryEvent event) {
        if (this.registryFactory != null) {
            event.create(this.registryFactory.get(), this::onFill);
        }
    }

    public void clearEntries() {
        this.registryMap.clear();
    }

    @Override
    public Collection<T> getValues() {
        return this.canUseVanilla() ? Objects.requireNonNull(getRegistry()).getValues() : super.getValues();
    }

    @Override
    public Set<ResourceLocation> getKeys() {
        return this.canUseVanilla() ? Objects.requireNonNull(getRegistry()).getKeys() : super.getKeys();
    }

    @Override
    public Set<Map.Entry<ResourceLocation, T>> getEntries() {
        return this.canUseVanilla() ? Objects.requireNonNull(getRegistry()).getEntries().stream()
                .map(entry -> Map.entry(entry.getKey().location(), entry.getValue()))
                .collect(Collectors.toSet()) : super.getEntries();
    }

    @Override
    public Optional<T> getValue(ResourceLocation type) {
        return this.canUseVanilla() ? Optional.ofNullable(Objects.requireNonNull(getRegistry()).getValue(type)) : super.getValue(type);
    }

    @Override
    public <I extends T> Optional<ResourceLocation> getKey(I type) {
        return this.canUseVanilla() ? Optional.ofNullable(Objects.requireNonNull(getRegistry()).getKey(type)) : super.getKey(type);
    }

    @Override
    public Codec<T> byNameCodec() {
        return getRegistry().getCodec();
    }

    public IForgeRegistry<T> getRegistry() {
        return this.registryHolder.get();
    }

    private void onFill(IForgeRegistry<?> registry) {

    }

}
