package hungteen.htlib.common.codec.consumer.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.api.codec.HTEntityConsumer;
import hungteen.htlib.api.codec.HTEntityConsumerType;
import hungteen.htlib.util.helper.impl.EffectHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/11/1 7:54
 **/
public record MobEffectEntityConsumer(Holder<MobEffect> effect, int duration, int level, boolean ambient, boolean display) implements HTEntityConsumer {

    public static final MapCodec<MobEffectEntityConsumer> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            EffectHelper.get().getHolderCodec().fieldOf("effect").forGetter(MobEffectEntityConsumer::effect),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("duration").forGetter(MobEffectEntityConsumer::duration),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("level", 0).forGetter(MobEffectEntityConsumer::level),
            Codec.BOOL.optionalFieldOf("ambient", false).forGetter(MobEffectEntityConsumer::ambient),
            Codec.BOOL.optionalFieldOf("display", true).forGetter(MobEffectEntityConsumer::display)
    ).apply(instance, MobEffectEntityConsumer::new));

    @Override
    public void consume(Entity entity) {
        if(entity instanceof LivingEntity living){
            living.addEffect(EffectHelper.effect(effect(), duration(), level(), ambient, display()));
        }
    }

    @Override
    public HTEntityConsumerType<?> getType() {
        return HTLibEntityConsumerTypes.MOB_EFFECT;
    }
}
