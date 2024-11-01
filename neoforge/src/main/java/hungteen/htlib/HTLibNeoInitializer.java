package hungteen.htlib;

import hungteen.htlib.api.HTLibAPI;
import hungteen.htlib.common.HTLibNeoRegistryHandler;
import hungteen.htlib.common.HTResourceManager;
import hungteen.htlib.common.command.HTLibCommand;
import hungteen.htlib.common.impl.HTLibBoatTypes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(HTLibAPI.MOD_ID)
public class HTLibNeoInitializer
{
//    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
//    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
//    // Create a Deferred Register to hold Items which will all be registered under the "examplemod" namespace
//    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
//    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "examplemod" namespace
//    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
//
//    // Creates a new Block with the id "examplemod:example_block", combining the namespace and path
//    public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
//    // Creates a new BlockItem with the id "examplemod:example_block", combining the namespace and path
//    public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block", EXAMPLE_BLOCK);
//
//    // Creates a new food item with the id "examplemod:example_id", nutrition 1 and saturation 2
//    public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item", new Item.Properties().food(new FoodProperties.Builder()
//            .alwaysEdible().nutrition(1).saturationModifier(2f).build()));
//
//    // Creates a creative tab with the id "examplemod:example_tab" for the example item, that is placed after the combat tab
//    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.initialize("example_tab", () -> CreativeModeTab.builder()
//            .title(Component.translatable("itemGroup.examplemod")) //The language key for the title of your CreativeModeTab
//            .withTabsBefore(CreativeModeTabs.COMBAT)
//            .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
//            .displayItems((parameters, output) -> {
//                output.accept(EXAMPLE_ITEM.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
//            }).build());

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public HTLibNeoInitializer(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(HTLibNeoInitializer::onCommonSetup);
//        modBus.addListener(EventPriority.LOW, HTSuitHandler::initialize);
//        modBus.addListener(HTSuitHandler::clear);
//        modBus.addListener(HTDataGenHandler::gatherData);
//        modBus.addListener(HTSuitHandler::fillInCreativeTab);
//        modBus.addListener(HTSuitHandler::addAttributes);
//        modBus.addListener(HTSuitHandler::addSpawnPlacements);
//        modBus.addListener(HTSuitHandler::clear);
        HTLibNeoRegistryHandler.register(modBus);

        /* Neo Bus Events */
        IEventBus forgeBus = NeoForge.EVENT_BUS;
//        forgeBus.addListener(DummyEntityHandler::tick);
//        forgeBus.addListener(PlayerCapabilityManager::tick);
        forgeBus.addListener(HTLibNeoRegistryHandler::onDatapackSync);
        forgeBus.addListener((RegisterCommandsEvent event) -> HTLibCommand.register(event.getDispatcher(), event.getBuildContext()));
    }

    public static void onCommonSetup(FMLCommonSetupEvent event) {
        HTResourceManager.init();
        HTLibNeoRegistryHandler.onCommonSetup();
        event.enqueueWork(() -> {
            HTLibBoatTypes.register();
//            HTSuitHandler.setUp();
        });
    }



}
