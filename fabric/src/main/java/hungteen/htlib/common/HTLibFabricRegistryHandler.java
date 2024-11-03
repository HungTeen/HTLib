package hungteen.htlib.common;

import hungteen.htlib.common.codec.HTLibCodecRegistryHandler;
import hungteen.htlib.common.entity.HTLibEntities;
import hungteen.htlib.common.impl.HTLibBoatTypes;
import hungteen.htlib.common.impl.registry.HTFabricCodecRegistryImpl;
import hungteen.htlib.common.impl.registry.HTFabricCustomRegistry;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.common.world.entity.HTLibDummyEntities;
import hungteen.htlib.util.helper.JavaHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import java.util.List;
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
    }

    private static void onDatapackSync() {
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> {
            getCodecRegistries().forEach(registry -> registry.syncToClient(List.of(player)));
        });
    }

    private static void initialize() {
        // Vanilla Registry.
        HTLibEntities.registry().initialize();

        // Simple Registry.
        HTLibBoatTypes.registry().initialize();

        // Custom Registry.
        HTLibDummyEntities.registry().initialize();

        // Codec Suit Registry.
        HTLibCodecRegistryHandler.initialize();
    }

    private static Stream<HTFabricCustomRegistry> getCustomRegistries() {
        return JavaHelper.castStream(HTRegistryManager.getCustomRegistries().stream(), HTFabricCustomRegistry.class);
    }

    private static Stream<HTFabricCodecRegistryImpl> getCodecRegistries() {
        return JavaHelper.castStream(HTRegistryManager.getCodecRegistries().stream(), HTFabricCodecRegistryImpl.class);
    }

}
