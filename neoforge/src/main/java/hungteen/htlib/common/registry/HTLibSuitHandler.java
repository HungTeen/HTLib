package hungteen.htlib.common.registry;

import hungteen.htlib.common.registry.suit.HTEntitySuit;
import hungteen.htlib.common.registry.suit.HTStoneSuit;
import hungteen.htlib.common.registry.suit.HTWoodSuit;
import hungteen.htlib.util.NeoHelper;
import hungteen.htlib.util.helper.impl.BlockHelper;
import hungteen.htlib.util.helper.impl.EntityHelper;
import hungteen.htlib.util.helper.impl.ItemHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/7/4 16:58
 */
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class HTLibSuitHandler {

    /**
     * Register at the tail of event.
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void register(RegisterEvent event) {
        if(NeoHelper.canRegister(event, ItemHelper.get()) || NeoHelper.canRegister(event, BlockHelper.get())){
            // Register for all stone suit.
            HTLibStoneSuits.getSuits().forEach(suit -> {
                suit.register(event);
            });

            // Register for all wood suit.
            HTLibWoodSuits.getSuits().forEach(suit -> {
                suit.register(event);
            });
        }
        if(NeoHelper.canRegister(event, ItemHelper.get()) || NeoHelper.canRegister(event, EntityHelper.get())){
            // Register for all entity suit.
            EntitySuits.getSuits().forEach(suit -> {
                suit.register(event);
            });
        }
    }

    @SubscribeEvent
    public static void addAttributes(EntityAttributeCreationEvent event) {
        EntitySuits.getSuits().forEach(suit -> {
            suit.getAttributeSupplier().ifPresent(attributeSupplier -> {
                event.put((EntityType<? extends LivingEntity>) suit.getEntityType(), attributeSupplier);
            });
        });
    }

    @SubscribeEvent
    public static void addSpawnPlacements(RegisterSpawnPlacementsEvent event){
        EntitySuits.getSuits().forEach(suit -> suit.addPlacement(event));
    }

    @SubscribeEvent
    public static void fillInCreativeTab(BuildCreativeModeTabContentsEvent event) {
        HTLibStoneSuits.getSuits().forEach(suit -> suit.fillTabs(event));
        HTLibWoodSuits.getSuits().forEach(suit -> suit.fillTabs(event));
    }

    /**
     * {@link hungteen.htlib.HTLibNeoInitializer#onCommonSetup(FMLCommonSetupEvent)}。
     */
    public static void setUp() {
        /* Register Stripped Action */
        HTLibWoodSuits.registry().getValues().forEach(wood -> {
            wood.getBlockOpt(HTWoodSuit.HTWoodVariant.WOOD).ifPresent(block1 -> {
                wood.getBlockOpt(HTWoodSuit.HTWoodVariant.STRIPPED_WOOD).ifPresent(block2 -> {
                    BlockHelper.registerAxeStrip(block1, block2);
                });
            });
            wood.getBlockOpt(HTWoodSuit.HTWoodVariant.LOG).ifPresent(block1 -> {
                wood.getBlockOpt(HTWoodSuit.HTWoodVariant.STRIPPED_LOG).ifPresent(block2 -> {
                    BlockHelper.registerAxeStrip(block1, block2);
                });
            });
        });
    }

    @SubscribeEvent
    public static void clear(FMLLoadCompleteEvent event) {
        event.enqueueWork(() -> {
            HTLibStoneSuits.getSuits().forEach(HTStoneSuit::clear);
            HTLibWoodSuits.getSuits().forEach(HTWoodSuit::clear);
            EntitySuits.getSuits().forEach(HTEntitySuit::clear);
        });
    }
}
