package hungteen.htlib.util.interfaces;

import com.mojang.serialization.Codec;
import hungteen.htlib.common.world.raid.RaidComponent;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-27 20:48
 **/
public interface IRaidComponentType<P extends RaidComponent> {

    /**
     * Get the method to codec raid.
     * @return codec method.
     */
    Codec<P> codec();

}
