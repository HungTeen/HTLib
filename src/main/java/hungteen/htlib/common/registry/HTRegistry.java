package hungteen.htlib.common.registry;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import hungteen.htlib.HTLib;
import hungteen.htlib.api.interfaces.IHTRegistry;
import hungteen.htlib.util.helper.registry.RegistryHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryBuilder;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 主要是用于先于常规注册的一些东西，先注册这些可以更方便的一个循环来注册常规注册。<br>
 * 建议在自身mod的构造函数中注册，如{@link HTLib#HTLib()}。
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/25 9:21
 */
public class HTRegistry<T> implements IHTRegistry<T> {

    private final ConcurrentHashMap<ResourceLocation, Supplier<? extends T>> registryMap = new ConcurrentHashMap<>();
    private final ResourceKey<Registry<T>> registryKey;
    private final HTRegistryHolder<T> registryHolder;
    private final Supplier<RegistryBuilder<?>> registryFactory;
    private final RegistryHelper<T> registryHelper;
    private boolean seenRegisterEvent = false;

    HTRegistry(ResourceLocation registryName) {
        this(registryName, () -> new RegistryBuilder<T>().setName(registryName).setMaxID(Integer.MAX_VALUE - 1).disableSaving().disableSync().hasTags());
    }

    HTRegistry(ResourceLocation registryName, final Supplier<RegistryBuilder<T>> sup) {
        this.registryKey = ResourceKey.createRegistryKey(registryName);
        this.registryHolder = new HTRegistryHolder<>(this.registryKey);
        this.registryFactory = () -> sup.get().setName(registryName);
        this.registryHelper = new RegistryHelper<>() {
            @Override
            public Either<IForgeRegistry<T>, Registry<T>> getRegistry() {
                return Either.left(HTRegistry.this.getRegistry());
            }
        };
    }

    @Override
    public void register(IEventBus modBus) {
        modBus.addListener(this::createRegistry);
        modBus.addListener(this::addEntries);
        modBus.addListener(this::clearEntries);
    }

    @Override
    public <I extends T> I register(ResourceLocation name, @NotNull I type) {
        if (seenRegisterEvent) {
            throw new IllegalStateException("Cannot register new entries to HTSimpleRegistry after RegisterEvent has been fired.");
        }
        if (registryMap.containsKey(name)) {
            HTLib.getLogger().warn("HTSimpleRegistry {} already registered {}", this.getRegistryName(), name);
        }
        registryMap.put(name, () -> type);
        return type;
    }

    @Override
    public Collection<T> getValues() {
        return this.canUseVanilla() ? Objects.requireNonNull(getRegistry()).getValues() : registryMap.values().stream().map(Supplier::get).map(t -> (T) t).toList();
    }

    @Override
    public Set<ResourceLocation> getKeys() {
        return this.canUseVanilla() ? Objects.requireNonNull(getRegistry()).getKeys() : new HashSet<>(registryMap.keySet());
    }

    @Override
    public Set<Map.Entry<ResourceLocation, T>> getEntries() {
        return this.canUseVanilla() ? Objects.requireNonNull(getRegistry()).getEntries().stream()
                .map(entry -> Map.entry(entry.getKey().location(), entry.getValue()))
                .collect(Collectors.toSet()) : registryMap.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), (T) entry.getValue().get()))
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<T> getValue(ResourceLocation type) {
        return this.canUseVanilla() ? Optional.ofNullable(Objects.requireNonNull(getRegistry()).getValue(type)) : Optional.ofNullable(registryMap.getOrDefault(type, () -> (T) null).get());
    }

    @Override
    public <I extends T> Optional<ResourceLocation> getKey(I type) {
        return this.canUseVanilla() ? Optional.ofNullable(Objects.requireNonNull(getRegistry()).getKey(type)) : Optional.empty();
    }

    @Override
    public ResourceKey<T> createKey(ResourceLocation name) {
        return ResourceKey.create(getRegistryKey(), name);
    }

    @Override
    public ResourceLocation getRegistryName() {
        return this.registryKey.location();
    }

    @Override
    public ResourceKey<Registry<T>> getRegistryKey() {
        return registryKey;
    }

    @Override
    public Codec<T> byNameCodec() {
        return this.canUseVanilla() ? Objects.requireNonNull(getRegistry()).getCodec() :
                ResourceLocation.CODEC.flatXmap((location) -> {
                    return this.getValue(location).map(DataResult::success).orElseGet(() -> {
                        return DataResult.error(() -> "Unknown registry key in " + this.getRegistryName() + ": " + location);
                    });
                }, (value) -> {
                    return this.getKey(value).map(DataResult::success).orElseGet(() -> {
                        return DataResult.error(() -> "Unknown registry element in " + this.getRegistryName() + ": " + value);
                    });
                });
    }

    @Nullable
    public IForgeRegistry<T> getRegistry() {
        return this.registryHolder.get();
    }

    public RegistryHelper<T> helper() {
        return registryHelper;
    }

    private boolean canUseVanilla() {
        return this.seenRegisterEvent && this.registryHolder.get() != null;
    }

    private void addEntries(RegisterEvent event) {
        if (event.getRegistryKey().equals(this.getRegistryKey())) {
            this.registryMap.forEach((key, value) -> event.register(this.getRegistryKey(), key, () -> (T) value.get()));
            this.seenRegisterEvent = true;
        }
    }

    private void createRegistry(NewRegistryEvent event) {
        if (this.registryFactory != null) {
            event.create(this.registryFactory.get(), this::onFill);
        }
    }

    private void onFill(IForgeRegistry<?> registry) {

    }

    private void clearEntries(FMLCommonSetupEvent event) {
        this.registryMap.clear();
    }

}
