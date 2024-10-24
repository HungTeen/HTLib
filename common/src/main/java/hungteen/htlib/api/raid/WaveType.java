package hungteen.htlib.api.raid;

import com.mojang.serialization.MapCodec;
import hungteen.htlib.api.registry.SimpleEntry;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-27 20:21
 **/
public interface WaveType<P extends WaveComponent> extends SimpleEntry {

    /**
     * Get the method to codec wave.
     * @return Codec method.
     */
    MapCodec<P> codec();

}
