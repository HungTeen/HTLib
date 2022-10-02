package hungteen.htlib.network;

import hungteen.htlib.HTLib;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-09-29 22:32
 **/
public class SpawnParticlePacket {

    private String type;
    private double x;
    private double y;
    private double z;

    public SpawnParticlePacket(String type, double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
    }

    public SpawnParticlePacket(FriendlyByteBuf buffer) {
        this.type = buffer.readUtf();
        this.x = buffer.readDouble();
        this.y = buffer.readDouble();
        this.z = buffer.readDouble();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.type);
        buffer.writeDouble(x);
        buffer.writeDouble(y);
        buffer.writeDouble(z);
    }

    public static class Handler {

        public static void onMessage(SpawnParticlePacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(()->{
                ParticleType<?> particle = ForgeRegistries.PARTICLE_TYPES.getValue(new ResourceLocation(message.type));
                if(particle instanceof SimpleParticleType){
                    HTLib.PROXY.getPlayer().level.addParticle(((SimpleParticleType) particle).getType(), message.x, message.y, message.z, 0, 0, 0);
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }

}
