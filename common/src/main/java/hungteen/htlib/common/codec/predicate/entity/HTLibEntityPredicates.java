package hungteen.htlib.common.codec.predicate.entity;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.codec.HTEntityPredicate;
import hungteen.htlib.api.codec.HTEntityPredicateType;
import hungteen.htlib.api.registry.HTCodecRegistry;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.util.helper.impl.HTLibHelper;
import net.minecraft.core.Holder;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/31 22:06
 **/
public interface HTLibEntityPredicates {

    /**
     * 暂时不需要同步给客户端。
     */
    HTCodecRegistry<HTEntityPredicate> PREDICATES = HTRegistryManager.codec(HTLibHelper.prefix("entity_predicate"), HTLibEntityPredicates::getDirectCodec);

    static Codec<Holder<HTEntityPredicate>> getCodec(){
        return registry().getHolderCodec(getDirectCodec());
    }

    static Codec<HTEntityPredicate> getDirectCodec(){
        return HTLibEntityPredicateTypes.registry().byNameCodec().dispatch(HTEntityPredicate::getType, HTEntityPredicateType::codec);
    }

    static HTCodecRegistry<HTEntityPredicate> registry(){
        return PREDICATES;
    }
}
