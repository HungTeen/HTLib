package hungteen.htlib;

import com.mojang.logging.LogUtils;
import hungteen.htlib.client.ClientProxy;
import hungteen.htlib.common.HTSounds;
import hungteen.htlib.common.WoodIntegrations;
import hungteen.htlib.common.capability.PlayerCapabilityManager;
import hungteen.htlib.common.capability.raid.RaidCapProvider;
import hungteen.htlib.common.command.HTCommand;
import hungteen.htlib.common.command.HTCommandArgumentInfos;
import hungteen.htlib.common.datapack.HTCodecLoader;
import hungteen.htlib.common.entity.HTEntities;
import hungteen.htlib.common.impl.placement.HTPlaceComponents;
import hungteen.htlib.common.impl.raid.HTRaidComponents;
import hungteen.htlib.common.impl.result.HTResultComponents;
import hungteen.htlib.common.impl.spawn.HTSpawnComponents;
import hungteen.htlib.common.impl.wave.HTWaveComponents;
import hungteen.htlib.common.network.NetworkHandler;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.world.entity.DummyEntityManager;
import hungteen.htlib.common.world.entity.HTDummyEntities;
import hungteen.htlib.data.HTTestGen;
import hungteen.htlib.util.helper.StringHelper;
import hungteen.htlib.util.helper.registry.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
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
        modBus.addListener(EventPriority.NORMAL, HTLib::setUp);
        modBus.addListener(EventPriority.NORMAL, false, HTLib::postRegister);
        modBus.addListener(EventPriority.LOW, false, WoodIntegrations::register);
        modBus.addListener(EventPriority.NORMAL, false, HTTestGen::gatherData);
        modBus.addListener(EventPriority.NORMAL, false, WoodIntegrations::fillInCreativeTab);
        modBus.addListener(EventPriority.LOWEST, false, FMLClientSetupEvent.class, event -> HTRegistryManager.globalInit());

        /* Forge Bus Events */
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(EventPriority.NORMAL, DummyEntityManager::tick);
        forgeBus.addListener(EventPriority.NORMAL, PlayerCapabilityManager::tick);
        forgeBus.addGenericListener(Entity.class, HTLib::attachCapabilities);
        forgeBus.addListener(EventPriority.NORMAL, false, AddReloadListenerEvent.class, event -> event.addListener(new HTCodecLoader()));
        forgeBus.addListener(EventPriority.NORMAL, false, OnDatapackSyncEvent.class, HTRegistryManager::syncToClient);
        forgeBus.addListener(EventPriority.NORMAL, false, RegisterCommandsEvent.class, event -> HTCommand.register(event.getDispatcher()));
    }

    public void register(IEventBus modBus){
        HTEntities.register(modBus);
        HTSounds.register(modBus);
        HTCommandArgumentInfos.register(modBus);
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

        event.enqueueWork(() -> {
            HTPlaceComponents.registerStuffs();
            HTSpawnComponents.registerStuffs();
            HTWaveComponents.registerStuffs();
            HTResultComponents.registerStuffs();
            HTRaidComponents.registerStuffs();
            HTDummyEntities.registerStuffs();

        });
    }

    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {

        } else {
            event.addCapability(StringHelper.prefix("raid"), new RaidCapProvider(event.getObject()));
        }
    }

    public static Logger getLogger() {
        return LOGGER;
    }

}
