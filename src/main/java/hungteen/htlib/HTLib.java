package hungteen.htlib;

import com.mojang.logging.LogUtils;
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
import hungteen.htlib.common.impl.result.HTResultComponents;
import hungteen.htlib.common.impl.result.HTResultTypes;
import hungteen.htlib.common.impl.spawn.HTSpawnComponents;
import hungteen.htlib.common.impl.spawn.HTSpawnTypes;
import hungteen.htlib.common.impl.wave.HTWaveComponents;
import hungteen.htlib.common.impl.wave.HTWaveTypes;
import hungteen.htlib.common.network.NetworkHandler;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.suit.HTSuitHandler;
import hungteen.htlib.common.world.entity.DummyEntityManager;
import hungteen.htlib.common.world.entity.HTDummyEntities;
import hungteen.htlib.data.HTDataGenHandler;
import hungteen.htlib.util.helper.HTLibHelper;
import hungteen.htlib.util.helper.registry.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import org.slf4j.Logger;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-09-24 23:04
 **/
@Mod(HTLib.MOD_ID)
public class HTLib {

    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Mod ID.
    public static final String MOD_ID = "htlib";
    // Proxy of Server and Client.
    public static CommonProxy PROXY = DistExecutor.unsafeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    public HTLib() {
        /* Mod Bus Events */
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        register(modBus);
        modBus.addListener(HTLib::setUp);
        modBus.addListener(HTLib::postRegister);
        modBus.addListener(EventPriority.LOW, HTSuitHandler::register);
        modBus.addListener(HTSuitHandler::clear);
        modBus.addListener(HTDataGenHandler::gatherData);
        modBus.addListener(HTSuitHandler::fillInCreativeTab);
        modBus.addListener(HTSuitHandler::addAttributes);
        modBus.addListener(HTSuitHandler::addSpawnPlacements);
        modBus.addListener(HTSuitHandler::clear);

        /* Forge Bus Events */
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(DummyEntityManager::tick);
        forgeBus.addListener(PlayerCapabilityManager::tick);
        forgeBus.addGenericListener(Entity.class, HTLib::attachCapabilities);
        forgeBus.addListener(HTRegistryManager::syncToClient);
        forgeBus.addListener((RegisterCommandsEvent event) -> HTCommand.register(event.getDispatcher(), event.getBuildContext()));
    }

    public void register(IEventBus modBus){
        HTEntities.register(modBus);
        HTSounds.register(modBus);
        HTBlockEntities.register(modBus);
        HTCommandArgumentInfos.register(modBus);

        HTDummyEntities.registry().register(modBus);

        HTPositionTypes.registry().register(modBus);
        HTPositionComponents.registry().register(modBus);
        HTResultTypes.registry().register(modBus);
        HTResultComponents.registry().register(modBus);
        HTSpawnTypes.registry().register(modBus);
        HTSpawnComponents.registry().register(modBus);
        HTWaveTypes.registry().register(modBus);
        HTWaveComponents.registry().register(modBus);
        HTRaidTypes.registry().register(modBus);
        HTRaidComponents.registry().register(modBus);
    }

    /**
     * Used in group registration.
     */
    public static void postRegister(RegisterEvent event){
        ItemHelper.get().register(event);
        BlockHelper.get().register(event);
        BlockHelper.entity().register(event);
        BlockHelper.paint().register(event);
        BlockHelper.banner().register(event);
        EntityHelper.get().register(event);
        EffectHelper.get().register(event);
        ParticleHelper.get().register(event);
        SoundHelper.get().register(event);
        BiomeHelper.get().register(event);
        FluidHelper.get().register(event);
        GameEventHelper.get().register(event);
        PoiTypeHelper.get().register(event);
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

    public static String id(){
        return MOD_ID;
    }

    public static Logger getLogger() {
        return LOGGER;
    }

}
