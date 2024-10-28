package hungteen.htlib.util;

import hungteen.htlib.api.HTLibAPI;
import hungteen.htlib.api.util.helper.HTModIDHelper;
import hungteen.htlib.common.network.ClientPacketContext;
import hungteen.htlib.common.network.ServerPacketContext;
import hungteen.htlib.common.network.packet.PlayToClientPacket;
import hungteen.htlib.common.network.packet.PlayToServerPacket;
import hungteen.htlib.platform.Platform;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/28 9:21
 **/
public interface FabricHelper {

    HTModIDHelper HELPER = Platform.FABRIC::getName;

    static <T extends PlayToServerPacket> ServerPlayNetworking.PlayPayloadHandler<T> wrapServerHandler(BiConsumer<T, ServerPacketContext> consumer) {
        return (t, payloadContext) -> {
            ServerPlayer player = payloadContext.player();
            if (player != null) {
                var serverPacketContext = new ServerPacketContext(player);
                payloadContext.server().execute(() -> {
                    consumer.accept(t, serverPacketContext);
                });
            } else {
                HTLibAPI.logger().debug("Tried to handle server packet payload with no player: {}", t.getClass().getSimpleName());
            }
        };
    }

    static <T extends PlayToClientPacket> ClientPlayNetworking.PlayPayloadHandler<T> wrapClientHandler(BiConsumer<T, ClientPacketContext> consumer) {
        return (t, payloadContext) -> {
            LocalPlayer player = payloadContext.player();
            if (player != null) {
                var clientPacketContext = new ClientPacketContext(player);
                payloadContext.client().execute(() -> {
                    consumer.accept(t, clientPacketContext);
                });
            } else {
                HTLibAPI.logger().debug("Tried to handle client packet payload with no player: {}", t.getClass().getSimpleName());
            }
        };
    }

    static HTModIDHelper get() {
        return HELPER;
    }
}
