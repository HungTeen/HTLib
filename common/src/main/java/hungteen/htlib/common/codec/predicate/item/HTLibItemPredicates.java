package hungteen.htlib.common.codec.predicate.item;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.codec.HTItemPredicate;
import hungteen.htlib.api.codec.HTItemPredicateType;
import hungteen.htlib.api.registry.HTCodecRegistry;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.util.helper.impl.HTLibHelper;
import net.minecraft.core.Holder;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/31 22:06
 **/
public interface HTLibItemPredicates {

    /**
     * 暂时不需要同步给客户端。
     */
    HTCodecRegistry<HTItemPredicate> PREDICATES = HTRegistryManager.codec(HTLibHelper.prefix("item_predicate"), HTLibItemPredicates::getDirectCodec);

    static Codec<Holder<HTItemPredicate>> getCodec(){
        return registry().getHolderCodec(getDirectCodec());
    }

    static Codec<HTItemPredicate> getDirectCodec(){
        return HTLibItemPredicateTypes.registry().byNameCodec().dispatch(HTItemPredicate::getType, HTItemPredicateType::codec);
    }

    static HTCodecRegistry<HTItemPredicate> registry(){
        return PREDICATES;
    }
}
