package hungteen.htlib.common.impl.registry;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.registry.HTCustomRegistry;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/21 22:14
 **/
public class HTFabricCustomRegistry<T> extends HTCustomRegistryImpl<T> implements HTCustomRegistry<T> {

    private final Registry<T> registry;

    public HTFabricCustomRegistry(Class<T> clazz, ResourceLocation registryName) {
        super(registryName);
        this.registry = FabricRegistryBuilder.createSimple(this.registryKey)
                .attribute(RegistryAttribute.SYNCED)
                .buildAndRegister();
    }

    @Override
    public <I extends T> I register(ResourceLocation name, @NotNull I type) {
        return Registry.register(this.registry, name, type);
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
        return getRegistry().byNameCodec();
    }

    public Registry<T> getRegistry() {
        return this.registry;
    }

}
