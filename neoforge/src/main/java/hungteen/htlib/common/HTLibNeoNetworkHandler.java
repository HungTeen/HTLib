package hungteen.htlib.common;

import hungteen.htlib.api.HTLibAPI;
import hungteen.htlib.common.network.packet.*;
import hungteen.htlib.util.NeoHelper;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import javax.annotation.Nullable;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-09-29 22:26
 **/
@EventBusSubscriber(modid = HTLibAPI.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class HTLibNeoNetworkHandler {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(
                DummyEntityPlayPacket.TYPE,
                DummyEntityPlayPacket.STREAM_CODEC,
                NeoHelper.wrapClientHandler(DummyEntityPlayPacket::process)
        );
        registrar.playToClient(
                DummyEntityInitPacket.TYPE,
                DummyEntityInitPacket.STREAM_CODEC,
                NeoHelper.wrapClientHandler(DummyEntityInitPacket::process)
        );
        registrar.playToClient(
                SyncDatapackPacket.TYPE,
                SyncDatapackPacket.STREAM_CODEC,
                NeoHelper.wrapClientHandler(SyncDatapackPacket::process)
        );
        registrar.playToClient(
                PlaySoundPacket.TYPE,
                PlaySoundPacket.STREAM_CODEC,
                NeoHelper.wrapClientHandler(PlaySoundPacket::process)
        );
        registrar.playToClient(
                SpawnParticlePacket.TYPE,
                SpawnParticlePacket.STREAM_CODEC,
                NeoHelper.wrapClientHandler(SpawnParticlePacket::process)
        );
    }

    public static void sendToServer(CustomPacketPayload msg) {
        PacketDistributor.sendToServer(msg);
    }

    public static void sendToClient(CustomPacketPayload msg) {
        PacketDistributor.sendToAllPlayers(msg);
    }

    public static void sendToClient(ServerPlayer serverPlayer, CustomPacketPayload msg) {
        PacketDistributor.sendToPlayer(serverPlayer, msg);
    }

    public static void sendToNearByClient(ServerLevel level, @Nullable ServerPlayer player, Vec3 vec, double dis, CustomPacketPayload msg) {
        PacketDistributor.sendToPlayersNear(level, player, vec.x, vec.y, vec.z, dis, msg);
    }

    public static void sendToClientTrackingPlayerAndSelf(Entity entity, CustomPacketPayload msg) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, msg);
    }

}
