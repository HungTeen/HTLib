package hungteen.htlib.api.codec;

import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/31 21:39
 **/
public interface HTItemPredicate {

    /**
     * 判断物品是否符合条件，
     * @return 如果符合返回 true，否则返回 false，如果无法判断返回 {@link Optional#empty()}。
     */
    Optional<Boolean> predicate(ItemStack stack);

    /**
     * Use with {@link HTItemPredicateType#codec()} to specify the type of predicate.
     * @return type.
     */
    HTItemPredicateType<?> getType();

}
