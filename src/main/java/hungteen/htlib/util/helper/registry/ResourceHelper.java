package hungteen.htlib.util.helper.registry;

import hungteen.htlib.util.helper.ForgeHelper;
import hungteen.htlib.util.helper.HTLibHelper;
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
public abstract class ResourceHelper<T> {

    /**
     * 注册类的注册名。
     */
    public abstract ResourceKey<? extends Registry<T>> resourceKey();

    /* Tag Related Methods */

    /**
     * 根据name创建tag。
     */
    public TagKey<T> tag(String name) {
        return tag(new ResourceLocation(name));
    }

    /**
     * 根据location创建tag。
     */
    public TagKey<T> tag(ResourceLocation location) {
        return TagKey.create(resourceKey(), location);
    }

    /**
     * 根据name创建ht的tag。
     */
    public TagKey<T> htTag(String name){
        return tag(HTLibHelper.prefix(name));
    }

    /**
     * 根据name创建forge的tag。
     */
    public TagKey<T> forgeTag(String name){
        return tag(ForgeHelper.get().prefix(name));
    }

    /* Common Methods */

    public HolderGetter<T> lookup(BootstapContext<?> context){
        return context.lookup(resourceKey());
    }

    public Holder<T> holder(BootstapContext<?> context, ResourceKey<T> key){
        return lookup(context).getOrThrow(key);
    }

    public HolderSet<T> holderSet(BootstapContext<?> context, TagKey<T> key){
        return lookup(context).getOrThrow(key);
    }

    /**
     * 直接创建。
     */
    public DeferredRegister<T> createRegister(String modId){
        return DeferredRegister.create(resourceKey(), modId);
    }

    public ResourceKey<T> createKey(ResourceLocation location){
        return ResourceKey.create(resourceKey(), location);
    }

    /**
     * Used by {@link RegisterEvent}.
     */
    public void register(RegisterEvent event, ResourceLocation location, Supplier<T> supplier){
        event.register(resourceKey(), location, supplier);
    }

    /**
     * easy check.
     */
    public boolean matchEvent(RegisterEvent event){
        return resourceKey().equals(event.getRegistryKey());
    }

}
