package hungteen.htlib.common.codec.consumer.level;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.api.codec.HTLevelConsumer;
import hungteen.htlib.api.codec.HTLevelConsumerType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.Level;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/11/1 8:21
 **/
public record ExplosionLevelConsumer(float power, Level.ExplosionInteraction interaction) implements HTLevelConsumer {

    public static final MapCodec<ExplosionLevelConsumer> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ExtraCodecs.POSITIVE_FLOAT.fieldOf("power").forGetter(ExplosionLevelConsumer::power),
            Level.ExplosionInteraction.CODEC.optionalFieldOf("interaction", Level.ExplosionInteraction.NONE).forGetter(ExplosionLevelConsumer::interaction)
    ).apply(instance, ExplosionLevelConsumer::new));

    @Override
    public void consume(ServerLevel level, BlockPos pos) {
        level.explode(null, pos.getX(), pos.getY(), pos.getZ(), power, interaction());
    }

    @Override
    public HTLevelConsumerType<?> getType() {
        return HTLibLevelConsumerTypes.EXPLOSION;
    }
}
