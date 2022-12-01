package hungteen.htlib;

import com.mojang.logging.LogUtils;
import hungteen.htlib.client.ClientProxy;
import hungteen.htlib.common.capability.raid.RaidCapProvider;
import hungteen.htlib.common.command.HTCommand;
import hungteen.htlib.common.command.HTCommandArgumentInfos;
import hungteen.htlib.common.datapack.HTCodecLoader;
import hungteen.htlib.common.entity.HTBoat;
import hungteen.htlib.common.entity.HTEntities;
import hungteen.htlib.common.item.HTBoatDispenseItemBehavior;
import hungteen.htlib.common.network.NetworkHandler;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.world.entity.DummyEntityManager;
import hungteen.htlib.common.world.entity.HTDummyEntities;
import hungteen.htlib.data.HTTestGen;
import hungteen.htlib.impl.placement.HTPlaceComponents;
import hungteen.htlib.impl.raid.HTRaidComponents;
import hungteen.htlib.impl.spawn.HTSpawnComponents;
import hungteen.htlib.impl.wave.HTWaveComponents;
import hungteen.htlib.util.interfaces.IBoatType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.*;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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
    // WIDGETS.
    public static final ResourceLocation WIDGETS = prefix("textures/gui/widgets.png");

    public HTLib() {
        /* Mod Bus Events */
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(EventPriority.NORMAL, HTLib::setUp);
        modBus.addListener(EventPriority.NORMAL, false, GatherDataEvent.class, (event) -> {
            event.getGenerator().addProvider(event.includeServer(), new HTTestGen(event.getGenerator()));
        });
        HTEntities.register(modBus);
        HTCommandArgumentInfos.register(modBus);

        /* Forge Bus Events */
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(EventPriority.NORMAL, false, AddReloadListenerEvent.class, (event) -> {
            event.addListener(new HTCodecLoader());
        });
        forgeBus.addListener(EventPriority.NORMAL, false, OnDatapackSyncEvent.class, (event) -> {
                HTRegistryManager.getRegistries().forEach(registry -> {
                    event.getPlayerList().getPlayers().forEach(registry::syncToClient);
                });
        });
        forgeBus.addListener(EventPriority.NORMAL, false, TickEvent.LevelTickEvent.class, (event) -> {
            if(event.phase == TickEvent.Phase.END && event.level instanceof ServerLevel){
                DummyEntityManager.get((ServerLevel) event.level).tick();
            }
        });
        forgeBus.addListener(EventPriority.NORMAL, false, RegisterCommandsEvent.class, (event) -> {
            HTCommand.register(event.getDispatcher());
        });
        forgeBus.addGenericListener(Entity.class, HTLib::attachCapabilities);

        HTPlaceComponents.registerStuffs();
        HTSpawnComponents.registerStuffs();
        HTWaveComponents.registerStuffs();
        HTRaidComponents.registerStuffs();
        HTDummyEntities.registerStuffs();
        HTBoat.register(IBoatType.DEFAULT);
    }

    public static void setUp(FMLCommonSetupEvent event) {
        NetworkHandler.init();

        HTBoat.getBoatTypes().forEach(type -> {
            DispenserBlock.registerBehavior(type.getBoatItem(), new HTBoatDispenseItemBehavior(type, false));
            DispenserBlock.registerBehavior(type.getChestBoatItem(), new HTBoatDispenseItemBehavior(type, true));
        });
    }

    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event){
        if(event.getObject() instanceof Player){

        } else {
            event.addCapability(prefix("raid"), new RaidCapProvider(event.getObject()));
        }
    }

    /**
     * get resource with mod prefix.
     */
    public static ResourceLocation res(String modId, String name) {
        return new ResourceLocation(modId, name);
    }

    /**
     * get resource with mod prefix.
     */
    public static ResourceLocation prefix(String name) {
        return res(MOD_ID, name);
    }

    public static boolean in(ResourceLocation resourceLocation){
        return in(resourceLocation, MOD_ID);
    }

    public static boolean in(ResourceLocation resourceLocation, String modId){
        return resourceLocation.getNamespace().equals(modId);
    }

    public static Logger getLogger() {
        return LOGGER;
    }

}
