package hungteen.htlib.common;

import hungteen.htlib.common.impl.BoatTypes;
import hungteen.htlib.common.impl.registry.suit.EntitySuit;
import hungteen.htlib.common.impl.registry.suit.EntitySuits;
import hungteen.htlib.common.impl.registry.suit.StoneSuits;
import hungteen.htlib.common.impl.registry.suit.TreeSuits;
import hungteen.htlib.common.item.HTBoatItem;
import hungteen.htlib.util.helper.StringHelper;
import hungteen.htlib.util.helper.impl.BlockHelper;
import hungteen.htlib.util.BoatType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.util.Optional;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/7/4 16:58
 */
public class HTSuitHandler {

    /**
     * Register at the tail of event.
     */
    public static void register(RegisterEvent event) {
        if (ForgeRegistries.ITEMS.getRegistryKey().equals(event.getRegistryKey())
                || ForgeRegistries.BLOCKS.getRegistryKey().equals(event.getRegistryKey())) {
            StoneSuits.registry().getValues().forEach(suit -> {
                suit.getSettingMap().forEach((type, setting) -> {
                    event.register(ForgeRegistries.BLOCKS.getRegistryKey(), setting.getName(suit.getLocation()), () -> {
                        final Block block = setting.getSupplier().get();
                        suit.getBlockMap().put(type, block);
                        return block;
                    });
                    if (setting.hasItem()) {
                        event.register(ForgeRegistries.ITEMS.getRegistryKey(), setting.getName(suit.getLocation()), () -> {
                            final Optional<Block> block = suit.getBlockOpt(type);
                            assert block.isPresent() : "Why cause block item registry failed ? No block of %s : %s found.".formatted(suit.getLocation().toString(), type.toString());
                            return setting.getItemFunction().apply(block.get(), setting.getItemProperties());
                        });
                    }
                });
            });

        }
        if (ForgeRegistries.ITEMS.getRegistryKey().equals(event.getRegistryKey())) {
            BoatTypes.registerBoatType(BoatType.DEFAULT);
            TreeSuits.getTreeSuits().forEach(wood -> {
                BlockHelper.registerWoodType(wood.getWoodType());
                if (wood.getBoatSetting().isEnabled()) {
                    BoatTypes.registerBoatType(wood.getBoatSetting().getBoatType());
                }
            });
        }
        if (ForgeRegistries.ITEMS.getRegistryKey().equals(event.getRegistryKey())) {
            TreeSuits.registry().getValues().forEach(suit -> {
                if (suit.getBoatSetting().isEnabled()) {
                    event.register(ForgeRegistries.ITEMS.getRegistryKey(), StringHelper.suffix(suit.getLocation(), "boat"), () -> {
                        final Item item = new HTBoatItem(suit.getBoatSetting().getItemProperties(), suit.getBoatSetting().getBoatType(), false);
                        suit.putBoatItem(TreeSuits.HTBoatStyles.NORMAL, item);
                        return item;
                    });
                    event.register(ForgeRegistries.ITEMS.getRegistryKey(), StringHelper.suffix(suit.getLocation(), "chest_boat"), () -> {
                        final Item item = new HTBoatItem(suit.getBoatSetting().getItemProperties(), suit.getBoatSetting().getBoatType(), true);
                        suit.putBoatItem(TreeSuits.HTBoatStyles.CHEST, item);
                        return item;
                    });
                }
            });
        }
    }

    public static void addAttributes(EntityAttributeCreationEvent event) {
        EntitySuits.getSuits().forEach(suit -> {
            suit.getAttributeSupplier().ifPresent(attributeSupplier -> {
                event.put((EntityType<? extends LivingEntity>) suit.getEntityType(), attributeSupplier);
            });
        });
    }

//    public static void addSpawnPlacements(SpawnPlacementRegisterEvent event){
//        EntitySuits.getSuits().forEach(suit -> suit.addPlacement(event));
//    }

    public static void fillInCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) {
            for (TreeSuits.TreeSuit wood : TreeSuits.getTreeSuits()) {
                wood.getBoatItems().forEach((suit, item) -> {
                    event.accept(new ItemStack(item), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                });
            }
        }
    }

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

    public static void clear(FMLLoadCompleteEvent event) {
        event.enqueueWork(() -> {
            StoneSuits.registry().getValues().forEach(StoneSuits.StoneSuit::clear);
            TreeSuits.getTreeSuits().forEach(TreeSuits.TreeSuit::clear);
            EntitySuits.getSuits().forEach(EntitySuit::clear);
        });
    }
}
