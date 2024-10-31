package hungteen.htlib.common.codec.predicate.item;

import com.mojang.serialization.MapCodec;
import hungteen.htlib.api.codec.HTItemPredicate;
import hungteen.htlib.api.codec.HTItemPredicateType;
import hungteen.htlib.api.registry.HTCustomRegistry;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.util.helper.impl.HTLibHelper;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/31 21:49
 **/
public interface HTLibItemPredicateTypes {

    HTCustomRegistry<HTItemPredicateType<?>> TYPES = HTRegistryManager.custom(HTLibHelper.prefix("item_predicate_type"));

    HTItemPredicateType<AndItemPredicate> AND = register("and", AndItemPredicate.CODEC);
    HTItemPredicateType<OrItemPredicate> OR = register("or", OrItemPredicate.CODEC);
    HTItemPredicateType<NotItemPredicate> NOT = register("not", NotItemPredicate.CODEC);
    HTItemPredicateType<TagItemPredicate> TAG = register("tag", TagItemPredicate.CODEC);

    static <T extends HTItemPredicate> HTItemPredicateType<T> register(String name, MapCodec<T> codec) {
        return registry().register(HTLibHelper.prefix(name), new PositionTypeImpl<>(codec));
    }

    static HTCustomRegistry<HTItemPredicateType<?>> registry() {
        return TYPES;
    }

    record PositionTypeImpl<P extends HTItemPredicate>(MapCodec<P> codec) implements HTItemPredicateType<P> {
    }
}
