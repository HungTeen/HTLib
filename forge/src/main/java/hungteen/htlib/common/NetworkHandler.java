package hungteen.htlib.common;

import hungteen.htlib.api.HTLibAPI;
import hungteen.htlib.common.network.ClientPacketContext;
import hungteen.htlib.common.network.HTPlayToClientPayload;
import hungteen.htlib.common.network.HTPlayToServerPayload;
import hungteen.htlib.common.network.ServerPacketContext;
import hungteen.htlib.common.network.packets.DummyEntityPacket;
import hungteen.htlib.common.network.packets.PlaySoundPacket;
import hungteen.htlib.common.network.packets.SyncDatapackPacket;
import hungteen.htlib.util.helper.impl.HTLibHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

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
            channel = ChannelBuilder
                    .named(HTLibHelper.prefix("networking"))
                    .optional()
                    .networkProtocolVersion(0)
                    .simpleChannel()
                    .configuration()
                    .clientbound()
                    .add(DummyEntityPacket.class, DummyEntityPacket.STREAM_CODEC, wrapClientHandler(DummyEntityPacket::process))
                    .add(SyncDatapackPacket.class, SyncDatapackPacket.STREAM_CODEC, wrapClientHandler(SyncDatapackPacket::process))
                    .play()
                    .clientbound()
                    .add(PlaySoundPacket.class, PlaySoundPacket.STREAM_CODEC, wrapClientHandler(PlaySoundPacket::process))
                    .build()
            ;
        }
    }

    public static SimpleChannel channel() {
        init();
        return channel;
    }

    public static <MSG> void sendToServer(MSG msg) {
        channel().send(msg, PacketDistributor.SERVER.noArg());
    }

    public static <MSG> void sendToClient(MSG msg) {
        channel().send(msg, PacketDistributor.ALL.noArg());
    }

    public static <MSG> void sendToClient(ServerPlayer serverPlayer, MSG msg) {
        channel().send(msg, PacketDistributor.PLAYER.with(serverPlayer));
    }

    public static <MSG> void sendToNearByClient(Level level, Vec3 vec, double dis, MSG msg) {
        channel().send(msg, PacketDistributor.NEAR.with(new PacketDistributor.TargetPoint(vec.x, vec.y, vec.z, dis, level.dimension())));
    }

    public static <T extends HTPlayToServerPayload> BiConsumer<T, CustomPayloadEvent.Context> wrapServerHandler(BiConsumer<T, ServerPacketContext> consumer) {
        return (t, payloadContext) -> {
            ServerPlayer player = payloadContext.getSender();
            if (player != null) {
                var serverPacketContext = new ServerPacketContext(player);
                payloadContext.setPacketHandled(true);
                payloadContext.enqueueWork(() -> {
                    consumer.accept(t, serverPacketContext);
                });
            } else {
                HTLibAPI.logger().debug("Tried to handle server packet payload with no player: {}", t.getClass().getSimpleName());
            }
        };
    }

    public static <T extends HTPlayToClientPayload> BiConsumer<T, CustomPayloadEvent.Context> wrapClientHandler(BiConsumer<T, ClientPacketContext> consumer) {
        return (t, payloadContext) -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                var clientPacketContext = new ClientPacketContext(player);
                payloadContext.setPacketHandled(true);
                payloadContext.enqueueWork(() -> {
                    consumer.accept(t, clientPacketContext);
                });
            } else {
                HTLibAPI.logger().debug("Tried to handle client packet payload with no player: {}", t.getClass().getSimpleName());
            }
        };
    }

}
