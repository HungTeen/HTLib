package hungteen.htlib.api.interfaces.raid;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.ISimpleEntry;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-27 20:21
 **/
public interface IWaveComponentType<P extends IWaveComponent> extends ISimpleEntry {

    /**
     * Get the method to codec wave.
     * @return Codec method.
     */
    Codec<P> codec();

}
