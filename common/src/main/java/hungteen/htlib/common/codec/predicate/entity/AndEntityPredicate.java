package hungteen.htlib.common.codec.predicate.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.api.codec.HTEntityPredicate;
import hungteen.htlib.api.codec.HTEntityPredicateType;
import hungteen.htlib.util.helper.JavaHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.Optional;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/31 22:12
 **/
public record AndEntityPredicate(List<Holder<HTEntityPredicate>> predicates) implements HTEntityPredicate{

    public static final MapCodec<AndEntityPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            HTLibEntityPredicates.getCodec().listOf().fieldOf("predicates").forGetter(AndEntityPredicate::predicates)
    ).apply(instance, AndEntityPredicate::new));

    @Override
    public Optional<Boolean> predicate(Entity entity) {
        return JavaHelper.and(predicates, holder -> holder.value().predicate(entity));
    }

    @Override
    public HTEntityPredicateType<?> getType() {
        return HTLibEntityPredicateTypes.AND;
    }
}
