package hungteen.htlib.common.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import hungteen.htlib.HTLib;
import hungteen.htlib.api.interfaces.ISimpleEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.registries.RegistryBuilder;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-25 21:27
 **/
public class HTRegistryManager {

    private static final BiMap<ResourceLocation, HTCodecRegistry<?>> CODEC_REGISTRIES = Maps.synchronizedBiMap(HashBiMap.create());

    /**
     * {@link HTLib#HTLib()}
     */
    public static void syncToClient(OnDatapackSyncEvent event){
        if(event.getPlayer() == null){
            HTRegistryManager.getRegistries().forEach(registry -> {
                event.getPlayerList().getPlayers().forEach(registry::syncToClient);
            });
        }
    }

    /**
     * do not create more than one registry for specific registry entityType.
     */
    public static <T> HTCommonRegistry<T> createCommon(ResourceLocation registryName){
        return new HTCommonRegistry<>(registryName);
    }

    public static <T> HTCommonRegistry<T> createCommon(ResourceLocation registryName, Supplier<RegistryBuilder<T>> builderSup){
        return new HTCommonRegistry<>(registryName, builderSup);
    }

    /**
     * do not create more than one registry for specific registry entityType.
     */
    public static <T extends ISimpleEntry> HTSimpleRegistry<T> createSimple(ResourceLocation registryName){
        return new HTSimpleRegistry<>(registryName);
    }

    public static <T extends ISimpleEntry> HTSimpleRegistry<T> createSimple(ResourceLocation registryName, Supplier<RegistryBuilder<T>> builderSup){
        return new HTSimpleRegistry<>(registryName, builderSup);
    }

    /**
     * Do not sync.
     */
    public static <T> HTCodecRegistry<T> create(ResourceLocation registryName, Class<T> clazz, Supplier<Codec<T>> codecSup){
        return create(registryName, clazz, codecSup, null);
    }

    /**
     * Sync.
     */
    public static <T> HTCodecRegistry<T> create(ResourceLocation registryName, Class<T> clazz, Supplier<Codec<T>> codecSup, boolean defaultSync){
        return create(registryName, clazz, codecSup, codecSup, defaultSync);
    }

    public static <T> HTCodecRegistry<T> create(ResourceLocation registryName, Class<T> clazz, Supplier<Codec<T>> codecSup, Supplier<Codec<T>> syncSup){
        return create(registryName, clazz, codecSup, syncSup, true);
    }

    /**
     * do not create more than one registry for specific registry entityType.
     * @param syncSup 用于数据包的同步。
     * @param defaultSync 是否采用原版的同步方法。
     */
    public static <T> HTCodecRegistry<T> create(ResourceLocation registryName, Class<T> clazz, Supplier<Codec<T>> codecSup, @Nullable Supplier<Codec<T>> syncSup, boolean defaultSync){
        final HTCodecRegistry<T> codecRegistry = new HTCodecRegistry<>(clazz, registryName, codecSup, syncSup, defaultSync);
        CODEC_REGISTRIES.put(registryName, codecRegistry);
        return codecRegistry;
    }

    public static Optional<HTCodecRegistry<?>> get(ResourceLocation registryName){
        return Optional.ofNullable(CODEC_REGISTRIES.getOrDefault(registryName, null));
    }

    public static List<HTCodecRegistry<?>> getRegistries(){
        return CODEC_REGISTRIES.values().stream().toList();
    }

}
