package hungteen.htlib.common;

import hungteen.htlib.api.HTLibAPI;
import hungteen.htlib.common.entity.HTLibEntities;
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
        // TODO group preInitialize
//        ItemHelper.getCodecRegistry().preInitialize(event);
//        BlockHelper.getCodecRegistry().preInitialize(event);
//        BlockHelper.entity().preInitialize(event);
//        BlockHelper.paint().preInitialize(event);
//        BlockHelper.banner().preInitialize(event);
//        EntityHelper.getCodecRegistry().preInitialize(event);
//        EffectHelper.getCodecRegistry().preInitialize(event);
//        ParticleHelper.getCodecRegistry().preInitialize(event);
//        SoundHelper.getCodecRegistry().preInitialize(event);
//        BiomeHelper.getCodecRegistry().preInitialize(event);
//        FluidHelper.getCodecRegistry().preInitialize(event);
//        GameEventHelper.getCodecRegistry().preInitialize(event);
//        PoiTypeHelper.getCodecRegistry().preInitialize(event);
    }

    @SubscribeEvent
    public static void registerCustomRegistry(NewRegistryEvent event) {
        getCustomRegistries().forEach(registry -> registry.createRegistry(event));
    }

    /**
     * Subscribe at {@link hungteen.htlib.HTLibNeoInitializer}.
     */
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        HTRegistryManager.getCodecRegistries().forEach(registry -> registry.syncToClient(event.getPlayer()));
    }

    @SubscribeEvent
    public static void registerNewDatapack(DataPackRegistryEvent.NewRegistry event) {
        getCodecRegistries().forEach(registry -> registry.addRegistry(event));

    }

    public static void register(IEventBus modBus) {
        NeoHelper.initRegistry(HTLibEntities.registry(), modBus);
        NeoHelper.initRegistry(HTLibSounds.registry(), modBus);

        HTLibDummyEntities.registry().initialize();
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