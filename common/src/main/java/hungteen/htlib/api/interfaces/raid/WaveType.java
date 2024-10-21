package hungteen.htlib.api.interfaces.raid;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.registry.SimpleEntry;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-27 20:21
 **/
public interface WaveType<P extends IWaveComponent> extends SimpleEntry {

    /**
     * Get the method to codec wave.
     * @return Codec method.
     */
    Codec<P> codec();

}
