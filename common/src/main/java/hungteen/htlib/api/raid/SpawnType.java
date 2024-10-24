package hungteen.htlib.api.raid;

import com.mojang.serialization.MapCodec;
import hungteen.htlib.api.registry.SimpleEntry;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-27 16:56
 **/
public interface SpawnType<P extends SpawnComponent> extends SimpleEntry {

    /**
     * Get the method to codec spawn component.
     * @return Spawn codec.
     */
    MapCodec<P> codec();

}
