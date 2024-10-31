package hungteen.htlib.common.codec.predicate.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.api.codec.HTItemPredicate;
import hungteen.htlib.api.codec.HTItemPredicateType;
import hungteen.htlib.util.helper.impl.ItemHelper;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/31 22:48
 **/
public record TagItemPredicate(TagKey<Item> tag) implements HTItemPredicate {

    public static final MapCodec<TagItemPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            TagKey.hashedCodec(ItemHelper.get().resourceKey()).fieldOf("tag").forGetter(TagItemPredicate::tag)
    ).apply(instance, TagItemPredicate::new));

    @Override
    public Optional<Boolean> predicate(ItemStack stack) {
        return Optional.of(stack.is(tag()));
    }

    @Override
    public HTItemPredicateType<?> getType() {
        return HTLibItemPredicateTypes.TAG;
    }
}
