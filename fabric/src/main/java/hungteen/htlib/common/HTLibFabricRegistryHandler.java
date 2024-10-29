package hungteen.htlib.common;

import hungteen.htlib.common.entity.HTLibEntities;
import hungteen.htlib.common.impl.HTLibBoatTypes;
import hungteen.htlib.common.impl.registry.HTFabricCodecRegistryImpl;
import hungteen.htlib.common.impl.registry.HTFabricCustomRegistry;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.common.world.entity.HTLibDummyEntities;
import hungteen.htlib.util.helper.JavaHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import java.util.stream.Stream;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/22 21:08
 **/
public class HTLibFabricRegistryHandler {

    public static void register() {
        initialize();
        onDatapackSync();
        registerNewDatapack();
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

    private static void onDatapackSync() {
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> {
            getCodecRegistries().forEach(registry -> registry.syncToClient(player));
        });
    }

    private static void registerNewDatapack() {
        getCodecRegistries().forEach(HTFabricCodecRegistryImpl::addRegistry);
    }

    private static void initialize() {
        HTLibEntities.registry().initialize();
        HTLibSounds.registry().initialize();

        HTLibBoatTypes.registry().initialize();
        HTLibDummyEntities.registry().initialize();
    }

    private static Stream<HTFabricCustomRegistry> getCustomRegistries() {
        return JavaHelper.castStream(HTRegistryManager.getCustomRegistries().stream(), HTFabricCustomRegistry.class);
    }

    private static Stream<HTFabricCodecRegistryImpl> getCodecRegistries() {
        return JavaHelper.castStream(HTRegistryManager.getCodecRegistries().stream(), HTFabricCodecRegistryImpl.class);
    }

}
