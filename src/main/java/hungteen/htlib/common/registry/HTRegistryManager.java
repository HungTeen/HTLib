package hungteen.htlib.common.registry;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.ISimpleEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-25 21:27
 **/
public class HTRegistryManager {

    /**
     * do not create more than one registry for specific registry entityType.
     */
    public static <T> HTRegistry<T> create(ResourceLocation registryName){
        return new HTRegistry<>(registryName);
    }

    public static <T> HTRegistry<T> create(ResourceLocation registryName, Supplier<RegistryBuilder<T>> builderSup){
        return new HTRegistry<>(registryName, builderSup);
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
     * do not create more than one registry for specific registry entityType.
     */
    public static <T> HTCodecRegistry<T> create(ResourceLocation registryName, Supplier<Codec<T>> codecSup, Supplier<Codec<T>> syncSup){
        return new HTCodecRegistry<>(registryName, codecSup, syncSup);
    }

    public static <T> HTCodecRegistry<T> create(ResourceLocation registryName, Supplier<RegistryBuilder<T>> builderSup, Supplier<Codec<T>> codecSup, Supplier<Codec<T>> syncSup){
        return new HTCodecRegistry<>(registryName, builderSup, codecSup, syncSup);
    }

}
