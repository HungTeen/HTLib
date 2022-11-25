package hungteen.htlib.common.network;

import hungteen.htlib.HTLib;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-09-29 22:26
 **/
public class NetworkHandler {

    private static int id = 0;

    private static SimpleChannel CHANNEL;

    public static void init() {

        CHANNEL = NetworkRegistry.ChannelBuilder
                .named(HTLib.prefix("networking"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        CHANNEL.registerMessage(getId(), PlaySoundPacket.class, PlaySoundPacket::encode, PlaySoundPacket::new, PlaySoundPacket.Handler::onMessage);
        CHANNEL.registerMessage(getId(), SpawnParticlePacket.class, SpawnParticlePacket::encode, SpawnParticlePacket::new, SpawnParticlePacket.Handler::onMessage);
//        CHANNEL.registerMessage(id ++, DatapackPacket.class, DatapackPacket::encode, DatapackPacket::new, DatapackPacket.Handler::onMessage);

    }

    public static <MSG> void sendToServer(MSG msg){
        CHANNEL.sendToServer(msg);
    }

    public static <MSG> void sendToClient(ServerPlayer serverPlayer, MSG msg){
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), msg);
    }

    public static <MSG> void sendToNearByClient(Level world, Vec3 vec, double dis, MSG msg){
        CHANNEL.send(PacketDistributor.NEAR.with(() -> {
            return new PacketDistributor.TargetPoint(vec.x, vec.y, vec.z, dis, world.dimension());
        }), msg);
    }

    private static int getId(){
        return id ++;
    }
}
