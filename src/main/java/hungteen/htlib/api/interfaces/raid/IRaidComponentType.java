package hungteen.htlib.api.interfaces.raid;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.ISimpleEntry;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-27 20:48
 **/
public interface IRaidComponentType<P extends IRaidComponent> extends ISimpleEntry {

    /**
     * Get the method to codec raid.
     * @return Codec method.
     */
    Codec<P> codec();

}
