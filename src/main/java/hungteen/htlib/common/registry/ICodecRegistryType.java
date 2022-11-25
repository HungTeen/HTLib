package hungteen.htlib.common.registry;

import com.mojang.serialization.Codec;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/24 16:27
 */
public interface ICodecRegistryType<P> {

    Codec<P> codec();

}
