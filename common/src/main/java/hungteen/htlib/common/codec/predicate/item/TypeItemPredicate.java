package hungteen.htlib.common.codec.predicate.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.api.codec.HTItemPredicate;
import hungteen.htlib.api.codec.HTItemPredicateType;
import hungteen.htlib.util.helper.impl.ItemHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/31 22:48
 **/
public record TypeItemPredicate(Item item) implements HTItemPredicate {

    public static final MapCodec<TypeItemPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemHelper.get().getCodec().fieldOf("item").forGetter(TypeItemPredicate::item)
    ).apply(instance, TypeItemPredicate::new));

    @Override
    public Optional<Boolean> predicate(ItemStack stack) {
        return Optional.of(stack.is(item()));
    }

    @Override
    public HTItemPredicateType<?> getType() {
        return HTLibItemPredicateTypes.TYPE;
    }
}
