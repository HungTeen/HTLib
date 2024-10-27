package hungteen.htlib;

import hungteen.htlib.api.HTLibAPI;
import hungteen.htlib.common.*;
import hungteen.htlib.common.capability.PlayerCapabilityManager;
import hungteen.htlib.common.command.HTCommand;
import hungteen.htlib.common.impl.BoatTypes;
import hungteen.htlib.data.HTDataGenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-09-24 23:04
 **/
@Mod(HTLibAPI.MOD_ID)
public class HTLibForgeInitializer {

    public HTLibForgeInitializer(FMLJavaModLoadingContext context) {
        /* Mod Bus Events */
        IEventBus modBus = context.getModEventBus();

        modBus.addListener(HTLibForgeInitializer::onCommonSetup);
        modBus.addListener(EventPriority.LOW, HTSuitHandler::register);
        modBus.addListener(HTSuitHandler::clear);
        modBus.addListener(HTDataGenHandler::gatherData);
        modBus.addListener(HTSuitHandler::fillInCreativeTab);
        modBus.addListener(HTSuitHandler::addAttributes);
//        modBus.addListener(HTSuitHandler::addSpawnPlacements);
        modBus.addListener(HTSuitHandler::clear);
        HTLibForgeRegistryHandler.register(modBus);

        /* Forge Bus Events */
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(DummyEntityHandler::tick);
        forgeBus.addListener(PlayerCapabilityManager::tick);
        forgeBus.addListener(HTLibForgeRegistryHandler::onDatapackSync);
        forgeBus.addListener((RegisterCommandsEvent event) -> HTCommand.register(event.getDispatcher(), event.getBuildContext()));
    }

    public static void onCommonSetup(FMLCommonSetupEvent event) {
        HTLibForgeNetworkHandler.init();
        HTResourceManager.init();
        HTLibForgeRegistryHandler.onCommonSetup();
        event.enqueueWork(() -> {
            BoatTypes.register();
            HTSuitHandler.setUp();
        });
    }

}
