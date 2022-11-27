package hungteen.htlib.util.interfaces;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.ISimpleRegistry;
import hungteen.htlib.common.world.raid.WaveComponent;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-27 20:21
 **/
public interface IWaveComponentType<P extends WaveComponent> extends ISimpleRegistry {

    /**
     * Get the method to codec wave.
     * @return codec method.
     */
    Codec<P> codec();

}
