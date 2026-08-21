package hungteen.htlib.common.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import hungteen.htlib.HTLib;
import hungteen.htlib.api.interfaces.IHTCommonRegistry;
import hungteen.htlib.api.interfaces.IHTResourceHelper;
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
 * HTLib 特殊注册系统的"先于原版注册"分支（通用注册 / Forge Registry 实现）。 <br>
 *
 * <h3>这是什么？</h3>
 * <p>很多模组需要先注册一批<b>轻量"类型/选项"对象</b>（例如 HTLib 的 raid 类型、
 * wave 类型、spawn 类型、result 类型），这些对象不受原版常规注册管理。本类为它们
 * 提供一个 <b>自定义 Forge Registry</b>（{@link IForgeRegistry}），在 mod 加载阶段
 * 由 NewRegistryEvent 创建、由 RegisterEvent 填充，注册后即可通过
 * {@code getValues()} / {@code getValue()} / {@code getKey()} / {@code byNameCodec()} 访问。</p>
 * <p>由于它"先于/独立于"常规注册，注册完成后容易用一个循环再驱动方块、物品、
 * 实体等常规注册的批量生成（这也是本类的核心用途）。</p>
 *
 * <h3>作用于哪个生命周期？</h3>
 * <ol>
 *     <li><b>Mod 构造函数</b>：由 {@link HTRegistryManager#createCommon} 创建，
 *     此时条目暂存于 {@link #registryMap}（尚未进入 Forge 注册表）。</li>
 *     <li><b>{@link net.minecraftforge.registries.NewRegistryEvent}</b>：
 *     {@link #register} 挂接的本方法被触发，用 {@link #registryFactory} 创建真正的
 *     {@link IForgeRegistry}。</li>
 *     <li><b>{@link net.minecraftforge.registries.RegisterEvent}</b>：
 *     {@link #addEntries} 把 {@link #registryMap} 中暂存的条目一次性写入注册表，
 *     并置 {@link #seenRegisterEvent} 标记，之后禁止再 {@link #register}（会抛异常）。</li>
 *     <li><b>{@link net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent}</b>：
 *     {@link #clearEntries} 清空暂存表以释放内存；此后数据均可从正式注册表读取。</li>
 * </ol>
 *
 * <p><b>注意：</b>请在每个 mod 自身的构造函数中创建并 {@link #register}，
 * 参考 {@link HTLib#HTLib()} 与 {@link HTLib#register}。</p>
 *
 * @author PangTeen
 * @program HTLib
 * @data 2023/7/11 9:38
 */
public class HTCommonRegistry<T> extends HTRegistry<T> implements IHTCommonRegistry<T> {

    private final ConcurrentHashMap<ResourceLocation, Supplier<? extends T>> registryMap = new ConcurrentHashMap<>();
    private final Supplier<RegistryBuilder<?>> registryFactory;
    private boolean seenRegisterEvent = false;

    HTCommonRegistry(ResourceLocation registryName) {
        this(registryName, () -> new RegistryBuilder<T>().setName(registryName).setMaxID(Integer.MAX_VALUE - 1).disableSaving().hasTags());
    }

    HTCommonRegistry(ResourceLocation registryName, final Supplier<RegistryBuilder<T>> builderSup) {
        super(registryName);
        this.registryFactory = () -> builderSup.get().setName(registryName);
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
    @Override
    public IForgeRegistry<T> getRegistry() {
        return this.registryHolder.get();
    }

    @Override
    public IHTResourceHelper<T> helper() {
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
