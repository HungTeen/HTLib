package hungteen.htlib.common.network;

import hungteen.htlib.api.HTLibAPI;
import hungteen.htlib.util.helper.HTLibHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.ForgePacketHandler;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;
import net.minecraftforge.network.packets.Acknowledge;
import net.minecraftforge.network.packets.MismatchData;

import java.util.function.BiConsumer;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-09-29 22:26
 **/
public class NetworkHandler {

    private static SimpleChannel channel;

    /**
     * 初始化网络通道，只会被调用一次。
     */
    public static void init() {
        if (channel == null) {
//            channel = ChannelBuilder
//                    .named(HTLibHelper.prefix("networking"))
//                    .optional()
//                    .networkProtocolVersion(1)
//                    .payloadChannel()
//                    .play()
//                    .serverbound()
//                    .add(Acknowledge.class, Acknowledge.STREAM_CODEC, ctx(ForgePacketHandler::handleClientAck))
//                    .build()
//                    .clientbound()
//                    .add(MismatchData.class, MismatchData.STREAM_CODEC, ctx(ForgePacketHandler::handleModMismatchData))
//                    .build
//            ;

            channel = ChannelBuilder
                    .named(HTLibHelper.prefix("networking"))
                    .optional()
                    .networkProtocolVersion(1)
                    .simpleChannel()
                    .play()
                    .serverbound()
                    .add(Acknowledge.class, Acknowledge.STREAM_CODEC, wrapServerHandler(ForgePacketHandler::handleClientAck))
                    .build()
                    .clientbound()
                    .add(MismatchData.class, MismatchData.STREAM_CODEC, wrapClientHandler(ForgePacketHandler::handleModMismatchData))
                    .build
            ;
        }
    }

    public static SimpleChannel channel() {
        init();
        return channel;
    }


//        CHANNEL.registerMessage(getId(), PlaySoundPacket.class, PlaySoundPacket::encode, PlaySoundPacket::new, PlaySoundPacket.Handler::onMessage);
//        CHANNEL.registerMessage(getId(), DummyEntityPacket.class, DummyEntityPacket::encode, DummyEntityPacket::new, DummyEntityPacket.Handler::onMessage);
//        CHANNEL.registerMessage(getId(), SyncDatapackPacket.class, SyncDatapackPacket::encode, SyncDatapackPacket::new, SyncDatapackPacket.Handler::onMessage);
//    }

    public static <MSG> void sendToServer(MSG msg) {
        channel().send(msg, PacketDistributor.SERVER.noArg());
    }

    public static <MSG> void sendToClient(MSG msg) {
        channel().send(PacketDistributor.ALL.noArg(), msg);
    }

    public static <MSG> void sendToClient(ServerPlayer serverPlayer, MSG msg) {
        channel().send(PacketDistributor.PLAYER.with(() -> serverPlayer), msg);
    }

    public static <MSG> void sendToNearByClient(Level world, Vec3 vec, double dis, MSG msg) {
        CHANNEL.send(PacketDistributor.NEAR.with(() -> {
            return new PacketDistributor.TargetPoint(vec.x, vec.y, vec.z, dis, world.dimension());
        }), msg);
    }

    public static <T extends PlayToServerPacket<T>> BiConsumer<T, CustomPayloadEvent.Context> wrapServerHandler(BiConsumer<T, ServerPacketContext> consumer) {
        return (t, payloadContext) -> {
            ServerPlayer player = payloadContext.getSender();
            if (player != null) {
                var serverPacketContext = new ServerPacketContext(player);
                payloadContext.setPacketHandled(true);
                payloadContext.enqueueWork(() -> {
                    consumer.accept(t, serverPacketContext);
                });
            } else {
                HTLibAPI.logger().debug("Tried to handle packet payload with no player: {}", t.type());
            }
        };
    }

    public static <T extends PlayToClientPacket<T>> BiConsumer<T, CustomPayloadEvent.Context> wrapClientHandler(BiConsumer<T, ClientPacketContext> consumer) {
        return (t, payloadContext) -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                var clientPacketContext = new ClientPacketContext(player);
                payloadContext.setPacketHandled(true);
                payloadContext.enqueueWork(() -> {
                    consumer.accept(t, clientPacketContext);
                });
            } else {
                HTLibAPI.logger().debug("Tried to handle packet payload with no player: {}", t.type());
            }
        };
    }

}
