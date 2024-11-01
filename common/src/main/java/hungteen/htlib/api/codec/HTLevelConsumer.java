package hungteen.htlib.api.codec;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/31 21:39
 **/
public interface HTLevelConsumer {

    /**
     * @param level The level to consume.
     */
    void consume(ServerLevel level, BlockPos pos);

    /**
     * Use with {@link HTLevelConsumerType#codec()} to specify the level consumer.
     * @return type.
     */
    HTLevelConsumerType<?> getType();

}
