package hungteen.htlib.api.interfaces.raid;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.ISimpleEntry;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-27 16:56
 **/
public interface ISpawnType<P extends ISpawnComponent> extends ISimpleEntry {

    /**
     * Get the method to codec spawn component.
     * @return Spawn codec.
     */
    Codec<P> codec();

}
