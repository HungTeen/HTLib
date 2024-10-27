package hungteen.htlib.common.impl.registry;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.registry.HTCodecRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DataPackRegistryEvent;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/6/28 11:10
 */
public class HTForgeCodecRegistryImpl<V> extends HTCodecRegistryImpl<V> implements HTCodecRegistry<V> {

    /**
     * @param registryName 注册名，决定了数据的路径。
     * @param codecSup 序列化格式。
     * @param syncSup 同步格式。
     * @param registryClass 数据类。
     */
    public HTForgeCodecRegistryImpl(ResourceLocation registryName, Supplier<Codec<V>> codecSup, @Nullable Supplier<Codec<V>> syncSup, @Nullable Class<V> registryClass, boolean requireCache) {
        super(registryName, codecSup, syncSup, registryClass, requireCache);
    }

    /**
     * Codec<Holder<T>> 不适用于原版的同步方法，故自己绕过。
     */
    public void addRegistry(DataPackRegistryEvent.NewRegistry event){
        if(this.defaultSync()){
            event.dataPackRegistry(this.getRegistryKey(), this.codecSup.get(), this.syncSup.get());
        } else {
            event.dataPackRegistry(this.getRegistryKey(), this.codecSup.get());
        }
    }

}
