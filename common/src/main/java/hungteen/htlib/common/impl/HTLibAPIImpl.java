package hungteen.htlib.common.impl;

import hungteen.htlib.api.HTLibAPI;
import hungteen.htlib.api.codec.*;
import hungteen.htlib.api.registry.HTCodecRegistry;
import hungteen.htlib.api.registry.HTCustomRegistry;
import hungteen.htlib.common.codec.predicate.block.HTLibBlockPredicateTypes;
import hungteen.htlib.common.codec.predicate.block.HTLibBlockPredicates;
import hungteen.htlib.common.codec.predicate.entity.HTLibEntityPredicateTypes;
import hungteen.htlib.common.codec.predicate.entity.HTLibEntityPredicates;
import hungteen.htlib.common.codec.predicate.item.HTLibItemPredicateTypes;
import hungteen.htlib.common.codec.predicate.item.HTLibItemPredicates;

import java.util.Optional;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/22 9:02
 */
public class HTLibAPIImpl implements HTLibAPI {

    @Override
    public int apiVersion() {
        return 1;
    }

    @Override
    public Optional<HTCodecRegistry<HTEntityPredicate>> entityPredicateRegistry() {
        return Optional.ofNullable(HTLibEntityPredicates.registry());
    }

    @Override
    public Optional<HTCustomRegistry<HTEntityPredicateType<?>>> entityPredicateTypeRegistry() {
        return Optional.ofNullable(HTLibEntityPredicateTypes.registry());
    }

    @Override
    public Optional<HTCodecRegistry<HTItemPredicate>> itemPredicateRegistry() {
        return Optional.ofNullable(HTLibItemPredicates.registry());
    }

    @Override
    public Optional<HTCustomRegistry<HTItemPredicateType<?>>> itemPredicateTypeRegistry() {
        return Optional.ofNullable(HTLibItemPredicateTypes.registry());
    }

    @Override
    public Optional<HTCodecRegistry<HTBlockPredicate>> blockPredicateRegistry() {
        return Optional.ofNullable(HTLibBlockPredicates.registry());
    }

    @Override
    public Optional<HTCustomRegistry<HTBlockPredicateType<?>>> blockPredicateTypeRegistry() {
        return Optional.ofNullable(HTLibBlockPredicateTypes.registry());
    }
}
