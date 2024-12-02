package hungteen.htlib.common.network.packet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.common.network.ClientPacketContext;
import hungteen.htlib.util.helper.impl.HTLibHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * @author PangTeen
 * @program HTLib
 * @create 2024/11/29 11:09
 **/
public record SpawnParticlePacket(ParticleOptions particle, Vec3 pos, Vec3 speed, int count) implements PlayToClientPacket{

    public static final CustomPacketPayload.Type<SpawnParticlePacket> TYPE = new CustomPacketPayload.Type<>(HTLibHelper.prefix("spawn_particle"));
    public static final Codec<SpawnParticlePacket> CODEC = RecordCodecBuilder.<SpawnParticlePacket>mapCodec(instance -> instance.group(
            ParticleTypes.CODEC.fieldOf("particle").forGetter(SpawnParticlePacket::particle),
            Vec3.CODEC.fieldOf("pos").forGetter(SpawnParticlePacket::pos),
            Vec3.CODEC.fieldOf("speed").forGetter(SpawnParticlePacket::speed),
            Codec.INT.optionalFieldOf("count", 1).forGetter(SpawnParticlePacket::count)
    ).apply(instance, SpawnParticlePacket::new)).codec();

    public static final StreamCodec<RegistryFriendlyByteBuf, SpawnParticlePacket> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);

    @Override
    public void process(ClientPacketContext context) {
        Level level = context.player().level();
        for(int i = 0; i < count; ++i ){
            level.addParticle(particle, pos.x, pos.y, pos.z, speed.x, speed.y, speed.z);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
