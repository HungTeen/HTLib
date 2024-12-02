package hungteen.htlib.common;

import hungteen.htlib.common.network.packet.*;
import hungteen.htlib.util.ForgeHelper;
import hungteen.htlib.util.helper.impl.HTLibHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-09-29 22:26
 **/
public class HTLibForgeNetworkHandler {

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
                    .play()
                    .clientbound()
                    .add(DummyEntityPlayPacket.class, DummyEntityPlayPacket.STREAM_CODEC, ForgeHelper.wrapClientHandler(DummyEntityPlayPacket::process))
                    .add(DummyEntityInitPacket.class, DummyEntityInitPacket.STREAM_CODEC, ForgeHelper.wrapClientHandler(DummyEntityInitPacket::process))
                    .add(PlaySoundPacket.class, PlaySoundPacket.STREAM_CODEC, ForgeHelper.wrapClientHandler(PlaySoundPacket::process))
                    .add(SyncDatapackPacket.class, SyncDatapackPacket.STREAM_CODEC, ForgeHelper.wrapClientHandler(SyncDatapackPacket::process))
                    .add(SpawnParticlePacket.class, SpawnParticlePacket.STREAM_CODEC, ForgeHelper.wrapClientHandler(SpawnParticlePacket::process))
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

    public static <MSG> void sendToClientTrackingPlayerAndSelf(Entity entity, MSG msg) {
        channel().send(msg, PacketDistributor.TRACKING_ENTITY_AND_SELF.with(entity));
    }

}
