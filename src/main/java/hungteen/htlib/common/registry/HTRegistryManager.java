package hungteen.htlib.common.registry;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.ISimpleEntry;
import net.minecraft.resources.ResourceLocation;

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

    /**
     * do not create more than one registry for specific registry entityType.
     */
    public static <T extends ISimpleEntry> HTSimpleRegistry<T> createSimple(ResourceLocation registryName){
        return new HTSimpleRegistry<>(registryName);
    }

    /**
     * do not create more than one registry for specific registry entityType.
     */
    public static <T> HTCodecRegistry<T> create(ResourceLocation registryName, Supplier<Codec<T>> codecSup, Supplier<Codec<T>> syncSup){
        return new HTCodecRegistry<>(registryName, codecSup, syncSup);
    }

}
