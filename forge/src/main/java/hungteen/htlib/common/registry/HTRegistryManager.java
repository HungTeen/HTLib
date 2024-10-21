package hungteen.htlib.common.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import hungteen.htlib.HTLib;
import hungteen.htlib.api.registry.SimpleEntry;
import hungteen.htlib.util.helper.JavaHelper;
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
        } else {
            HTRegistryManager.getRegistries().forEach(registry -> {
                registry.syncToClient(event.getPlayer());
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
    public static <T extends SimpleEntry> HTSimpleRegistry<T> createSimple(ResourceLocation registryName){
        return new HTSimpleRegistry<>(registryName);
    }

    public static <T extends SimpleEntry> HTSimpleRegistry<T> createSimple(ResourceLocation registryName, Supplier<RegistryBuilder<T>> builderSup){
        return new HTSimpleRegistry<>(registryName, builderSup);
    }

    /**
     * Do not sync.
     */
    public static <T> HTCodecRegistry<T> create(ResourceLocation registryName, Supplier<Codec<T>> codecSup){
        return create(registryName, codecSup, null, null, false);
    }

    /**
     * Do not sync.
     */
    public static <T> HTCodecRegistry<T> create(ResourceLocation registryName, Supplier<Codec<T>> codecSup, boolean requireCache){
        return create(registryName, codecSup, null, null, requireCache);
    }

    /**
     * 使用HTLib的方法同步数据。
     */
    public static <T> HTCodecRegistry<T> create(ResourceLocation registryName, Supplier<Codec<T>> codecSup, Class<T> clazz){
        return create(registryName, codecSup, codecSup, clazz, false);
    }

    /**
     * 使用原版的方法同步数据。
     */
    public static <T> HTCodecRegistry<T> create(ResourceLocation registryName, Supplier<Codec<T>> codecSup, Supplier<Codec<T>> syncSup){
        return create(registryName, codecSup, syncSup, null, false);
    }

    /**
     * Do not create more than one registry for specific registry entityType.
     * @param syncSup 用于数据包的同步。
     * @param clazz 有clazz说明采用HTLib的方法同步数据，而非采用原版的同步方法。
     * @param requireCache 是否需要缓存数据（服务端）。
     */
    public static <T> HTCodecRegistry<T> create(ResourceLocation registryName, Supplier<Codec<T>> codecSup, @Nullable Supplier<Codec<T>> syncSup, Class<T> clazz, boolean requireCache){
        final HTCodecRegistry<T> codecRegistry = new HTCodecRegistry<>(registryName, codecSup, syncSup, clazz, requireCache);
        CODEC_REGISTRIES.put(registryName, codecRegistry);
        return codecRegistry;
    }

    public static Optional<HTCodecRegistry<?>> get(ResourceLocation registryName){
        return JavaHelper.getOpt(CODEC_REGISTRIES, registryName);
    }

    public static List<HTCodecRegistry<?>> getRegistries(){
        return CODEC_REGISTRIES.values().stream().toList();
    }

}
