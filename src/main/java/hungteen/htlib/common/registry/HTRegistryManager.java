package hungteen.htlib.common.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import hungteen.htlib.HTLib;
import hungteen.htlib.api.interfaces.ISimpleEntry;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-25 21:27
 **/
public class HTRegistryManager {

    private static final BiMap<String, HTCodecRegistry<?>> CODEC_REGISTRIES = HashBiMap.create();

    /**
     * 全局初始化。
     */
    public static void globalInit() {
        getRegistries().stream().filter(HTCodecRegistry::isGlobal).forEach(HTCodecRegistry::init);
    }

    /**
     * do not create more than one registry for specific registry entityType.
     */
    public static <T extends ISimpleEntry> HTSimpleRegistry<T> create(ResourceLocation registryName){
        return new HTSimpleRegistry<>(registryName);
    }

    public static Optional<HTCodecRegistry<?>> get(String registryName){
        return Optional.ofNullable(CODEC_REGISTRIES.getOrDefault(registryName, null));
    }

    /**
     * Globally create.
     */
    public static <T> HTCodecRegistry<T> create(Class<T> clazz, String registryName, Supplier<Codec<T>> supplier, String namespace){
        return create(clazz, registryName, supplier, true, namespace);
    }

    public static <T> HTCodecRegistry<T> create(Class<T> clazz, String registryName, Supplier<Codec<T>> supplier, boolean isGlobal){
        return create(clazz, registryName, supplier, isGlobal, HTLib.MOD_ID);
    }

    public static <T> HTCodecRegistry<T> create(Class<T> clazz, String registryName, Supplier<Codec<T>> supplier){
        return create(clazz, registryName, supplier, false);
    }

    public static <T> HTCodecRegistry<T> create(Class<T> clazz, String registryName, Supplier<Codec<T>> supplier, boolean isGlobal, String namespace){
        if(CODEC_REGISTRIES.containsKey(registryName)){
            throw new IllegalArgumentException("Cannot create duplicate registry {}, use get instead".formatted(registryName));
        }
        HTCodecRegistry<T> registry = new HTCodecRegistry<>(clazz, registryName, supplier, isGlobal, namespace);
        CODEC_REGISTRIES.put(registryName, registry);
        return registry;
    }

    public static List<String> getRegistryNames(boolean isGlobal){
        return CODEC_REGISTRIES.entrySet().stream().filter(entry -> entry.getValue().isGlobal() == isGlobal).map(Map.Entry::getKey).toList();
    }

    public static List<HTCodecRegistry<?>> getRegistries(){
        return CODEC_REGISTRIES.values().stream().toList();
    }

}
