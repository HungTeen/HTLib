package hungteen.htlib;

import com.mojang.logging.LogUtils;
import hungteen.htlib.entity.HTBoat;
import hungteen.htlib.entity.HTEntities;
import hungteen.htlib.interfaces.IBoatType;
import hungteen.htlib.item.HTBoatDispenseItemBehavior;
import hungteen.htlib.network.NetworkHandler;
import net.minecraft.core.dispenser.BoatDispenseItemBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.DispenserBlock;
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

    public HTLib(){
//get mod event bus.
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(EventPriority.NORMAL, HTLib::setUp);
        HTEntities.ENTITY_TYPES.register(modBus);

        HTBoat.register(IBoatType.DEFAULT);
    }

    public static void setUp(FMLCommonSetupEvent event) {
        NetworkHandler.init();

        HTBoat.getBoatTypes().forEach(type -> {
            DispenserBlock.registerBehavior(type.getBoatItem(), new HTBoatDispenseItemBehavior(type));
        });
    }

    /**
     * get resource with mod prefix.
     */
    public static ResourceLocation res(String modid, String name) {
        return new ResourceLocation(modid, name);
    }

    /**
     * get resource with mod prefix.
     */
    public static ResourceLocation prefix(String name) {
        return res(MOD_ID, name);
    }

    public static Logger getLogger(){
        return LOGGER;
    }

}
