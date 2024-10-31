package hungteen.htlib.common.codec.predicate.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.api.codec.HTItemPredicate;
import hungteen.htlib.api.codec.HTItemPredicateType;
import hungteen.htlib.util.helper.JavaHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/31 22:12
 **/
public record AndItemPredicate(List<Holder<HTItemPredicate>> predicates) implements HTItemPredicate{

    public static final MapCodec<AndItemPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            HTLibItemPredicates.getCodec().listOf().fieldOf("predicates").forGetter(AndItemPredicate::predicates)
    ).apply(instance, AndItemPredicate::new));

    @Override
    public Optional<Boolean> predicate(ItemStack stack) {
        return JavaHelper.and(predicates, holder -> holder.value().predicate(stack));
    }

    @Override
    public HTItemPredicateType<?> getType() {
        return HTLibItemPredicateTypes.AND;
    }
}
