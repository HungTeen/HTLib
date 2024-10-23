package hungteen.htlib.api.interfaces.raid;

import com.mojang.serialization.MapCodec;
import hungteen.htlib.api.registry.SimpleEntry;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-27 20:48
 **/
public interface RaidType<P extends IRaidComponent> extends SimpleEntry {

    /**
     * Get the method to codec raid.
     * @return Codec method.
     */
    MapCodec<P> codec();

}
