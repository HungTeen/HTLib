package hungteen.htlib.common.registry.suit;

import hungteen.htlib.HTLib;
import hungteen.htlib.common.impl.BoatTypes;
import hungteen.htlib.util.helper.registry.BlockHelper;
import hungteen.htlib.util.helper.registry.ItemHelper;
import hungteen.htlib.util.interfaces.IBoatType;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.registries.RegisterEvent;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/7/4 16:58
 */
public class HTSuitHandler {

    /**
     * Register at the tail of event，{@link HTLib#HTLib()}
     */
    public static void register(RegisterEvent event) {
        StoneSuits.registry().getValues().forEach(suit -> suit.register(event));
        if(ItemHelper.get().matchEvent(event)){
            BoatTypes.registerBoatType(IBoatType.DEFAULT);
            TreeSuits.getTreeSuits().forEach(wood -> {
                BlockHelper.registerWoodType(wood.getWoodType());
                if (wood.getBoatSetting().isEnabled()) {
                    BoatTypes.registerBoatType(wood.getBoatSetting().getBoatType());
                }
            });
        }
        TreeSuits.getTreeSuits().forEach(wood -> wood.register(event));
    }

    /**
     * {@link HTLib#HTLib()}
     */
    public static void addAttributes(EntityAttributeCreationEvent event){
        EntitySuits.getSuits().forEach(suit -> suit.addAttribute(event));
    }

    /**
     * {@link HTLib#HTLib()}
     */
    public static void addSpawnPlacements(SpawnPlacementRegisterEvent event){
        EntitySuits.getSuits().forEach(suit -> suit.addPlacement(event));
    }

    /**
     * {@link HTLib#HTLib()}
     */
    public static void fillInCreativeTab(CreativeModeTabEvent.BuildContents event){
        TreeSuits.getTreeSuits().forEach(wood -> wood.fillSuits(event));
    }

    /**
     * {@link HTLib#setUp(FMLCommonSetupEvent)}
     */
    public static void setUp() {
        /* Register Stripped Action */
        TreeSuits.getTreeSuits().forEach(wood -> {
            wood.getBlockOpt(TreeSuits.HTWoodTypes.WOOD).ifPresent(block1 -> {
                wood.getBlockOpt(TreeSuits.HTWoodTypes.STRIPPED_WOOD).ifPresent(block2 -> {
                    BlockHelper.registerAxeStrip(block1, block2);
                });
            });
            wood.getBlockOpt(TreeSuits.HTWoodTypes.LOG).ifPresent(block1 -> {
                wood.getBlockOpt(TreeSuits.HTWoodTypes.STRIPPED_LOG).ifPresent(block2 -> {
                    BlockHelper.registerAxeStrip(block1, block2);
                });
            });
        });
    }

    /**
     * {@link HTLib#HTLib()}
     */
    public static void clear(FMLLoadCompleteEvent event) {
        event.enqueueWork(() -> {
            StoneSuits.registry().getValues().forEach(StoneSuits.StoneSuit::clear);
            TreeSuits.getTreeSuits().forEach(TreeSuits.TreeSuit::clear);
            EntitySuits.getSuits().forEach(EntitySuit::clear);
        });
    }
}
