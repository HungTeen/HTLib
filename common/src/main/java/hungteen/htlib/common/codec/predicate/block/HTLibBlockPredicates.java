package hungteen.htlib.common.codec.predicate.block;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.codec.HTBlockPredicate;
import hungteen.htlib.api.codec.HTBlockPredicateType;
import hungteen.htlib.api.registry.HTCodecRegistry;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.util.helper.impl.HTLibHelper;
import net.minecraft.core.Holder;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/31 22:06
 **/
public interface HTLibBlockPredicates {

    /**
     * 暂时不需要同步给客户端。
     */
    HTCodecRegistry<HTBlockPredicate> PREDICATES = HTRegistryManager.codec(HTLibHelper.prefix("block_predicate"), HTLibBlockPredicates::getDirectCodec);

    static Codec<Holder<HTBlockPredicate>> getCodec(){
        return registry().getHolderCodec(getDirectCodec());
    }

    static Codec<HTBlockPredicate> getDirectCodec(){
        return HTLibBlockPredicateTypes.registry().byNameCodec().dispatch(HTBlockPredicate::getType, HTBlockPredicateType::codec);
    }

    static HTCodecRegistry<HTBlockPredicate> registry(){
        return PREDICATES;
    }
}
