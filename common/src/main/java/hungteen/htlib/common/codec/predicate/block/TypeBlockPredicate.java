package hungteen.htlib.common.codec.predicate.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.api.codec.HTBlockPredicate;
import hungteen.htlib.api.codec.HTBlockPredicateType;
import hungteen.htlib.util.helper.impl.BlockHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/31 22:48
 **/
public record TypeBlockPredicate(Block block) implements HTBlockPredicate {

    public static final MapCodec<TypeBlockPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BlockHelper.get().getCodec().fieldOf("block").forGetter(TypeBlockPredicate::block)
    ).apply(instance, TypeBlockPredicate::new));

    @Override
    public Optional<Boolean> predicate(BlockState state) {
        return Optional.of(state.is(block()));
    }

    @Override
    public HTBlockPredicateType<?> getType() {
        return HTLibBlockPredicateTypes.TYPE;
    }
}
