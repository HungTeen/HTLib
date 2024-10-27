package hungteen.htlib.util;

import hungteen.htlib.api.HTLibAPI;
import hungteen.htlib.api.util.helper.HTModIDHelper;
import hungteen.htlib.common.impl.registry.HTNeoVanillaRegistry;
import hungteen.htlib.common.impl.registry.HTVanillaRegistry;
import hungteen.htlib.common.network.ClientPacketContext;
import hungteen.htlib.common.network.ServerPacketContext;
import hungteen.htlib.common.network.packet.PlayToClientPacket;
import hungteen.htlib.common.network.packet.PlayToServerPacket;
import hungteen.htlib.platform.Platform;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

import java.util.function.BiConsumer;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/26 21:40
 **/
public interface NeoHelper {

    HTModIDHelper HELPER = Platform.NEOFORGE::getName;

    static boolean isModLoaded(String modId){
        return ModList.get().isLoaded(modId);
    }

    static <T> void initRegistry(HTVanillaRegistry<T> registry, IEventBus eventBus){
        if(registry instanceof HTNeoVanillaRegistry<T> forgeRegistry){
            forgeRegistry.register(eventBus);
        }
    }

    static <T extends PlayToServerPacket> IPayloadHandler<T> wrapServerHandler(BiConsumer<T, ServerPacketContext> consumer) {
        return (t, payloadContext) -> {
            if (payloadContext.player() instanceof ServerPlayer player) {
                var serverPacketContext = new ServerPacketContext(player);
                payloadContext.enqueueWork(() -> {
                    consumer.accept(t, serverPacketContext);
                });
            } else {
                HTLibAPI.logger().debug("Tried to handle server packet payload with no player: {}", t.getClass().getSimpleName());
            }
        };
    }

    static <T extends PlayToClientPacket> IPayloadHandler<T> wrapClientHandler(BiConsumer<T, ClientPacketContext> consumer) {
        return (t, payloadContext) -> {
            if (payloadContext.player() instanceof LocalPlayer player) {
                var clientPacketContext = new ClientPacketContext(player);
                payloadContext.enqueueWork(() -> {
                    consumer.accept(t, clientPacketContext);
                });
            } else {
                HTLibAPI.logger().debug("Tried to handle client packet payload with no player: {}", t.getClass().getSimpleName());
            }
        };
    }

    static HTModIDHelper get(){
        return HELPER;
    }
}
