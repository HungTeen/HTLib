package hungteen.htlib.common.codec.predicate.block;

import com.mojang.serialization.MapCodec;
import hungteen.htlib.api.codec.HTBlockPredicate;
import hungteen.htlib.api.codec.HTBlockPredicateType;
import hungteen.htlib.api.registry.HTCustomRegistry;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.util.helper.impl.HTLibHelper;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/31 21:49
 **/
public interface HTLibBlockPredicateTypes {

    HTCustomRegistry<HTBlockPredicateType<?>> TYPES = HTRegistryManager.custom(HTLibHelper.prefix("block_predicate_type"));

    HTBlockPredicateType<AndBlockPredicate> AND = register("and", AndBlockPredicate.CODEC);
    HTBlockPredicateType<OrBlockPredicate> OR = register("or", OrBlockPredicate.CODEC);
    HTBlockPredicateType<NotBlockPredicate> NOT = register("not", NotBlockPredicate.CODEC);
    HTBlockPredicateType<TypeBlockPredicate> TYPE = register("type", TypeBlockPredicate.CODEC);
    HTBlockPredicateType<TagBlockPredicate> TAG = register("tag", TagBlockPredicate.CODEC);

    static <T extends HTBlockPredicate> HTBlockPredicateType<T> register(String name, MapCodec<T> codec) {
        return registry().register(HTLibHelper.prefix(name), new BlockPredicateTypeImpl<>(codec));
    }

    static HTCustomRegistry<HTBlockPredicateType<?>> registry() {
        return TYPES;
    }

    record BlockPredicateTypeImpl<P extends HTBlockPredicate>(MapCodec<P> codec) implements HTBlockPredicateType<P> {
    }
}
