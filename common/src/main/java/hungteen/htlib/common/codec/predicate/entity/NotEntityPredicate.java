package hungteen.htlib.common.codec.predicate.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.api.codec.HTEntityPredicate;
import hungteen.htlib.api.codec.HTEntityPredicateType;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;

import java.util.Optional;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/31 22:12
 **/
public record NotEntityPredicate(Holder<HTEntityPredicate> predicate) implements HTEntityPredicate{

    public static final MapCodec<NotEntityPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            HTLibEntityPredicates.getCodec().fieldOf("predicate").forGetter(NotEntityPredicate::predicate)
    ).apply(instance, NotEntityPredicate::new));

    @Override
    public Optional<Boolean> predicate(Entity entity) {
        return predicate.value().predicate(entity).map(b -> !b);
    }

    @Override
    public HTEntityPredicateType<?> getType() {
        return HTLibEntityPredicateTypes.NOT;
    }
}
