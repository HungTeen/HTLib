package hungteen.htlib.common.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.ISimpleRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
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
     * do not create more than one registry for specific registry type.
     */
    public static <T extends ISimpleRegistry> HTSimpleRegistry<T> create(ResourceLocation registryName){
        return new HTSimpleRegistry<>(registryName);
    }

    public static Optional<HTCodecRegistry<?>> get(String registryName){
        return Optional.ofNullable(CODEC_REGISTRIES.getOrDefault(registryName, null));
    }

    public static <T> HTCodecRegistry<T> create(Class<T> clazz, String registryName, Supplier<Codec<T>> supplier){
        if(CODEC_REGISTRIES.containsKey(registryName)){
            throw new IllegalArgumentException("Cannot create duplicate registry {}, use get instead".formatted(registryName));
        }
        HTCodecRegistry<T> registry = new HTCodecRegistry<>(clazz, registryName, supplier);
        CODEC_REGISTRIES.put(registryName, registry);
        return registry;
    }

    public static List<String> getRegistries(){
        return CODEC_REGISTRIES.keySet().stream().toList();
    }

}
