package hungteen.htlib.common.codec.predicate.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.api.codec.HTEntityPredicate;
import hungteen.htlib.api.codec.HTEntityPredicateType;
import hungteen.htlib.util.helper.impl.EntityHelper;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.Optional;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/31 22:48
 **/
public record TagEntityPredicate(TagKey<EntityType<?>> tag) implements HTEntityPredicate {

    public static final MapCodec<TagEntityPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            TagKey.hashedCodec(EntityHelper.get().resourceKey()).fieldOf("tag").forGetter(TagEntityPredicate::tag)
    ).apply(instance, TagEntityPredicate::new));

    @Override
    public Optional<Boolean> predicate(Entity entity) {
        return Optional.of(entity.getType().is(tag()));
    }

    @Override
    public HTEntityPredicateType<?> getType() {
        return HTLibEntityPredicateTypes.TAG;
    }
}
