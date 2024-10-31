package hungteen.htlib.common.codec.predicate.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.api.codec.HTItemPredicate;
import hungteen.htlib.api.codec.HTItemPredicateType;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/31 22:12
 **/
public record NotItemPredicate(Holder<HTItemPredicate> predicate) implements HTItemPredicate{

    public static final MapCodec<NotItemPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            HTLibItemPredicates.getCodec().fieldOf("predicate").forGetter(NotItemPredicate::predicate)
    ).apply(instance, NotItemPredicate::new));

    @Override
    public Optional<Boolean> predicate(ItemStack stack) {
        return predicate.value().predicate(stack).map(b -> !b);
    }

    @Override
    public HTItemPredicateType<?> getType() {
        return HTLibItemPredicateTypes.NOT;
    }
}
