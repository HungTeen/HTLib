package hungteen.htlib.common.codec.predicate.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.api.codec.HTBlockPredicate;
import hungteen.htlib.api.codec.HTBlockPredicateType;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/31 22:12
 **/
public record NotBlockPredicate(Holder<HTBlockPredicate> predicate) implements HTBlockPredicate{

    public static final MapCodec<NotBlockPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            HTLibBlockPredicates.getCodec().fieldOf("predicate").forGetter(NotBlockPredicate::predicate)
    ).apply(instance, NotBlockPredicate::new));

    @Override
    public Optional<Boolean> predicate(BlockState state) {
        return predicate.value().predicate(state).map(b -> !b);
    }

    @Override
    public HTBlockPredicateType<?> getType() {
        return HTLibBlockPredicateTypes.NOT;
    }
}
