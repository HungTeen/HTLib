package hungteen.htlib.api.codec;

import net.minecraft.world.entity.Entity;

import java.util.Optional;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/31 21:39
 **/
public interface HTEntityPredicate {

    /**
     * 判断实体是否符合条件，
     * @return 如果符合返回 true，否则返回 false，如果无法判断返回 {@link Optional#empty()}。
     */
    Optional<Boolean> predicate(Entity entity);

    /**
     * Use with {@link HTEntityPredicateType#codec()} to specify the type of predicate.
     * @return type.
     */
    HTEntityPredicateType<?> getType();

}
