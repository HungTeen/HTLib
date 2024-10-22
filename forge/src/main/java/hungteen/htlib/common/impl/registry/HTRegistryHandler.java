package hungteen.htlib.common.impl.registry;

import hungteen.htlib.util.helper.JavaHelper;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DataPackRegistryEvent;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/22 21:08
 **/
public class HTRegistryHandler {

    public static void onDatapackSync(OnDatapackSyncEvent event){
        HTRegistryManager.getCodecRegistries().forEach(registry -> registry.syncToClient(event.getPlayer()));
    }

    public static void registerNewDatapack(DataPackRegistryEvent.NewRegistry event){
        JavaHelper.castStream(HTRegistryManager.getCodecRegistries().stream(), HTCodecRegistryImpl.class)
                .forEach(registry -> registry.addRegistry(event));
    }

    public static void register(IEventBus modBus){
        JavaHelper.castStream(HTRegistryManager.getCustomRegistries().stream(), HTForgeCustomRegistry.class)
                .forEach(registry -> {
                    registry.register(modBus);
                });
        JavaHelper.castStream(HTRegistryManager.getVanillaRegistries().stream(), HTForgeVanillaRegistry.class)
                .forEach(registry -> {
                    registry.register(modBus);
                });
    }
}
