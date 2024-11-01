package hungteen.htlib.common.codec.consumer.level;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.codec.HTLevelConsumer;
import hungteen.htlib.api.codec.HTLevelConsumerType;
import hungteen.htlib.api.registry.HTCodecRegistry;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.util.helper.impl.HTLibHelper;
import net.minecraft.core.Holder;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/31 22:06
 **/
public interface HTLibLevelConsumers {

    /**
     * 暂时不需要同步给客户端。
     */
    HTCodecRegistry<HTLevelConsumer> CONSUMERS = HTRegistryManager.codec(HTLibHelper.prefix("level_consumer"), HTLibLevelConsumers::getDirectCodec);

    static Codec<Holder<HTLevelConsumer>> getCodec(){
        return registry().getHolderCodec(getDirectCodec());
    }

    static Codec<HTLevelConsumer> getDirectCodec(){
        return HTLibLevelConsumerTypes.registry().byNameCodec().dispatch(HTLevelConsumer::getType, HTLevelConsumerType::codec);
    }

    static HTCodecRegistry<HTLevelConsumer> registry(){
        return CONSUMERS;
    }
}
