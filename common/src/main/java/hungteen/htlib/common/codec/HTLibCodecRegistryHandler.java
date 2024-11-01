package hungteen.htlib.common.codec;

import hungteen.htlib.common.codec.consumer.entity.HTLibEntityConsumerTypes;
import hungteen.htlib.common.codec.consumer.entity.HTLibEntityConsumers;
import hungteen.htlib.common.codec.consumer.level.HTLibLevelConsumerTypes;
import hungteen.htlib.common.codec.consumer.level.HTLibLevelConsumers;
import hungteen.htlib.common.codec.predicate.block.HTLibBlockPredicateTypes;
import hungteen.htlib.common.codec.predicate.block.HTLibBlockPredicates;
import hungteen.htlib.common.codec.predicate.entity.HTLibEntityPredicateTypes;
import hungteen.htlib.common.codec.predicate.entity.HTLibEntityPredicates;
import hungteen.htlib.common.codec.predicate.item.HTLibItemPredicateTypes;
import hungteen.htlib.common.codec.predicate.item.HTLibItemPredicates;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/11/1 8:45
 **/
public interface HTLibCodecRegistryHandler {

    static void initialize() {
        HTLibEntityPredicateTypes.registry().initialize();
        HTLibEntityPredicates.registry().initialize();

        HTLibItemPredicateTypes.registry().initialize();
        HTLibItemPredicates.registry().initialize();

        HTLibBlockPredicateTypes.registry().initialize();
        HTLibBlockPredicates.registry().initialize();

        HTLibEntityConsumerTypes.registry().initialize();
        HTLibEntityConsumers.registry().initialize();

        HTLibLevelConsumerTypes.registry().initialize();
        HTLibLevelConsumers.registry().initialize();
    }
}
