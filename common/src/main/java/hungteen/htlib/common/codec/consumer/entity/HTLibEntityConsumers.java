package hungteen.htlib.common.codec.consumer.entity;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.codec.HTEntityConsumer;
import hungteen.htlib.api.codec.HTEntityConsumerType;
import hungteen.htlib.api.registry.HTCodecRegistry;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.util.helper.impl.HTLibHelper;
import net.minecraft.core.Holder;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/31 22:06
 **/
public interface HTLibEntityConsumers {

    /**
     * 暂时不需要同步给客户端。
     */
    HTCodecRegistry<HTEntityConsumer> CONSUMERS = HTRegistryManager.codec(HTLibHelper.prefix("entity_consumer"), HTLibEntityConsumers::getDirectCodec);

    static Codec<Holder<HTEntityConsumer>> getCodec(){
        return registry().getHolderCodec(getDirectCodec());
    }

    static Codec<HTEntityConsumer> getDirectCodec(){
        return HTLibEntityConsumerTypes.registry().byNameCodec().dispatch(HTEntityConsumer::getType, HTEntityConsumerType::codec);
    }

    static HTCodecRegistry<HTEntityConsumer> registry(){
        return CONSUMERS;
    }
}
