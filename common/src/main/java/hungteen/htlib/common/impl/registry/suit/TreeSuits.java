package hungteen.htlib.common.impl.registry.suit;

import hungteen.htlib.api.registry.HTSimpleRegistry;
import hungteen.htlib.common.block.entityblock.HTHangingSignBlock;
import hungteen.htlib.common.block.entityblock.HTStandingSignBlock;
import hungteen.htlib.common.block.entityblock.HTWallHangingSignBlock;
import hungteen.htlib.common.block.entityblock.HTWallSignBlock;
import hungteen.htlib.common.block.wood.*;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.common.impl.registry.HTSimpleRegistryImpl;
import hungteen.htlib.util.helper.impl.HTLibHelper;
import hungteen.htlib.util.helper.StringHelper;
import hungteen.htlib.util.helper.impl.BlockHelper;
import hungteen.htlib.util.helper.impl.ItemHelper;
import hungteen.htlib.util.BoatType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Used to resolve wood-related registrations at once. <br>
 * Register WoodType for Signs, BoatType for Boats, and related blocks and items <br>
 * Provide easy method in data gen, at <b>BlockStateGen</b>, {@link hungteen.htlib.data.tag.HTItemTagGen}, <b>BlockModelGen, HTItemModelGen</b> <br>
 * Register axe strip behavior and boat dispense action at. <br>
 * @author PangTeen
 * @program HTLib
 * @data 2023/2/21 10:13
 */
public class TreeSuits {

    private static final HTSimpleRegistryImpl<TreeSuit> WOODS = HTRegistryManager.simple(HTLibHelper.prefix("wood"));

    /**
     * Modders should call this method in their mod constructor.
     */
    public static TreeSuit registerWoodIntegration(TreeSuit type) {
        return registry().register(type);
    }

    public static Collection<TreeSuit> getTreeSuits() {
        return Collections.unmodifiableCollection(registry().getValues());
    }

    public static HTSimpleRegistry<TreeSuit> registry(){
        return WOODS;
    }

    public static Set<Block> getSignBlocks(){
        final Set<Block> blocks = new HashSet<>();
        getTreeSuits().forEach(wood -> {
            wood.getBlockOpt(HTWoodTypes.STANDING_SIGN).ifPresent(blocks::add);
            wood.getBlockOpt(HTWoodTypes.WALL_SIGN).ifPresent(blocks::add);
        });
        return blocks;
    }

    public static Set<Block> getHangingSignBlocks(){
        final Set<Block> blocks = new HashSet<>();
        getTreeSuits().forEach(wood -> {
            wood.getBlockOpt(HTWoodTypes.HANGING_SIGN).ifPresent(blocks::add);
            wood.getBlockOpt(HTWoodTypes.WALL_HANGING_SIGN).ifPresent(blocks::add);
        });
        return blocks;
    }

    public static TreeSuit getWoodIntegration(String name) {
        return registry().getValue(name).orElse(null);
    }

    public static Builder builder(ResourceLocation location) {
        return new Builder(location);
    }

    public static class TreeSuit extends BlockSuit<HTWoodTypes> {

        private final EnumMap<HTWoodTypes, HTBlockSetting> woodSettings = new EnumMap<>(HTWoodTypes.class);
        private final EnumMap<HTWoodTypes, Block> woodBlocks = new EnumMap<>(HTWoodTypes.class);
        private final EnumMap<HTBoatStyles, Item> boatItems = new EnumMap<>(HTBoatStyles.class);
        private final BoatSetting boatSetting;
        private final BlockSetType blockSetType;
        private final WoodType woodType;
        private TagKey<Item> logItemTag;
        private TagKey<Block> logBlockTag;

