package hungteen.htlib.common;

import hungteen.htlib.api.HTLibAPI;
import hungteen.htlib.common.codec.HTLibCodecRegistryHandler;
import hungteen.htlib.common.entity.HTLibEntities;
import hungteen.htlib.common.impl.HTLibBoatTypes;
import hungteen.htlib.common.impl.registry.HTForgeCodecRegistryImpl;
import hungteen.htlib.common.impl.registry.HTForgeCustomRegistry;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.common.world.entity.HTLibDummyEntities;
import hungteen.htlib.util.ForgeHelper;
import hungteen.htlib.util.helper.JavaHelper;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegisterEvent;

import java.util.stream.Stream;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/22 21:08
 **/
@Mod.EventBusSubscriber(modid = HTLibAPI.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class HTLibForgeRegistryHandler {

    @SubscribeEvent
    public static void registerNewEntries(RegisterEvent event) {
        getCustomRegistries().forEach(registry -> registry.addEntries(event));
    }

    @SubscribeEvent
    public static void registerCustomRegistry(NewRegistryEvent event) {
        getCustomRegistries().forEach(registry -> registry.createRegistry(event));
    }

    /**
     * Subscribe at {@link hungteen.htlib.HTLibForgeInitializer}.
     */
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        getCodecRegistries().forEach(registry -> registry.syncToClient(event.getPlayers()));
    }

    @SubscribeEvent
    public static void registerNewDatapack(DataPackRegistryEvent.NewRegistry event) {
        getCodecRegistries().forEach(registry -> registry.addRegistry(event));
    }

    public static void register(IEventBus modBus) {
        // Vanilla Registry.
        ForgeHelper.initRegistry(HTLibEntities.registry(), modBus);

        // Simple Registry.
        HTLibBoatTypes.registry().initialize();

        // Custom Registry.
        HTLibDummyEntities.registry().initialize();

        // Codec Registry.
        HTLibCodecRegistryHandler.initialize();
    }

    /**
     * call from {@link hungteen.htlib.HTLibForgeInitializer#onCommonSetup}.
     */
    public static void onCommonSetup() {
        getCustomRegistries().forEach(HTForgeCustomRegistry::clearEntries);
    }

    private static Stream<HTForgeCustomRegistry> getCustomRegistries() {
        return JavaHelper.castStream(HTRegistryManager.getCustomRegistries().stream(), HTForgeCustomRegistry.class);
    }

    private static Stream<HTForgeCodecRegistryImpl> getCodecRegistries() {
        return JavaHelper.castStream(HTRegistryManager.getCodecRegistries().stream(), HTForgeCodecRegistryImpl.class);
    }

}
