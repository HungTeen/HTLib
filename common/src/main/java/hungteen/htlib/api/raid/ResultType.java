package hungteen.htlib.api.raid;

import com.mojang.serialization.MapCodec;
import hungteen.htlib.api.registry.SimpleEntry;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/24 10:48
 */
public interface ResultType<P extends ResultComponent> extends SimpleEntry {

    /**
     * Get Codec.
     * @return Codec.
     */
    MapCodec<P> codec();

}