        /**
         * Use {@link Builder} instead.
         * @param registryName such as "pvz:nut", former is mod id, latter is wood name.
         */
        private TreeSuit(ResourceLocation registryName) {
            super(registryName);
            this.boatSetting = new BoatSetting(new BoatType() {
                @Override
                public Block getPlanks() {
                    return TreeSuit.this.woodBlocks.getOrDefault(HTWoodTypes.PLANKS, Blocks.AIR);
                }

                @Override
                public Item getBoatItem() {
                    return TreeSuit.this.boatItems.getOrDefault(HTBoatStyles.NORMAL, ItemStack.EMPTY.getItem());
                }

                @Override
                public Item getChestBoatItem() {
                    return TreeSuit.this.boatItems.getOrDefault(HTBoatStyles.CHEST, ItemStack.EMPTY.getItem());
                }

                @Override
                public String name() {
                    return TreeSuit.this.getName();
                }

                @Override
                public String getModID() {
                    return TreeSuit.this.getModID();
                }
            });
            this.blockSetType = new BlockSetType(registryName.toString());
            this.woodType = new WoodType(registryName.toString(), this.blockSetType);
            woodSettings.put(HTWoodTypes.LOG, new HTBlockSetting(
                    r -> StringHelper.suffix(r, "log"),
                    Block.Properties.ofFullCopy(Blocks.OAK_LOG),
                    RotatedPillarBlock::new
            ));
            woodSettings.put(HTWoodTypes.STRIPPED_LOG, new HTBlockSetting(
                    r -> StringHelper.expand(r, "stripped", "log"),
                    Block.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG),
                    RotatedPillarBlock::new
            ));
            woodSettings.put(HTWoodTypes.WOOD, new HTBlockSetting(
                    r -> StringHelper.suffix(r, "wood"),
                    Block.Properties.ofFullCopy(Blocks.OAK_WOOD),
                    RotatedPillarBlock::new
            ));
            woodSettings.put(HTWoodTypes.STRIPPED_WOOD, new HTBlockSetting(
                    r -> StringHelper.expand(r, "stripped", "wood"),
                    Block.Properties.ofFullCopy(Blocks.STRIPPED_OAK_WOOD),
                    RotatedPillarBlock::new
            ));
            woodSettings.put(HTWoodTypes.PLANKS, new HTBlockSetting(
                    r -> StringHelper.suffix(r, "planks"),
                    Block.Properties.ofFullCopy(Blocks.OAK_PLANKS),
                    Block::new
            ));
            woodSettings.put(HTWoodTypes.DOOR, new HTBlockSetting(
                    r -> StringHelper.suffix(r, "door"),
                    Block.Properties.ofFullCopy(Blocks.OAK_DOOR),
                    properties -> new HTDoorBlock(this.blockSetType, properties)
            ));
            woodSettings.put(HTWoodTypes.TRAP_DOOR, new HTBlockSetting(
                    r -> StringHelper.suffix(r, "trapdoor"),
                    Block.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR),
                    properties -> new HTTrapDoorBlock(this.blockSetType, properties)
            ));
            woodSettings.put(HTWoodTypes.FENCE, new HTBlockSetting(
                    r -> StringHelper.suffix(r, "fence"),
                    Block.Properties.ofFullCopy(Blocks.OAK_FENCE),
                    FenceBlock::new
            ));
            woodSettings.put(HTWoodTypes.FENCE_GATE, new HTBlockSetting(
                    r -> StringHelper.suffix(r, "fence_gate"),
                    Block.Properties.ofFullCopy(Blocks.OAK_FENCE_GATE),
                    properties -> new FenceGateBlock(this.woodType, properties)
            ));
            woodSettings.put(HTWoodTypes.STANDING_SIGN, new HTBlockSetting(
                    r -> StringHelper.suffix(r, "sign"),
                    Block.Properties.ofFullCopy(Blocks.OAK_SIGN),
                    p -> new HTStandingSignBlock(p, TreeSuit.this.woodType),
                    new Item.Properties().stacksTo(16),
                    (block, po) -> new SignItem(po, block, TreeSuit.this.woodBlocks.get(HTWoodTypes.WALL_SIGN))
            ));
            woodSettings.put(HTWoodTypes.WALL_SIGN, new HTBlockSetting(
                    r -> StringHelper.suffix(r, "wall_sign"),
                    Block.Properties.ofFullCopy(Blocks.OAK_SIGN),
                    p -> new HTWallSignBlock(p, this.woodType)
            ));
            woodSettings.put(HTWoodTypes.HANGING_SIGN, new HTBlockSetting(
                    r -> StringHelper.suffix(r, "hanging_sign"),
                    Block.Properties.ofFullCopy(Blocks.OAK_HANGING_SIGN),
                    p -> new HTHangingSignBlock(p, this.woodType),
                    new Item.Properties().stacksTo(16),
                    (block, po) -> new HangingSignItem(block, TreeSuit.this.woodBlocks.get(HTWoodTypes.WALL_HANGING_SIGN), po)
            ));
            woodSettings.put(HTWoodTypes.WALL_HANGING_SIGN, new HTBlockSetting(
                    r -> StringHelper.suffix(r, "wall_hanging_sign"),
                    Block.Properties.ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN),
                    p -> new HTWallHangingSignBlock(p, this.woodType)
            ));
            woodSettings.put(HTWoodTypes.STAIRS, new HTBlockSetting(
                    r -> StringHelper.suffix(r, "stairs"),
                    Block.Properties.ofFullCopy(Blocks.OAK_STAIRS),
                    p -> new HTStairBlock(getBlock(HTWoodTypes.PLANKS).defaultBlockState(), p)
            ));
            woodSettings.put(HTWoodTypes.BUTTON, new HTBlockSetting(
                    r -> StringHelper.suffix(r, "button"),
                    Block.Properties.ofFullCopy(Blocks.OAK_BUTTON),
                    properties -> new HTButtonBlock(this.blockSetType, 30, properties)
            ));
            woodSettings.put(HTWoodTypes.SLAB, new HTBlockSetting(
                    r -> StringHelper.suffix(r, "slab"),
                    Block.Properties.ofFullCopy(Blocks.OAK_SLAB),
                    SlabBlock::new
            ));
            woodSettings.put(HTWoodTypes.PRESSURE_PLATE, new HTBlockSetting(
                    r -> StringHelper.suffix(r, "pressure_plate"),
                    Block.Properties.ofFullCopy(Blocks.OAK_PRESSURE_PLATE),
                    properties -> new HTPressurePlateBlock(this.blockSetType, properties)
            ));

            final ResourceLocation tagLocation = StringHelper.suffix(this.getLocation(), "logs");
            this.logItemTag = ItemHelper.get().tag(tagLocation);
            this.logBlockTag = BlockHelper.get().tag(tagLocation);
        }

        public EnumMap<HTBoatStyles, Item> getBoatItems() {
            return boatItems;
        }

        public void putBoatItem(HTBoatStyles boatSuit, Item item){
            this.boatItems.put(boatSuit, item);
        }

        @Override
        protected Map<HTWoodTypes, HTBlockSetting> getSettingMap() {
            return this.woodSettings;
        }

        @Override
        protected Map<HTWoodTypes, Block> getBlockMap() {
            return this.woodBlocks;
        }

        private void updateBoatItems(Consumer<Item.Properties> consumer) {
            this.boatSetting.itemProperties(consumer);
        }

        private void banBoat() {
            this.boatSetting.disable();
        }

        public Optional<Item> getBoatItem(HTBoatStyles boatSuit){
            return Optional.ofNullable(this.boatItems.getOrDefault(boatSuit, null));
        }

        @NotNull
        public Item getBoat(HTBoatStyles boatSuit) {
            return getBoatItem(boatSuit).orElseThrow();
        }

        public Optional<TagKey<Item>> getLogItemTag(){
            return Optional.ofNullable(this.logItemTag);
        }

        public Optional<TagKey<Block>> getLogBlockTag(){
            return Optional.ofNullable(this.logBlockTag);
        }

        public Block getLog() {
            return getBlock(HTWoodTypes.LOG);
        }

        public Block getStrippedLog() {
            return getBlock(HTWoodTypes.STRIPPED_LOG);
        }

        public Block getWood() {
            return getBlock(HTWoodTypes.WOOD);
        }

        public Block getStrippedWood() {
            return getBlock(HTWoodTypes.STRIPPED_WOOD);
        }

        public Block getPlanks() {
            return getBlock(HTWoodTypes.PLANKS);
        }

        public Block getDoor() {
            return getBlock(HTWoodTypes.DOOR);
        }

        public Block getTrapDoor() {
            return getBlock(HTWoodTypes.TRAP_DOOR);
        }

        public Block getFence() {
            return getBlock(HTWoodTypes.FENCE);
        }

        public Block getFenceGate() {
            return getBlock(HTWoodTypes.FENCE_GATE);
        }

        public Block getStandingSign() {
            return getBlock(HTWoodTypes.STANDING_SIGN);
        }

        public Block getWallSign() {
            return getBlock(HTWoodTypes.WALL_SIGN);
        }

        public Block getHangingSign() {
            return getBlock(HTWoodTypes.HANGING_SIGN);
        }

        public Block getWallHangingSign() {
            return getBlock(HTWoodTypes.WALL_HANGING_SIGN);
        }

        public Block getStairs() {
            return getBlock(HTWoodTypes.STAIRS);
        }

        public Block getButton() {
            return getBlock(HTWoodTypes.BUTTON);
        }

        public Block getSlab() {
            return getBlock(HTWoodTypes.SLAB);
        }

        public Block getPressurePlate() {
            return getBlock(HTWoodTypes.PRESSURE_PLATE);
        }

        public Item getBoatItem(){
            return getBoat(HTBoatStyles.NORMAL);
        }

        public Item getChestBoatItem(){
            return getBoat(HTBoatStyles.CHEST);
        }

        public BoatSetting getBoatSetting() {
            return boatSetting;
        }

        public WoodType getWoodType() {
            return woodType;
        }

        public BlockSetType getBlockSetType() {
            return blockSetType;
        }
    }

    public static class Builder {
        private final TreeSuit integration;

        public Builder(ResourceLocation registryLocation) {
            this.integration = new TreeSuit(registryLocation);
        }

        /**
         * 取消一些木制品方块。
         */
        public Builder banBlocks(HTWoodTypes... woodSuits) {
            integration.banBlocks(woodSuits);
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
            for (HTWoodTypes suit : HTWoodTypes.values()) {
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
            for (HTWoodTypes suit : HTWoodTypes.values()) {
                updateBlockStrength(suit, suit.destroyTime * multiplier1, suit.explosionResistance * multiplier2);
            }
            return this;
        }

        public Builder updateBlockStrength(HTWoodTypes woodSuits, float strength) {
            integration.updateBlock(woodSuits, p -> p.strength(strength));
            return this;
        }

        public Builder updateBlockStrength(HTWoodTypes woodSuits, float strength, float resistance) {
            integration.updateBlock(woodSuits, p -> p.strength(strength, resistance));
            return this;
        }

        public Builder updateBlock(HTWoodTypes woodSuits, Consumer<BlockBehaviour.Properties> consumer) {
            integration.updateBlock(woodSuits, consumer);
            return this;
        }

        public Builder updateWoodItems(Consumer<Item.Properties> consumer) {
            for (HTWoodTypes suit : HTWoodTypes.values()) {
                integration.updateItem(suit, consumer);
            }
            return this;
        }

        public Builder updateWoodFunction(HTWoodTypes woodSuits, Function<BlockBehaviour.Properties, Block> function) {
            integration.setBlockFunction(woodSuits, function);
            return this;
        }

        public Builder updateBoatItems(Consumer<Item.Properties> consumer) {
            integration.updateBoatItems(consumer);
            return this;
        }

        public Builder disableLogTag(){
            integration.logItemTag = null;
            integration.logBlockTag = null;
            return this;
        }

        public TreeSuit build() {
            return this.integration;
        }

    }

    public static class BoatSetting {

        private final Item.Properties itemProperties;
        private final BoatType boatType;
        private boolean enabled = true;

        BoatSetting(BoatType boatType) {
            this.itemProperties = new Item.Properties().stacksTo(16);
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
        public BoatType getBoatType() {
            return boatType;
        }
    }

    public enum HTWoodTypes {

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
        HANGING_SIGN(1F),
        WALL_HANGING_SIGN(1F, 1F, false),
        STAIRS(2F, 3F),
        BUTTON(0.5F),
        SLAB(2F, 3F),
        PRESSURE_PLATE(0.5F);

        private final float destroyTime;
        private final float explosionResistance;
        private final boolean hasItem;

        HTWoodTypes(float strength) {
            this(strength, strength);
        }

        HTWoodTypes(float strength, float explosionResistance) {
            this(strength, explosionResistance, true);
        }

        HTWoodTypes(float strength, float explosionResistance, boolean hasItem) {
            this.destroyTime = strength;
            this.explosionResistance = explosionResistance;
            this.hasItem = hasItem;
        }

        public boolean hasItem() {
            return this.hasItem;
        }
    }

    public enum HTBoatStyles {
        NORMAL,
        CHEST;
    }

}
