package hungteen.htlib.common.codec.predicate.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.api.codec.HTBlockPredicate;
import hungteen.htlib.api.codec.HTBlockPredicateType;
import hungteen.htlib.util.helper.JavaHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/31 22:12
 **/
public record OrBlockPredicate(List<Holder<HTBlockPredicate>> predicates) implements HTBlockPredicate{

    public static final MapCodec<OrBlockPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            HTLibBlockPredicates.getCodec().listOf().fieldOf("predicates").forGetter(OrBlockPredicate::predicates)
    ).apply(instance, OrBlockPredicate::new));

    @Override
    public Optional<Boolean> predicate(BlockState state) {
        return JavaHelper.or(predicates, holder -> holder.value().predicate(state));
    }

    @Override
    public HTBlockPredicateType<?> getType() {
        return HTLibBlockPredicateTypes.OR;
    }
}