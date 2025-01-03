package hungteen.htlib;

import hungteen.htlib.api.HTLibAPI;
import hungteen.htlib.common.HTLibNeoRegistryHandler;
import hungteen.htlib.common.HTResourceManager;
import hungteen.htlib.common.command.HTLibCommand;
import hungteen.htlib.common.impl.HTLibBoatTypes;
import hungteen.htlib.common.registry.HTLibSuitHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * @author PangTeen
 */
@Mod(HTLibAPI.MOD_ID)
public class HTLibNeoInitializer {

    public HTLibNeoInitializer(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(HTLibNeoInitializer::onCommonSetup);
        HTLibNeoRegistryHandler.register(modBus);

        /* Neo Bus Events */
        IEventBus forgeBus = NeoForge.EVENT_BUS;
        forgeBus.addListener(HTLibNeoRegistryHandler::onDatapackSync);
        forgeBus.addListener((RegisterCommandsEvent event) -> HTLibCommand.register(event.getDispatcher(), event.getBuildContext()));
    }

    public static void onCommonSetup(FMLCommonSetupEvent event) {
        HTResourceManager.init();
        HTLibNeoRegistryHandler.onCommonSetup();
        event.enqueueWork(() -> {
            HTLibBoatTypes.registerDispenserBehaviors();
            HTLibSuitHandler.setUp();
        });
    }


}
