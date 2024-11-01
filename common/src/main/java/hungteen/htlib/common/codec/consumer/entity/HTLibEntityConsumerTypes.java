package hungteen.htlib.common.codec.consumer.entity;

import com.mojang.serialization.MapCodec;
import hungteen.htlib.api.codec.HTEntityConsumer;
import hungteen.htlib.api.codec.HTEntityConsumerType;
import hungteen.htlib.api.registry.HTCustomRegistry;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.util.helper.impl.HTLibHelper;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/31 21:49
 **/
public interface HTLibEntityConsumerTypes {

    HTCustomRegistry<HTEntityConsumerType<?>> TYPES = HTRegistryManager.custom(HTLibHelper.prefix("entity_consumer_type"));

    HTEntityConsumerType<MobEffectEntityConsumer> MOB_EFFECT = register("mob_effect", MobEffectEntityConsumer.CODEC);

    static <T extends HTEntityConsumer> HTEntityConsumerType<T> register(String name, MapCodec<T> codec) {
        return registry().register(HTLibHelper.prefix(name), new EntityConsumerTypeImpl<>(codec));
    }

    static HTCustomRegistry<HTEntityConsumerType<?>> registry() {
        return TYPES;
    }

    record EntityConsumerTypeImpl<P extends HTEntityConsumer>(MapCodec<P> codec) implements HTEntityConsumerType<P> {
    }
}
