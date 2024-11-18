package hungteen.htlib.common;

import hungteen.htlib.api.HTLibAPI;
import hungteen.htlib.common.blockentity.HTLibBlockEntities;
import hungteen.htlib.common.codec.HTLibCodecRegistryHandler;
import hungteen.htlib.common.entity.HTLibEntities;
import hungteen.htlib.common.impl.HTLibBoatTypes;
import hungteen.htlib.common.impl.registry.HTNeoCodecRegistryImpl;
import hungteen.htlib.common.impl.registry.HTNeoCustomRegistry;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.common.world.entity.HTLibDummyEntities;
import hungteen.htlib.util.NeoHelper;
import hungteen.htlib.util.helper.JavaHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.stream.Stream;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/22 21:08
 **/
@EventBusSubscriber(modid = HTLibAPI.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class HTLibNeoRegistryHandler {

    @SubscribeEvent
    public static void registerNewEntries(RegisterEvent event) {
        getCustomRegistries().forEach(registry -> registry.addEntries(event));
    }

    @SubscribeEvent
    public static void registerCustomRegistry(NewRegistryEvent event) {
        getCustomRegistries().forEach(registry -> registry.createRegistry(event));
    }

    /**
     * Subscribe at {@link hungteen.htlib.HTLibNeoInitializer}.
     */
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        getCodecRegistries().forEach(registry -> registry.syncToClient(event.getRelevantPlayers().toList()));
    }

    @SubscribeEvent
    public static void registerNewDatapack(DataPackRegistryEvent.NewRegistry event) {
        getCodecRegistries().forEach(registry -> registry.addRegistry(event));

    }

    public static void register(IEventBus modBus) {
        // Vanilla Registry.
        NeoHelper.initRegistry(HTLibEntities.registry(), modBus);
        NeoHelper.initRegistry(HTLibBlockEntities.registry(), modBus);

        // Simple Registry.
        HTLibBoatTypes.registry().initialize();

        // Custom Registry.
        HTLibDummyEntities.registry().initialize();

        // Codec Suit Registry.
        HTLibCodecRegistryHandler.initialize();
    }

    /**
     * call from {@link hungteen.htlib.HTLibNeoInitializer#onCommonSetup}.
     */
    public static void onCommonSetup() {
        getCustomRegistries().forEach(HTNeoCustomRegistry::clearEntries);
    }

    private static Stream<HTNeoCustomRegistry> getCustomRegistries() {
        return JavaHelper.castStream(HTRegistryManager.getCustomRegistries().stream(), HTNeoCustomRegistry.class);
    }

    private static Stream<HTNeoCodecRegistryImpl> getCodecRegistries() {
        return JavaHelper.castStream(HTRegistryManager.getCodecRegistries().stream(), HTNeoCodecRegistryImpl.class);
    }

}
