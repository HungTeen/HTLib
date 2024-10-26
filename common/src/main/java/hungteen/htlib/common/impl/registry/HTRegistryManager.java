package hungteen.htlib.common.impl.registry;

import com.mojang.serialization.Codec;
import hungteen.htlib.platform.HTLibPlatformAPI;
import hungteen.htlib.api.registry.HTCodecRegistry;
import hungteen.htlib.api.registry.HTCustomRegistry;
import hungteen.htlib.api.registry.SimpleEntry;
import hungteen.htlib.util.helper.JavaHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 管理已注册的 CodecRegistry <br>
 * 注意：需要在不同平台的数据包同步事件时通过 {@link HTCodecRegistry#syncToClient(ServerPlayer)} 同步数据。
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-25 21:27
 **/
public class HTRegistryManager {

    private static final Map<ResourceLocation, HTCodecRegistry<?>> CODEC_REGISTRIES = new ConcurrentHashMap<>();
    private static final Map<ResourceLocation, HTCustomRegistry<?>> CUSTOM_REGISTRIES = new ConcurrentHashMap<>();
    private static final Map<ResourceLocation, HTVanillaRegistry<?>> VANILLA_REGISTRIES = new ConcurrentHashMap<>();

    private static final class CodecFactoryHolder {
        private static final HTCodecRegistry.HTCodecRegistryFactory FACTORY = HTLibPlatformAPI.get().createCodecRegistryFactory();
    }

    private static final class CustomFactoryHolder {
        private static final HTCustomRegistry.HTCustomRegistryFactory FACTORY = HTLibPlatformAPI.get().createCustomRegistryFactory();
    }

    private static final class VanillaFactoryHolder {
        private static final HTVanillaRegistry.HTVanillaRegistryFactory FACTORY = HTLibPlatformAPI.get().createVanillaRegistryFactory();
    }

    /**
     * 获取 CodecRegistry 工厂。
     */
    public static HTCodecRegistry.HTCodecRegistryFactory codecFactory() {
        return CodecFactoryHolder.FACTORY;
    }

    /**
     * 获取 CodecRegistry 工厂。
     */
    public static HTCustomRegistry.HTCustomRegistryFactory customFactory() {
        return CustomFactoryHolder.FACTORY;
    }

    /**
     * 获取 CodecRegistry 工厂。
     */
    public static HTVanillaRegistry.HTVanillaRegistryFactory vanillaFactory() {
        return VanillaFactoryHolder.FACTORY;
    }

    /**
     * 创建一个原版的注册类型，复用原版实现跨平台。
     */
    public static <T> HTVanillaRegistry<T> vanilla(ResourceKey<? extends Registry<T>> registryKey, String modId){
        HTVanillaRegistry<T> registry = vanillaFactory().create(registryKey, modId);
        VANILLA_REGISTRIES.put(registryKey.location(), registry);
        return registry;
    }

    /**
     * 创建一个自定义的基于原版的注册类型。
     */
    public static <T> HTCustomRegistry<T> custom(Class<T> clazz, ResourceLocation registryName){
        HTCustomRegistry<T> registry = customFactory().create(clazz, registryName);
        CUSTOM_REGISTRIES.put(registryName, registry);
        return registry;
    }

    @Deprecated(since = "1.1.0")
    public static <T extends SimpleEntry> HTSimpleRegistryImpl<T> createSimple(ResourceLocation registryName){
        return simple(registryName);
    }

    /**
     * 创建一个简单的注册类型，不基于原版注册系统。
     */
    public static <T extends SimpleEntry> HTSimpleRegistryImpl<T> simple(ResourceLocation registryName){
        return new HTSimpleRegistryImpl<>(registryName);
    }

    /**
     * Do not sync.
     */
    public static <T> HTCodecRegistry<T> codec(ResourceLocation registryName, Supplier<Codec<T>> codecSup){
        return codec(registryName, codecSup, null, null, false);
    }

    /**
     * Do not sync.
     */
    public static <T> HTCodecRegistry<T> codec(ResourceLocation registryName, Supplier<Codec<T>> codecSup, boolean requireCache){
        return codec(registryName, codecSup, null, null, requireCache);
    }

    /**
     * 使用HTLib的方法同步数据。
     */
    public static <T> HTCodecRegistry<T> codec(ResourceLocation registryName, Supplier<Codec<T>> codecSup, Class<T> clazz){
        return codec(registryName, codecSup, codecSup, clazz, false);
    }

    /**
     * 使用原版的方法同步数据。
     */
    public static <T> HTCodecRegistry<T> codec(ResourceLocation registryName, Supplier<Codec<T>> codecSup, Supplier<Codec<T>> syncSup){
        return codec(registryName, codecSup, syncSup, null, false);
    }

    /**
     * Do not codec more than one registry for specific registry entityType.
     * @param syncSup 用于数据包的同步。
     * @param clazz 有clazz说明采用HTLib的方法同步数据，而非采用原版的同步方法。
     * @param requireCache 是否需要缓存数据（服务端）。
     */
    public static <T> HTCodecRegistry<T> codec(ResourceLocation registryName, Supplier<Codec<T>> codecSup, @Nullable Supplier<Codec<T>> syncSup, Class<T> clazz, boolean requireCache){
        final HTCodecRegistry<T> codecRegistry = codecFactory().create(registryName, codecSup, syncSup, clazz, requireCache);
        CODEC_REGISTRIES.put(registryName, codecRegistry);
        return codecRegistry;
    }

    public static Optional<HTVanillaRegistry<?>> getVanillaRegistry(ResourceLocation registryName){
        return JavaHelper.getOpt(VANILLA_REGISTRIES, registryName);
    }

    public static List<HTVanillaRegistry<?>> getVanillaRegistries(){
        return VANILLA_REGISTRIES.values().stream().toList();
    }

    public static Optional<HTCustomRegistry<?>> getCustomRegistry(ResourceLocation registryName){
        return JavaHelper.getOpt(CUSTOM_REGISTRIES, registryName);
    }

    public static List<HTCustomRegistry<?>> getCustomRegistries(){
        return CUSTOM_REGISTRIES.values().stream().toList();
    }

    public static Optional<HTCodecRegistry<?>> getCodecRegistry(ResourceLocation registryName){
        return JavaHelper.getOpt(CODEC_REGISTRIES, registryName);
    }

    public static List<HTCodecRegistry<?>> getCodecRegistries(){
        return CODEC_REGISTRIES.values().stream().toList();
    }

}
