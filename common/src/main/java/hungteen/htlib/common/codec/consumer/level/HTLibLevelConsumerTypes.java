package hungteen.htlib.common.codec.consumer.level;

import com.mojang.serialization.MapCodec;
import hungteen.htlib.api.codec.HTLevelConsumer;
import hungteen.htlib.api.codec.HTLevelConsumerType;
import hungteen.htlib.api.registry.HTCustomRegistry;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.util.helper.impl.HTLibHelper;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/31 21:49
 **/
public interface HTLibLevelConsumerTypes {

    HTCustomRegistry<HTLevelConsumerType<?>> TYPES = HTRegistryManager.custom(HTLibHelper.prefix("level_consumer_type"));

    HTLevelConsumerType<ExplosionLevelConsumer> EXPLOSION = register("explosion", ExplosionLevelConsumer.CODEC);

    static <T extends HTLevelConsumer> HTLevelConsumerType<T> register(String name, MapCodec<T> codec) {
        return registry().register(HTLibHelper.prefix(name), new LevelConsumerTypeImpl<>(codec));
    }

    static HTCustomRegistry<HTLevelConsumerType<?>> registry() {
        return TYPES;
    }

    record LevelConsumerTypeImpl<P extends HTLevelConsumer>(MapCodec<P> codec) implements HTLevelConsumerType<P> {
    }
}
