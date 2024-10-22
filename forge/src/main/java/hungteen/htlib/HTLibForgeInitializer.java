package hungteen.htlib;

import hungteen.htlib.api.HTLibAPI;
import hungteen.htlib.client.ClientProxy;
import hungteen.htlib.common.HTResourceManager;
import hungteen.htlib.common.HTSounds;
import hungteen.htlib.common.blockentity.HTBlockEntities;
import hungteen.htlib.common.capability.PlayerCapabilityManager;
import hungteen.htlib.common.capability.raid.RaidCapProvider;
import hungteen.htlib.common.command.HTCommand;
import hungteen.htlib.common.command.HTCommandArgumentInfos;
import hungteen.htlib.common.entity.HTEntities;
import hungteen.htlib.common.impl.BoatTypes;
import hungteen.htlib.common.impl.position.HTPositionComponents;
import hungteen.htlib.common.impl.position.HTPositionTypes;
import hungteen.htlib.common.impl.raid.HTRaidComponents;
import hungteen.htlib.common.impl.raid.HTRaidTypes;
import hungteen.htlib.common.impl.registry.HTCodecRegistryImpl;
import hungteen.htlib.common.impl.registry.HTRegistryHandler;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.common.impl.result.HTResultComponents;
import hungteen.htlib.common.impl.result.HTResultTypes;
import hungteen.htlib.common.impl.spawn.HTSpawnComponents;
import hungteen.htlib.common.impl.spawn.HTSpawnTypes;
import hungteen.htlib.common.impl.wave.HTWaveComponents;
import hungteen.htlib.common.impl.wave.HTWaveTypes;
import hungteen.htlib.common.network.NetworkHandler;
import hungteen.htlib.common.impl.registry.suit.HTSuitHandler;
import hungteen.htlib.common.world.entity.DummyEntityManager;
import hungteen.htlib.common.world.entity.HTDummyEntities;
import hungteen.htlib.data.HTDataGenHandler;
import hungteen.htlib.util.helper.HTLibHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DataPackRegistryEvent;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegisterEvent;
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
    public static CommonProxy PROXY = DistExecutor.unsafeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    public HTLibForgeInitializer(FMLJavaModLoadingContext context) {
        /* Mod Bus Events */
        IEventBus modBus = context.getModEventBus();
        register(modBus);
        modBus.addListener(HTLibForgeInitializer::setUp);
        modBus.addListener(HTLibForgeInitializer::postRegister);
        modBus.addListener(EventPriority.LOW, HTSuitHandler::register);
        modBus.addListener(HTSuitHandler::clear);
        modBus.addListener(HTDataGenHandler::gatherData);
        modBus.addListener(HTSuitHandler::fillInCreativeTab);
        modBus.addListener(HTSuitHandler::addAttributes);
        modBus.addListener(HTSuitHandler::addSpawnPlacements);
        modBus.addListener(HTSuitHandler::clear);
        modBus.addListener(HTRegistryHandler::registerNewDatapack);

        /* Forge Bus Events */
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(DummyEntityManager::tick);
        forgeBus.addListener(PlayerCapabilityManager::tick);
        forgeBus.addGenericListener(Entity.class, HTLibForgeInitializer::attachCapabilities);
        forgeBus.addListener(HTRegistryHandler::onDatapackSync);
        forgeBus.addListener((RegisterCommandsEvent event) -> HTCommand.register(event.getDispatcher(), event.getBuildContext()));
    }

    public void register(IEventBus modBus) {
        HTSounds.register(modBus);
        HTBlockEntities.register(modBus);
        HTCommandArgumentInfos.register(modBus);

        HTDummyEntities.registry().initialize();

        HTPositionTypes.registry().initialize();
        HTPositionComponents.registry().register(modBus);
        HTResultTypes.registry().initialize();
        HTResultComponents.registry().register(modBus);
        HTSpawnTypes.registry().initialize();
        HTSpawnComponents.registry().register(modBus);
        HTWaveTypes.registry().initialize();
        HTWaveComponents.registry().register(modBus);
        HTRaidTypes.registry().initialize();
        HTRaidComponents.registry().register(modBus);
    }

    /**
     * Used in group registration.
     */
    public static void postRegister(RegisterEvent event) {
        // TODO group register
//        ItemHelper.getCodecRegistry().register(event);
//        BlockHelper.getCodecRegistry().register(event);
//        BlockHelper.entity().register(event);
//        BlockHelper.paint().register(event);
//        BlockHelper.banner().register(event);
//        EntityHelper.getCodecRegistry().register(event);
//        EffectHelper.getCodecRegistry().register(event);
//        ParticleHelper.getCodecRegistry().register(event);
//        SoundHelper.getCodecRegistry().register(event);
//        BiomeHelper.getCodecRegistry().register(event);
//        FluidHelper.getCodecRegistry().register(event);
//        GameEventHelper.getCodecRegistry().register(event);
//        PoiTypeHelper.getCodecRegistry().register(event);
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

    public static String id() {
        return MOD_ID;
    }

    public static Logger getLogger() {
        return LOGGER;
    }

}
