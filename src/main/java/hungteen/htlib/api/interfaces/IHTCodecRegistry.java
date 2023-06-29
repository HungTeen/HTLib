package hungteen.htlib.api.interfaces;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.RegistryFileCodec;

/**
 * A registry style after vanilla registry, 一种后于原版注册的注册方式。 <br>
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-05 09:45
 **/
public interface IHTCodecRegistry<V> extends IHTRegistry<V>{

    default Codec<Holder<V>> getHolderCodec(Codec<V> directCodec){
        return RegistryFileCodec.create(getRegistryKey(), directCodec);
    }

    default Codec<HolderSet<V>> getListCodec(Codec<V> directCodec){
        return RegistryCodecs.homogeneousList(getRegistryKey(), directCodec);
    }

}
