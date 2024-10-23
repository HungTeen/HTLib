package hungteen.htlib.api.interfaces;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BootstrapContext;
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
     * 根据 name 创建 tag。
     */
    default TagKey<T> tag(String name) {
        return tag(ResourceLocation.parse(name));
    }

    /**
     * 根据 location 创建 tag。
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

}
