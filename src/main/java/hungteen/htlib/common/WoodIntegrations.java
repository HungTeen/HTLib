package hungteen.htlib.common;

import hungteen.htlib.HTLib;
import hungteen.htlib.api.interfaces.ISimpleEntry;
import hungteen.htlib.common.block.*;
import hungteen.htlib.common.entity.HTBoat;
import hungteen.htlib.common.item.HTBoatDispenseItemBehavior;
import hungteen.htlib.common.item.HTBoatItem;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.htlib.util.Pair;
import hungteen.htlib.util.helper.BlockHelper;
import hungteen.htlib.util.helper.ItemHelper;
import hungteen.htlib.util.helper.StringHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegisterEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Used to resolve wood-related registrations at once. <br>
 * Register WoodType for Signs, BoatType for Boats, and related blocks and items at {@link #register(RegisterEvent)} <br>
 * Provide easy method in data gen, at {@link hungteen.htlib.data.HTBlockStateGen}, {@link hungteen.htlib.data.tag.HTItemTagGen}, {@link hungteen.htlib.data.HTBlockModelGen}, {@link hungteen.htlib.data.HTItemModelGen} <br>
 * Register axe strip behavior and boat dispense action at {@link #setUp()}
 *
 * @author PangTeen
 * @program HTLib
 * @data 2023/2/21 10:13
 */
public class WoodIntegrations {

    private static final HTSimpleRegistry<WoodIntegration> WOODS = HTRegistryManager.create(HTLib.prefix("wood"));
    private static final HTSimpleRegistry<IBoatType> BOAT_TYPES = HTRegistryManager.create(HTLib.prefix("boat_type"));

    /**
     * Register at the tail of event，{@link HTLib#HTLib()}
     */
    public static void register(RegisterEvent event) {
        registerBoatType(IBoatType.DEFAULT);
        getWoodIntegrations().forEach(wood -> {
            BlockHelper.registerWoodType(wood.getWoodType());
            if (wood.getBoatSetting().isEnabled()) {
                registerBoatType(wood.getBoatSetting().getBoatType());
            }
            wood.registerBlockAndItems(event);
        });
    }

    /**
     * {@link HTLib#setUp(FMLCommonSetupEvent)}
     */
    public static void setUp() {
        /* Register Boat Dispense Behavior */
        getBoatTypes().forEach(type -> {
            DispenserBlock.registerBehavior(type.getBoatItem(), new HTBoatDispenseItemBehavior(type, false));
            DispenserBlock.registerBehavior(type.getChestBoatItem(), new HTBoatDispenseItemBehavior(type, true));
        });
        /* Register Stripped Action */
        getWoodIntegrations().forEach(wood -> {
            wood.getBlockOpt(WoodSuits.WOOD).ifPresent(block1 -> {
                wood.getBlockOpt(WoodSuits.STRIPPED_WOOD).ifPresent(block2 -> {
                    BlockHelper.registerAxeStrip(block1, block2);
                });
            });
            wood.getBlockOpt(WoodSuits.LOG).ifPresent(block1 -> {
                wood.getBlockOpt(WoodSuits.STRIPPED_LOG).ifPresent(block2 -> {
                    BlockHelper.registerAxeStrip(block1, block2);
                });
            });
        });
    }

    /**
     * Usually other mod no need to use this method, because HTLib has done everything in {@link #register(RegisterEvent)}.
     */
    public static void registerBoatType(WoodIntegrations.IBoatType type) {
        BOAT_TYPES.register(type);
    }

    /**
     * Modders should call this method in their mod constructor.
     */
    public static void registerWoodIntegration(WoodIntegration type) {
        WOODS.register(type);
    }

    public static Collection<WoodIntegrations.IBoatType> getBoatTypes() {
        return Collections.unmodifiableCollection(BOAT_TYPES.getValues());
    }

    public static List<WoodIntegration> getWoodIntegrations() {
        return Collections.unmodifiableList(WOODS.getValues());
    }

    public static IBoatType getBoatType(String name) {
        return BOAT_TYPES.getValue(name).orElse(WoodIntegrations.IBoatType.DEFAULT);
    }

    public static WoodIntegration getWoodIntegration(String name) {
        return WOODS.getValue(name).orElse(null);
    }

    public static Builder builder(ResourceLocation location) {
        return new Builder(location);
    }

    public static class WoodIntegration implements ISimpleEntry {

        private final ResourceLocation registryName;
        private final EnumMap<WoodSuits, WoodSetting> woodSettings = new EnumMap<>(WoodSuits.class);
        private final EnumMap<WoodSuits, Block> woodBlocks = new EnumMap<>(WoodSuits.class);
        private final WoodType woodType;
        private final BoatSetting boatSetting;
        private final EnumMap<BoatSuits, Item> boatItems = new EnumMap<>(BoatSuits.class);

        /**
         * Use {@link Builder} instead.
         *
         * @param registryName such as "pvz:nut", former is mod id, latter is wood name.
         */
        private WoodIntegration(ResourceLocation registryName) {
            this.registryName = registryName;
            this.boatSetting = new BoatSetting(new IBoatType() {
                @Override
                public Block getPlanks() {
                    return WoodIntegration.this.woodBlocks.getOrDefault(WoodSuits.PLANKS, Blocks.AIR);
                }

                @Override
                public Item getBoatItem() {
                    return WoodIntegration.this.boatItems.getOrDefault(BoatSuits.NORMAL, ItemStack.EMPTY.getItem());
                }

                @Override
                public Item getChestBoatItem() {
                    return WoodIntegration.this.boatItems.getOrDefault(BoatSuits.CHEST, ItemStack.EMPTY.getItem());
                }

                @Override
                public String getName() {
                    return WoodIntegration.this.getName();
                }

                @Override
                public String getModID() {
                    return WoodIntegration.this.getModID();
                }
            });
            this.woodType = WoodType.create(registryName.toString());
            woodSettings.put(WoodSuits.LOG, new WoodSetting(
                    r -> StringHelper.suffix(r, "log"),
                    Block.Properties.copy(Blocks.OAK_LOG),
                    HTLogBlock::new
            ));
            woodSettings.put(WoodSuits.STRIPPED_LOG, new WoodSetting(
                    r -> StringHelper.update(r, "stripped", "log"),
                    Block.Properties.copy(Blocks.STRIPPED_OAK_LOG),
                    HTLogBlock::new
            ));
            woodSettings.put(WoodSuits.WOOD, new WoodSetting(
                    r -> StringHelper.suffix(r, "wood"),
                    Block.Properties.copy(Blocks.OAK_WOOD),
                    HTLogBlock::new
            ));
            woodSettings.put(WoodSuits.STRIPPED_WOOD, new WoodSetting(
                    r -> StringHelper.update(r, "stripped", "wood"),
                    Block.Properties.copy(Blocks.STRIPPED_OAK_WOOD),
                    HTLogBlock::new
            ));
            woodSettings.put(WoodSuits.PLANKS, new WoodSetting(
                    r -> StringHelper.suffix(r, "planks"),
                    Block.Properties.copy(Blocks.OAK_PLANKS),
                    p -> new HTBurnBlock(p, 5, 20)
            ));
            woodSettings.put(WoodSuits.DOOR, new WoodSetting(
                    r -> StringHelper.suffix(r, "door"),
                    Block.Properties.copy(Blocks.OAK_DOOR),
                    DoorBlock::new
            ));
            woodSettings.put(WoodSuits.TRAP_DOOR, new WoodSetting(
                    r -> StringHelper.suffix(r, "trapdoor"),
                    Block.Properties.copy(Blocks.OAK_TRAPDOOR),
                    TrapDoorBlock::new
            ));
            woodSettings.put(WoodSuits.FENCE, new WoodSetting(
                    r -> StringHelper.suffix(r, "fence"),
                    Block.Properties.copy(Blocks.OAK_FENCE),
                    HTFenceBlock::new
            ));
            woodSettings.put(WoodSuits.FENCE_GATE, new WoodSetting(
                    r -> StringHelper.suffix(r, "fence_gate"),
                    Block.Properties.copy(Blocks.OAK_FENCE_GATE),
                    HTFenceGateBlock::new
            ));
            woodSettings.put(WoodSuits.STANDING_SIGN, new WoodSetting(
                    r -> StringHelper.suffix(r, "sign"),
                    Block.Properties.copy(Blocks.OAK_SIGN),
                    p -> new HTStandingSignBlock(p, WoodIntegration.this.woodType),
                    new Item.Properties().stacksTo(16).tab(CreativeModeTab.TAB_DECORATIONS),
                    (block, po) -> new SignItem(po, block, WoodIntegration.this.woodBlocks.get(WoodSuits.WALL_SIGN))
            ));
            woodSettings.put(WoodSuits.WALL_SIGN, new WoodSetting(
                    r -> StringHelper.suffix(r, "wall_sign"),
                    Block.Properties.copy(Blocks.OAK_SIGN),
                    p -> new HTWallSignBlock(p, this.woodType)
            ));
            woodSettings.put(WoodSuits.STAIRS, new WoodSetting(
                    r -> StringHelper.suffix(r, "stairs"),
                    Block.Properties.copy(Blocks.OAK_STAIRS),
                    p -> new HTStairBlock(woodBlocks.get(WoodSuits.PLANKS))
            ));
            woodSettings.put(WoodSuits.BUTTON, new WoodSetting(
                    r -> StringHelper.suffix(r, "button"),
                    Block.Properties.copy(Blocks.OAK_BUTTON),
                    WoodButtonBlock::new
            ));
            woodSettings.put(WoodSuits.SLAB, new WoodSetting(
                    r -> StringHelper.suffix(r, "slab"),
                    Block.Properties.copy(Blocks.OAK_SLAB),
                    HTSlabBlock::new
            ));
            woodSettings.put(WoodSuits.PRESSURE_PLATE, new WoodSetting(
                    r -> StringHelper.suffix(r, "pressure_plate"),
                    Block.Properties.copy(Blocks.OAK_PRESSURE_PLATE),
                    p -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, p)
            ));
        }

        private void registerBlockAndItems(RegisterEvent event) {
            woodSettings.forEach((suit, entry) -> {
                BlockHelper.get().register(event, entry.getName(getLocation()), () -> {
                    final Block block = entry.getSupplier().get();
                    woodBlocks.put(suit, block);
                    return block;
                });
                if (suit.hasItem) {
                    ItemHelper.get().register(event, entry.getName(getLocation()), () -> {
                        final Optional<Block> block = getBlockOpt(suit);
                        assert block.isPresent() : "Why cause block item registry failed ? No block of %s : %s found.".formatted(getLocation().toString(), suit.toString());
                        return entry.getItemFunction().apply(block.get(), entry.getItemProperties());
                    });
                }
            });
            if (getBoatSetting().isEnabled()) {
                ItemHelper.get().register(event, StringHelper.suffix(getLocation(), "boat"), () -> {
                    final Item item = new HTBoatItem(getBoatSetting().getItemProperties(), this.getBoatSetting().getBoatType(), false);
                    this.boatItems.put(BoatSuits.NORMAL, item);
                    return item;
                });
                ItemHelper.get().register(event, StringHelper.suffix(getLocation(), "chest_boat"), () -> {
                    final Item item = new HTBoatItem(getBoatSetting().getItemProperties(), this.getBoatSetting().getBoatType(), true);
                    this.boatItems.put(BoatSuits.CHEST, item);
                    return item;
                });
            }
        }

        private void updateWoodBlock(WoodSuits woodSuits, Consumer<BlockBehaviour.Properties> consumer) {
            this.getWoodSetting(woodSuits).ifPresent(suit -> {
                suit.blockProperties(consumer);
            });
        }

        private void updateWoodItem(WoodSuits woodSuits, Consumer<Item.Properties> consumer) {
            this.getWoodSetting(woodSuits).ifPresent(suit -> {
                suit.itemProperties(consumer);
            });
        }

        private void updateBoatItems(Consumer<Item.Properties> consumer) {
            this.boatSetting.itemProperties(consumer);
        }

        private void setBlockFunction(WoodSuits woodSuits, @NotNull Function<BlockBehaviour.Properties, Block> function) {
            this.getWoodSetting(woodSuits).ifPresent(suit -> {
                suit.setBlockFunction(function);
            });
        }

        private void banWoodSuits(WoodSuits... woodSuits) {
            Arrays.stream(woodSuits).forEach(this.woodSettings::remove);
        }

        private void banBoat() {
            this.boatSetting.disable();
        }

        public List<Pair<WoodSuits, Block>> getWoodBlocks() {
            return this.woodBlocks.entrySet().stream().map(Pair::of).toList();
        }

        public boolean hasWoodSuit(WoodSuits woodSuit) {
            return this.woodBlocks.containsKey(woodSuit);
        }

        public Optional<Block> getBlockOpt(WoodSuits woodSuit) {
            return Optional.ofNullable(this.woodBlocks.getOrDefault(woodSuit, null));
        }

        @NotNull
        public Block getBlock(WoodSuits woodSuit) {
            return Objects.requireNonNull(this.woodBlocks.get(woodSuit), "Wood type [ " + this.getRegistryName() + " ] has no block for suit [" + woodSuit.toString() + "] !");
        }

        public Block getLog() {
            return getBlock(WoodSuits.LOG);
        }

        public Block getStrippedLog() {
            return getBlock(WoodSuits.STRIPPED_LOG);
        }

        public Block getWood() {
            return getBlock(WoodSuits.WOOD);
        }

        public Block getStrippedWood() {
            return getBlock(WoodSuits.STRIPPED_WOOD);
        }

        public Block getPlanks() {
            return getBlock(WoodSuits.PLANKS);
        }

        public Block getDoor() {
            return getBlock(WoodSuits.DOOR);
        }

        public Block getTrapDoor() {
            return getBlock(WoodSuits.TRAP_DOOR);
        }

        public Block getFence() {
            return getBlock(WoodSuits.FENCE);
        }

        public Block getFenceGate() {
            return getBlock(WoodSuits.FENCE_GATE);
        }

        public Block getStandingSign() {
            return getBlock(WoodSuits.STANDING_SIGN);
        }

        public Block getWallSign() {
            return getBlock(WoodSuits.WALL_SIGN);
        }

        public Block getStairs() {
            return getBlock(WoodSuits.STAIRS);
        }

        public Block getButton() {
            return getBlock(WoodSuits.BUTTON);
        }

        public Block getSlab() {
            return getBlock(WoodSuits.SLAB);
        }

        public Block getPressurePlate() {
            return getBlock(WoodSuits.PRESSURE_PLATE);
        }

        public Optional<WoodSetting> getWoodSetting(WoodSuits woodSuits) {
            return Optional.ofNullable(woodSettings.getOrDefault(woodSuits, null));
        }

        public BoatSetting getBoatSetting() {
            return boatSetting;
        }

        public WoodType getWoodType() {
            return woodType;
        }

        @Override
        public String getName() {
            return this.registryName.getPath();
        }

        @Override
        public String getModID() {
            return this.registryName.getNamespace();
        }

    }

    public static class Builder {
        private final WoodIntegration integration;

        public Builder(ResourceLocation registryLocation) {
            this.integration = new WoodIntegration(registryLocation);
        }

        /**
         * 取消一些木制品方块。
         */
        public Builder banWoodSuits(WoodSuits... woodSuits) {
            integration.banWoodSuits(woodSuits);
            return this;
        }

        /**
         * Ban boat-related registrations.
         */
        public Builder banBoat() {
            integration.banBoat();
            return this;
        }

        /**
         * Update all suit block properties.
         */
        public Builder updateBlocks(Consumer<BlockBehaviour.Properties> consumer) {
            for (WoodSuits suit : WoodSuits.values()) {
                updateBlock(suit, consumer);
            }
            return this;
        }

        public Builder updateBlockStrengths(float multiplier) {
            return updateBlockStrengths(multiplier, multiplier);
        }

        /**
         * @param multiplier1 base on vanilla wood destroy time.
         * @param multiplier2 base on vanilla wood explosion resistance.
         */
        public Builder updateBlockStrengths(float multiplier1, float multiplier2) {
            for (WoodSuits suit : WoodSuits.values()) {
                updateBlockStrength(suit, suit.destroyTime * multiplier1, suit.explosionResistance * multiplier2);
            }
            return this;
        }

        public Builder updateBlockStrength(WoodSuits woodSuits, float strength) {
            integration.updateWoodBlock(woodSuits, p -> p.strength(strength));
            return this;
        }

        public Builder updateBlockStrength(WoodSuits woodSuits, float strength, float resistance) {
            integration.updateWoodBlock(woodSuits, p -> p.strength(strength, resistance));
            return this;
        }

        public Builder updateBlock(WoodSuits woodSuits, Consumer<BlockBehaviour.Properties> consumer) {
            integration.updateWoodBlock(woodSuits, consumer);
            return this;
        }

        public Builder updateWoodItems(Consumer<Item.Properties> consumer) {
            for (WoodSuits suit : WoodSuits.values()) {
                integration.updateWoodItem(suit, consumer);
            }
            return this;
        }

        public Builder updateWoodFunction(WoodSuits woodSuits, Function<BlockBehaviour.Properties, Block> function) {
            integration.setBlockFunction(woodSuits, function);
            return this;
        }

        public Builder updateBoatItems(Consumer<Item.Properties> consumer) {
            integration.updateBoatItems(consumer);
            return this;
        }

        public WoodIntegration build() {
            return this.integration;
        }

    }

    /**
     * Copy from {@link net.minecraft.world.entity.vehicle.Boat.Type}.
     *
     * @program: HTLib
     * @author: HungTeen
     * @create: 2022-10-13 22:41
     **/
    public interface IBoatType extends ISimpleEntry {

        IBoatType DEFAULT = new IBoatType() {
            @Override
            public String getName() {
                return "oak";
            }

            @Override
            public String getModID() {
                return "minecraft";
            }

            @Override
            public Block getPlanks() {
                return Blocks.OAK_PLANKS;
            }

            @Override
            public Item getBoatItem() {
                return Items.OAK_BOAT;
            }

            @Override
            public Item getChestBoatItem() {
                return Items.OAK_CHEST_BOAT;
            }
        };

        Block getPlanks();

        /**
         * {@link HTBoat#getDropItem()}
         */
        Item getBoatItem();

        /**
         * {@link hungteen.htlib.common.entity.HTChestBoat#getDropItem()}
         */
        Item getChestBoatItem();

    }

    public static class WoodSetting {

        private final Function<ResourceLocation, ResourceLocation> getName;
        private final BlockBehaviour.Properties blockProperties;
        private final Item.Properties itemProperties;
        private Function<BlockBehaviour.Properties, Block> blockFunction;
        private BiFunction<Block, Item.Properties, BlockItem> itemFunction;

        WoodSetting(Function<ResourceLocation, ResourceLocation> getName, BlockBehaviour.Properties blockProperties, Function<BlockBehaviour.Properties, Block> blockFunction) {
            this(getName, blockProperties, blockFunction, new Item.Properties(), ItemNameBlockItem::new);
        }

        WoodSetting(Function<ResourceLocation, ResourceLocation> getName, BlockBehaviour.Properties blockProperties, Function<BlockBehaviour.Properties, Block> blockFunction, Item.Properties itemProperties, BiFunction<Block, Item.Properties, BlockItem> itemFunction) {
            this.getName = getName;
            this.blockProperties = blockProperties;
            this.itemProperties = itemProperties;
            this.blockFunction = blockFunction;
            this.itemFunction = itemFunction;
        }

        public void itemProperties(Consumer<Item.Properties> consumer) {
            consumer.accept(itemProperties);
        }

        public Item.Properties getItemProperties() {
            return itemProperties;
        }

        public void blockProperties(Consumer<BlockBehaviour.Properties> consumer) {
            consumer.accept(blockProperties);
        }

        public BlockBehaviour.Properties getBlockProperties() {
            return blockProperties;
        }

        public ResourceLocation getName(ResourceLocation location) {
            return getName.apply(location);
        }

        public void setBlockFunction(@NotNull Function<BlockBehaviour.Properties, Block> blockFunction) {
            this.blockFunction = blockFunction;
        }

        public Supplier<Block> getSupplier() {
            return () -> this.blockFunction.apply(this.getBlockProperties());
        }

        public BiFunction<Block, Item.Properties, BlockItem> getItemFunction() {
            return itemFunction;
        }
    }

    public static class BoatSetting {

        private final Item.Properties itemProperties;
        private final IBoatType boatType;
        private boolean enabled = true;

        BoatSetting(IBoatType boatType) {
            this.itemProperties = new Item.Properties().stacksTo(16).tab(CreativeModeTab.TAB_TRANSPORTATION);
            this.boatType = boatType;
        }

        public void itemProperties(Consumer<Item.Properties> consumer) {
            consumer.accept(itemProperties);
        }

        public Item.Properties getItemProperties() {
            return itemProperties;
        }

        /**
         * Ban boat items.
         */
        public void disable() {
            this.enabled = false;
        }

        public boolean isEnabled() {
            return enabled;
        }

        @Nullable
        public IBoatType getBoatType() {
            return boatType;
        }
    }

    public enum WoodSuits {
        LOG(2F),
        STRIPPED_LOG(2F),
        WOOD(2F),
        STRIPPED_WOOD(2F),
        PLANKS(2F, 3F),
        DOOR(3F),
        TRAP_DOOR(3F),
        FENCE(2F, 3F),
        FENCE_GATE(2F, 3F),
        STANDING_SIGN(1F),
        WALL_SIGN(1F, 1F, false),
        STAIRS(2F, 3F),
        BUTTON(0.5F),
        SLAB(2F, 3F),
        PRESSURE_PLATE(0.5F);

        private final float destroyTime;
        private final float explosionResistance;
        private final boolean hasItem;

        WoodSuits(float strength) {
            this(strength, strength);
        }

        WoodSuits(float strength, float explosionResistance) {
            this(strength, explosionResistance, true);
        }

        WoodSuits(float strength, float explosionResistance, boolean hasItem) {
            this.destroyTime = strength;
            this.explosionResistance = explosionResistance;
            this.hasItem = hasItem;
        }

        public boolean hasItem() {
            return this.hasItem;
        }
    }

    public enum BoatSuits {
        NORMAL,
        CHEST;
    }

}
