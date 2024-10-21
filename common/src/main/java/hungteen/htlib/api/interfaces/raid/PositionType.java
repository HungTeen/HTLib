package hungteen.htlib.api.interfaces.raid;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.registry.SimpleEntry;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/24 10:48
 */
public interface PositionType<P extends IPositionComponent> extends SimpleEntry {

    /**
     * Get the method to codec placement.
     * @return Codec method.
     */
    Codec<P> codec();

}