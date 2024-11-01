package hungteen.htlib.api.codec;

import net.minecraft.world.entity.Entity;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/31 21:39
 **/
public interface HTEntityConsumer {

    /**
     * @param entity The entity to consume.
     */
    void consume(Entity entity);

    /**
     * Use with {@link HTEntityConsumerType#codec()} to specify the entity consumer.
     * @return type.
     */
    HTEntityConsumerType<?> getType();

}
