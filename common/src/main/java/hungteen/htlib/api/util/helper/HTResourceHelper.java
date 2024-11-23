package hungteen.htlib.api.util.helper;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.util.Platform;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

/**
 * 并不是所有的注册都提供了 Registry，如果只有 ResourceKey 就用此帮助类。
 * @author PangTeen
 * @program HTLib
 * @data 2023/3/29 19:34
 */
public interface HTResourceHelper<T> {

    /**
     * 注册类的注册名。
     */
    ResourceKey<? extends Registry<T>> resourceKey();

    /* Tag Related Methods */

    /**
     * @return 创建 minecraft:name。
     */
    default TagKey<T> vanillaTag(String name) {
        return tag(Platform.MINECRAFT.getNamespace(), name);
    }

    /**
     * @return 创建 neoforge:name。
     */
    default TagKey<T> neoTag(String name) {
        return tag(Platform.NEOFORGE.getNamespace(), name);
    }

    /**
     * @return 创建 fabric:name。
     */
    default TagKey<T> fabricTag(String name) {
        return tag(Platform.FABRIC.getNamespace(), name);
    }

    /**
     * @return 创建 forge:name。
     */
    default TagKey<T> forgeTag(String name) {
        return tag(Platform.FORGE.getNamespace(), name);
    }

    /**
     * @return 创建 c:name。
     */
    default TagKey<T> uniformTag(String name) {
        return tag(Platform.UNIFORM.getNamespace(), name);
    }

    /**
     * @return 根据 name 创建 tag。
     */
    default TagKey<T> tag(String name) {
        return tag(ResourceLocation.parse(name));
    }

    /**
     * @return 根据 modId 和 name 创建 tag。
     */
    default TagKey<T> tag(String modId, String name) {
        return tag(ResourceLocation.fromNamespaceAndPath(modId, name));
    }

    /**
     * @return 根据 location 创建 tag。
     */
    default TagKey<T> tag(ResourceLocation location) {
        return TagKey.create(resourceKey(), location);
    }

    /* Common Methods */

    default HolderGetter<T> lookup(BootstrapContext<?> context){
        return context.lookup(resourceKey());
    }

    default Holder<T> holder(BootstrapContext<?> context, ResourceKey<T> key){
        return context.lookup(resourceKey()).getOrThrow(key);
    }

    default HolderSet<T> holderSet(BootstrapContext<?> context, TagKey<T> key){
        return context.lookup(resourceKey()).getOrThrow(key);
    }

    default ResourceKey<T> createKey(ResourceLocation location){
        return ResourceKey.create(resourceKey(), location);
    }

    /* Codec Methods */

    default StreamCodec<RegistryFriendlyByteBuf, Holder<T>> getStreamCodec() {
        return ByteBufCodecs.holderRegistry(resourceKey());
    }

    default Codec<TagKey<T>> getTagCodec() {
        return TagKey.codec(resourceKey());
    }

}
