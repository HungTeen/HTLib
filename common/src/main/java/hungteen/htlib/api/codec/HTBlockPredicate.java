package hungteen.htlib.api.codec;

import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/31 21:39
 **/
public interface HTBlockPredicate {

    /**
     * 判断方块是否符合条件，
     * @return 如果符合返回 true，否则返回 false，如果无法判断返回 {@link Optional#empty()}。
     */
    Optional<Boolean> predicate(BlockState state);

    /**
     * Use with {@link HTBlockPredicateType#codec()} to specify the block predicate.
     * @return type.
     */
    HTBlockPredicateType<?> getType();

}
