package hungteen.htlib.util;

import hungteen.htlib.api.HTLibAPI;
import hungteen.htlib.api.util.helper.HTModIDHelper;
import hungteen.htlib.api.util.helper.HTResourceHelper;
import hungteen.htlib.common.impl.registry.HTNeoVanillaRegistry;
import hungteen.htlib.common.impl.registry.HTVanillaRegistry;
import hungteen.htlib.common.network.ClientPacketContext;
import hungteen.htlib.common.network.ServerPacketContext;
import hungteen.htlib.common.network.packet.PlayToClientPacket;
import hungteen.htlib.common.network.packet.PlayToServerPacket;
import hungteen.htlib.api.util.Platform;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/26 21:40
 **/
public interface NeoHelper {

    HTModIDHelper HELPER = Platform.NEOFORGE::getNamespace;

    /**
     * Check if the mod with the given modId is loaded.
     * @param modId the modId to check
     * @return true if the mod is loaded
     */
    static boolean isModLoaded(String modId){
        return ModList.get().isLoaded(modId);
    }

    /**
     * Avoid crash by fake player.
     */
    static boolean isValidPlayer(Player player) {
        return player != null && ! (player instanceof FakePlayer);
    }

    /**
     * Initialize the vanilla registry with the event bus.
     */
    static <T> void initRegistry(HTVanillaRegistry<T> registry, IEventBus eventBus){
        if(registry instanceof HTNeoVanillaRegistry<T> forgeRegistry){
            forgeRegistry.register(eventBus);
        }
    }

    /**
     * Wrap a server packet handler with the given consumer.
     */
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

    /**
     * Wrap a client packet handler with the given consumer.
     */
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

    /**
     * whether the event can be registered with specific registry type.
     * @param helper stand for the registry type.
     */
    static <T> boolean canRegister(RegisterEvent event, HTResourceHelper<T> helper){
        return event.getRegistryKey().equals(helper.resourceKey());
    }

    /**
     * registerDispenserBehaviors the supplier with the given name to the event with specific registry type.
     * @param helper stand for the registry type.
     */
    static <T> void register(RegisterEvent event, HTResourceHelper<T> helper, ResourceLocation name, Supplier<T> supplier){
        event.register(helper.resourceKey(), name, supplier);
    }

    static HTModIDHelper get(){
        return HELPER;
    }
}
