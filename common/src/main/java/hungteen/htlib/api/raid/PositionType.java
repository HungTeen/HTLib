package hungteen.htlib.api.raid;

import com.mojang.serialization.MapCodec;
import hungteen.htlib.api.registry.SimpleEntry;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/24 10:48
 */
public interface PositionType<P extends PositionComponent> extends SimpleEntry {

    /**
     * Get the method to codec placement.
     * @return Codec method.
     */
    MapCodec<P> codec();

}