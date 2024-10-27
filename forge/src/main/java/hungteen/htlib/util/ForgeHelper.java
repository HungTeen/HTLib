package hungteen.htlib.util;

import hungteen.htlib.api.HTLibAPI;
import hungteen.htlib.api.util.helper.HTModIDHelper;
import hungteen.htlib.common.impl.registry.HTForgeVanillaRegistry;
import hungteen.htlib.common.impl.registry.HTVanillaRegistry;
import hungteen.htlib.common.network.ClientPacketContext;
import hungteen.htlib.common.network.HTPlayToClientPayload;
import hungteen.htlib.common.network.HTPlayToServerPayload;
import hungteen.htlib.common.network.ServerPacketContext;
import hungteen.htlib.platform.Platform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/23 16:09
 **/
public interface ForgeHelper {

    HTModIDHelper HELPER = Platform.FORGE::getName;

    static boolean isModLoaded(String modId){
        return ModList.get().isLoaded(modId);
    }

    static void runClient(Supplier<Runnable> runnable){
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, runnable);
    }

    static void runServer(Supplier<Runnable> runnable){
        DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, runnable);
    }

    static <T> void initRegistry(HTVanillaRegistry<T> registry, IEventBus eventBus){
        if(registry instanceof HTForgeVanillaRegistry<T> forgeRegistry){
            forgeRegistry.register(eventBus);
        }
    }

    static <T extends HTPlayToServerPayload> BiConsumer<T, CustomPayloadEvent.Context> wrapServerHandler(BiConsumer<T, ServerPacketContext> consumer) {
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

    static <T extends HTPlayToClientPayload> BiConsumer<T, CustomPayloadEvent.Context> wrapClientHandler(BiConsumer<T, ClientPacketContext> consumer) {
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

    static HTModIDHelper get(){
        return HELPER;
    }

}
