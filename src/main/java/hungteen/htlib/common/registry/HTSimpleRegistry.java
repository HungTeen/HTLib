package hungteen.htlib.common.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import hungteen.htlib.HTLib;
import hungteen.htlib.api.interfaces.ISimpleRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/25 9:21
 *
 * 主要是用于先于常规注册的一些东西，先注册这些可以更方便的一个循环来注册常规注册。<br>
 * 建议在mod构造函数中注册。
 */
public final class HTSimpleRegistry<T extends ISimpleRegistry> {

    private final BiMap<ResourceLocation, Optional<? extends T>> registryMap = HashBiMap.create();
    private final ResourceLocation registryName;

    HTSimpleRegistry(ResourceLocation registryName){
        this.registryName = registryName;
    }

    public <I extends T> void register(I type) {
        if(registryMap.containsKey(type.getLocation())){
            HTLib.getLogger().warn("HTSimpleRegistry {} already registered {}", this.getRegistryName(), type.getLocation());
        }
        registryMap.put(type.getLocation(), Optional.ofNullable(type));
    }

//    /**
//     * Require Mod Bus.
//     */
//    public void register(IEventBus bus) {
//        bus.register(new HTEventDispatcher(this));
//    }

    public List<? extends T> getAll() {
        return registryMap.values().stream().filter(Optional::isPresent).map(Optional::get).toList();
    }

    public List<ResourceLocation> getIds() {
        return registryMap.keySet().stream().toList();
    }

    public Optional<? extends T> getValue(ResourceLocation type) {
        return registryMap.getOrDefault(type, Optional.empty());
    }

    public <I extends T> Optional<ResourceLocation> getKey(I type) {
        return Optional.ofNullable(registryMap.inverse().getOrDefault(Optional.ofNullable(type), null));
    }

    public ResourceLocation getRegistryName() {
        return registryName;
    }

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

//    public static class HTEventDispatcher {
//        private final HTSimpleRegistry<?> register;
//
//        public HTEventDispatcher(final HTSimpleRegistry<?> register) {
//            this.register = register;
//        }
//
//        @SubscribeEvent
//        public void handleEvent(NewRegistryEvent event) {
//            register.addEntries();
//        }
//    }
}
