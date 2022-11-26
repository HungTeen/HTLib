package hungteen.htlib.common.registry;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-26 11:01
 **/
public class HTTypeCodec<V> implements Codec<V> {

    private static final String TYPE = "type";

    @Override
    public <T> DataResult<Pair<V, T>> decode(DynamicOps<T> ops, T input) {
        return null;
    }

    @Override
    public <T> DataResult<T> encode(V input, DynamicOps<T> ops, T prefix) {
        return null;
    }
}
