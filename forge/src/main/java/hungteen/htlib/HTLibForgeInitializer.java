package hungteen.htlib;

import hungteen.htlib.api.HTLibAPI;
import hungteen.htlib.common.HTLibSounds;
import hungteen.htlib.common.HTResourceManager;
import hungteen.htlib.common.HTSuitHandler;
import hungteen.htlib.common.RegistryHandler;
import hungteen.htlib.common.capability.PlayerCapabilityManager;
import hungteen.htlib.common.capability.raid.RaidCapProvider;
import hungteen.htlib.common.command.HTCommand;
import hungteen.htlib.common.command.HTLibCommandArgumentInfos;
import hungteen.htlib.common.entity.HTLibEntities;
import hungteen.htlib.common.impl.BoatTypes;
import hungteen.htlib.common.impl.position.HTLibPositionComponents;
import hungteen.htlib.common.impl.position.HTLibPositionTypes;
import hungteen.htlib.common.impl.raid.HTLibRaidComponents;
import hungteen.htlib.common.impl.raid.HTLibRaidTypes;
import hungteen.htlib.common.impl.result.HTLibResultComponents;
import hungteen.htlib.common.impl.result.HTLibResultTypes;
import hungteen.htlib.common.impl.spawn.HTLibSpawnComponents;
import hungteen.htlib.common.impl.spawn.HTLibSpawnTypes;
import hungteen.htlib.common.impl.wave.HTLibWaveComponents;
import hungteen.htlib.common.impl.wave.HTLibWaveTypes;
import hungteen.htlib.common.network.NetworkHandler;
import hungteen.htlib.common.world.entity.DummyEntityManager;
import hungteen.htlib.common.world.entity.HTLibDummyEntities;
import hungteen.htlib.data.HTDataGenHandler;
import hungteen.htlib.util.helper.HTLibHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
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
        /* Mod Bus Events */
        IEventBus modBus = context.getModEventBus();

        modBus.addListener(HTLibForgeInitializer::setUp);
        modBus.addListener(RegistryHandler::postRegister);
        modBus.addListener(EventPriority.LOW, HTSuitHandler::register);
        modBus.addListener(HTSuitHandler::clear);
        modBus.addListener(HTDataGenHandler::gatherData);
        modBus.addListener(HTSuitHandler::fillInCreativeTab);
        modBus.addListener(HTSuitHandler::addAttributes);
//        modBus.addListener(HTSuitHandler::addSpawnPlacements);
        modBus.addListener(HTSuitHandler::clear);
        modBus.addListener(RegistryHandler::registerNewDatapack);
        RegistryHandler.register(modBus);

        /* Forge Bus Events */
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(DummyEntityManager::tick);
        forgeBus.addListener(PlayerCapabilityManager::tick);
        forgeBus.addGenericListener(Entity.class, HTLibForgeInitializer::attachCapabilities);
        forgeBus.addListener(RegistryHandler::onDatapackSync);
        forgeBus.addListener((RegisterCommandsEvent event) -> HTCommand.register(event.getDispatcher(), event.getBuildContext()));

        initialize();
    }

    public void initialize() {
        HTLibEntities.registry().initialize();
        HTLibSounds.registry().initialize();
        HTLibCommandArgumentInfos.registry().initialize();
        HTLibDummyEntities.registry().initialize();

        HTLibPositionTypes.registry().initialize();
        HTLibPositionComponents.registry().initialize();
        HTLibResultTypes.registry().initialize();
        HTLibResultComponents.registry().initialize();
        HTLibSpawnTypes.registry().initialize();
        HTLibSpawnComponents.registry().initialize();
        HTLibWaveTypes.registry().initialize();
        HTLibWaveComponents.registry().initialize();
        HTLibRaidTypes.registry().initialize();
        HTLibRaidComponents.registry().initialize();
    }

    public static void setUp(FMLCommonSetupEvent event) {
        NetworkHandler.init();
        HTResourceManager.init();
        event.enqueueWork(() -> {
            BoatTypes.register();
            HTSuitHandler.setUp();
        });
    }

    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {

        } else {
            event.addCapability(HTLibHelper.prefix("raid"), new RaidCapProvider(event.getObject()));
        }
    }

}
