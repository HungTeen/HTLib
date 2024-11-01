package hungteen.htlib.common.codec.predicate.entity;

import com.mojang.serialization.MapCodec;
import hungteen.htlib.api.codec.HTEntityPredicate;
import hungteen.htlib.api.codec.HTEntityPredicateType;
import hungteen.htlib.api.registry.HTCustomRegistry;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.util.helper.impl.HTLibHelper;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/31 21:49
 **/
public interface HTLibEntityPredicateTypes {

    HTCustomRegistry<HTEntityPredicateType<?>> TYPES = HTRegistryManager.custom(HTLibHelper.prefix("entity_predicate_type"));

    HTEntityPredicateType<AndEntityPredicate> AND = register("and", AndEntityPredicate.CODEC);
    HTEntityPredicateType<OrEntityPredicate> OR = register("or", OrEntityPredicate.CODEC);
    HTEntityPredicateType<NotEntityPredicate> NOT = register("not", NotEntityPredicate.CODEC);
    HTEntityPredicateType<TypeEntityPredicate> TYPE = register("type", TypeEntityPredicate.CODEC);
    HTEntityPredicateType<TagEntityPredicate> TAG = register("tag", TagEntityPredicate.CODEC);

    static <T extends HTEntityPredicate> HTEntityPredicateType<T> register(String name, MapCodec<T> codec) {
        return registry().register(HTLibHelper.prefix(name), new EntityPredicateTypeImpl<>(codec));
    }

    static HTCustomRegistry<HTEntityPredicateType<?>> registry() {
        return TYPES;
    }

    record EntityPredicateTypeImpl<P extends HTEntityPredicate>(MapCodec<P> codec) implements HTEntityPredicateType<P> {
    }
}
