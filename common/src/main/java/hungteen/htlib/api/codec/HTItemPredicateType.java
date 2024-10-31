package hungteen.htlib.api.codec;

import com.mojang.serialization.MapCodec;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/31 21:41
 **/
public interface HTItemPredicateType<P extends HTItemPredicate> {

    /**
     * @return Codec method.
     */
    MapCodec<P> codec();
}
