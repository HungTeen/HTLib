package hungteen.htlib.api.interfaces;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegisterEvent;

import java.util.function.Supplier;

/**
 * 并不是所有的注册都提供了Registry，如果只有ResourceKey就用此Helper。 <br>
 * @author PangTeen
 * @program HTLib
 * @data 2023/3/29 19:34
 */
public interface IHTResourceHelper<T> {

    /**
     * 注册类的注册名。
     */
    ResourceKey<? extends Registry<T>> resourceKey();

    /* Tag Related Methods */

    /**
     * 根据name创建tag。
     */
    default TagKey<T> tag(String name) {
        return tag(new ResourceLocation(name));
    }

    /**
     * 根据location创建tag。
     */
    default TagKey<T> tag(ResourceLocation location) {
        return TagKey.create(resourceKey(), location);
    }

    /* Common Methods */

    default HolderGetter<T> lookup(BootstapContext<?> context){
        return context.lookup(resourceKey());
    }

    default Holder<T> holder(BootstapContext<?> context, ResourceKey<T> key){
        return context.lookup(resourceKey()).getOrThrow(key);
    }

    default HolderSet<T> holderSet(BootstapContext<?> context, TagKey<T> key){
        return context.lookup(resourceKey()).getOrThrow(key);
    }

    /**
     * 直接创建。
     */
    default DeferredRegister<T> createRegister(String modId){
        return DeferredRegister.create(resourceKey(), modId);
    }

    default ResourceKey<T> createKey(ResourceLocation location){
        return ResourceKey.create(resourceKey(), location);
    }

    /**
     * Used by {@link RegisterEvent}.
     */
    default void register(RegisterEvent event, ResourceLocation location, Supplier<T> supplier){
        event.register(resourceKey(), location, supplier);
    }

    /**
     * easy check.
     */
    default boolean matchEvent(RegisterEvent event){
        return resourceKey().equals(event.getRegistryKey());
    }

}
