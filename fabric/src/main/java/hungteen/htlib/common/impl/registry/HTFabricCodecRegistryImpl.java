package hungteen.htlib.common.impl.registry;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.registry.HTCodecRegistry;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/6/28 11:10
 */
public class HTFabricCodecRegistryImpl<V> extends HTCodecRegistryImpl<V> implements HTCodecRegistry<V> {

    /**
     * @param registryName 注册名，决定了数据的路径。
     * @param codecSup 序列化格式。
     * @param syncSup 同步格式。
     * @param registryClass 数据类。
     */
    public HTFabricCodecRegistryImpl(ResourceLocation registryName, Supplier<Codec<V>> codecSup, @Nullable Supplier<Codec<V>> syncSup, @Nullable Class<V> registryClass, boolean requireCache) {
        super(registryName, codecSup, syncSup, registryClass, requireCache);
    }

    /**
     * Codec<Holder<T>> 不适用于原版的同步方法，故自己绕过。
     */
    public void addRegistry(){
        if(this.defaultSync()){
            DynamicRegistries.registerSynced(this.getRegistryKey(), this.codecSup.get(), this.syncSup.get(), DynamicRegistries.SyncOption.SKIP_WHEN_EMPTY);
        } else {
            DynamicRegistries.register(this.getRegistryKey(), this.codecSup.get());
        }
    }

}
