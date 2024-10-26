package hungteen.htlib;

import hungteen.htlib.api.HTLibAPI;
import hungteen.htlib.common.*;
import hungteen.htlib.common.capability.PlayerCapabilityManager;
import hungteen.htlib.common.command.HTCommand;
import hungteen.htlib.common.entity.HTLibEntities;
import hungteen.htlib.common.impl.BoatTypes;
import hungteen.htlib.common.world.entity.HTLibDummyEntities;
import hungteen.htlib.data.HTDataGenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-09-24 23:04
 **/
@Mod(HTLibAPI.MOD_ID)
public class HTLibForgeInitializer {

    private static final Logger LOGGER = HTLibAPI.logger();
    public static final String MOD_ID = HTLibAPI.MOD_ID;

    public HTLibForgeInitializer(FMLJavaModLoadingContext context) {
        initialize();

        /* Mod Bus Events */
        IEventBus modBus = context.getModEventBus();

        modBus.addListener(HTLibForgeInitializer::setUp);
        modBus.addListener(HTRegistryHandler::postRegister);
        modBus.addListener(EventPriority.LOW, HTSuitHandler::register);
        modBus.addListener(HTSuitHandler::clear);
        modBus.addListener(HTDataGenHandler::gatherData);
        modBus.addListener(HTSuitHandler::fillInCreativeTab);
        modBus.addListener(HTSuitHandler::addAttributes);
//        modBus.addListener(HTSuitHandler::addSpawnPlacements);
        modBus.addListener(HTSuitHandler::clear);
        modBus.addListener(HTRegistryHandler::registerNewDatapack);
        HTRegistryHandler.register(modBus);

        /* Forge Bus Events */
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(DummyEntityHandler::tick);
        forgeBus.addListener(PlayerCapabilityManager::tick);
        forgeBus.addListener(HTRegistryHandler::onDatapackSync);
        forgeBus.addListener((RegisterCommandsEvent event) -> HTCommand.register(event.getDispatcher(), event.getBuildContext()));
    }

    public void initialize() {
        HTLibEntities.registry().initialize();
        HTLibSounds.registry().initialize();
        HTLibDummyEntities.registry().initialize();
    }

    public static void setUp(FMLCommonSetupEvent event) {
        NetworkHandler.init();
        HTResourceManager.init();
        event.enqueueWork(() -> {
            BoatTypes.register();
            HTSuitHandler.setUp();
        });
    }

}
