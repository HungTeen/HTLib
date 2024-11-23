package hungteen.htlib.common.registry.suit;

import hungteen.htlib.common.block.entityblock.HTHangingSignBlock;
import hungteen.htlib.common.block.entityblock.HTStandingSignBlock;
import hungteen.htlib.common.block.entityblock.HTWallHangingSignBlock;
import hungteen.htlib.common.block.entityblock.HTWallSignBlock;
import hungteen.htlib.common.block.wood.*;
import hungteen.htlib.common.impl.HTLibBoatTypes;
import hungteen.htlib.common.item.HTBoatItem;
import hungteen.htlib.data.tag.HTItemTagGen;
import hungteen.htlib.util.HTBoatType;
import hungteen.htlib.util.NeoHelper;
import hungteen.htlib.util.helper.StringHelper;
import hungteen.htlib.util.helper.impl.BlockHelper;
import hungteen.htlib.util.helper.impl.ItemHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 木头系列管理一整套同类方块变体 {@link HTWoodVariant} & {@link HTBoatVariant}，例如橡木、白桦木、金合欢木等等。<br>
 * 1. 注册木头系列方块的所有变体和对应物品，注册 {@link HTBoatType} & {@link WoodType}。<br>
 * 2. 注册相关方块的 {@link net.neoforged.neoforge.common.ItemAbility} 和发射器行为。<br>
 * 3. 注册相关方块到创造模式物品栏 {@link #fillTabs(BuildCreativeModeTabContentsEvent)}。<br>
 * 4. 提供便捷的数据生成方法，例如 {@link hungteen.htlib.data.HTBlockStateGen}, {@link hungteen.htlib.data.loot.HTBlockLootGen}, {@link HTItemTagGen}。<br>
 * TODO BlockSetTypeBuilder & WoodTypeBuilder
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/11/17 22:54
 **/
public class HTWoodSet extends HTBlockSet<HTWoodSet.HTWoodVariant> {

    private final EnumMap<HTWoodVariant, HTBlockSetting> settingMap = new EnumMap<>(HTWoodVariant.class);
    private final EnumMap<HTWoodVariant, Block> blockMap = new EnumMap<>(HTWoodVariant.class);
    private final EnumMap<HTBoatVariant, Item> boatMap = new EnumMap<>(HTBoatVariant.class);
    private final HTBoatSetting boatSetting;
    private final BlockSetType blockSetType;
    private final WoodType woodType;
    private final TagKey<Item> logItemTag;
    private final TagKey<Block> logBlockTag;

    /**
     * Use {@link HTWoodSet.WoodSuitBuilder} instead.
     * @param registryName such as "pvz:nut", former is mod id, latter is wood name.
     */
    protected HTWoodSet(ResourceLocation registryName) {
        super(registryName);

        // BoatSetting Relate.
        this.boatSetting = this.defaultBoatSetting();

        // BlockSetType & WoodType.
        this.blockSetType = new BlockSetType(registryName.toString());
        this.woodType = new WoodType(registryName.toString(), this.blockSetType);

        // BlockSetting Relate.
        this.defaultBlockSettingMap();

        // Tag Relate.
        final ResourceLocation tagLocation = StringHelper.suffix(this.getLocation(), "logs");
        this.logItemTag = ItemHelper.get().tag(tagLocation);
        this.logBlockTag = BlockHelper.get().tag(tagLocation);
    }

    @Override
    public void fillTabs(BuildCreativeModeTabContentsEvent event) {
        super.fillTabs(event);
        if(event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)){
            getBoatMap().forEach((suit, item) -> {
                event.accept(new ItemStack(item), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            });
        }
    }

    @Override
    public void register(RegisterEvent event) {
        super.register(event);
        if(NeoHelper.canRegister(event, ItemHelper.get())){
            // 注册木头类型。
            BlockHelper.registerWoodType(getWoodType());
            // 注册船类型。
            if (getBoatSetting().isEnabled()) {
                HTLibBoatTypes.registerBoatType(getBoatSetting().getBoatType());
                NeoHelper.register(event, ItemHelper.get(), StringHelper.suffix(getLocation(), "boat"), () -> {
                    final Item item = new HTBoatItem(getBoatSetting().getItemProperties(), this.getBoatSetting().getBoatType(), false);
                    putBoatItem(HTBoatVariant.NORMAL, item);
                    return item;
                });
                NeoHelper.register(event, ItemHelper.get(), StringHelper.suffix(getLocation(), "chest_boat"), () -> {
                    final Item item = new HTBoatItem(getBoatSetting().getItemProperties(), this.getBoatSetting().getBoatType(), true);
                    putBoatItem(HTBoatVariant.CHEST, item);
                    return item;
                });
            }
        }
    }

    public Map<HTBoatVariant, Item> getBoatMap() {
        return boatMap;
    }

    public void putBoatItem(HTBoatVariant boatSuit, Item item) {
        this.boatMap.put(boatSuit, item);
    }

    @Override
    protected Map<HTWoodVariant, HTBlockSetting> getSettingMap() {
        return this.settingMap;
    }

    @Override
    protected Map<HTWoodVariant, Block> getBlockMap() {
        return this.blockMap;
    }

    private void updateBoatItems(Consumer<Item.Properties> consumer) {
        this.boatSetting.itemProperties(consumer);
    }

    private void banBoat() {
        this.boatSetting.disable();
    }

    public Optional<Item> getBoatItem(HTBoatVariant boatSuit) {
        return Optional.ofNullable(this.boatMap.getOrDefault(boatSuit, null));
    }

    @NotNull
    public Item getBoat(HTBoatVariant boatSuit) {
        return getBoatItem(boatSuit).orElseThrow();
    }

    public Optional<TagKey<Item>> getLogItemTag() {
        return Optional.ofNullable(this.logItemTag);
    }

    public Optional<TagKey<Block>> getLogBlockTag() {
        return Optional.ofNullable(this.logBlockTag);
    }

    /* Getter */

    public Block getLog() {
        return getBlock(HTWoodVariant.LOG);
    }

    public Block getStrippedLog() {
        return getBlock(HTWoodVariant.STRIPPED_LOG);
    }

    public Block getWood() {
        return getBlock(HTWoodVariant.WOOD);
    }

    public Block getStrippedWood() {
        return getBlock(HTWoodVariant.STRIPPED_WOOD);
    }

    public Block getPlanks() {
        return getBlock(HTWoodVariant.PLANKS);
    }

    public Block getDoor() {
        return getBlock(HTWoodVariant.DOOR);
    }

    public Block getTrapDoor() {
        return getBlock(HTWoodVariant.TRAP_DOOR);
    }

    public Block getFence() {
        return getBlock(HTWoodVariant.FENCE);
    }

    public Block getFenceGate() {
        return getBlock(HTWoodVariant.FENCE_GATE);
    }

    public Block getStandingSign() {
        return getBlock(HTWoodVariant.STANDING_SIGN);
    }

    public Block getWallSign() {
        return getBlock(HTWoodVariant.WALL_SIGN);
    }

    public Block getHangingSign() {
        return getBlock(HTWoodVariant.HANGING_SIGN);
    }

    public Block getWallHangingSign() {
        return getBlock(HTWoodVariant.WALL_HANGING_SIGN);
    }

    public Block getStairs() {
        return getBlock(HTWoodVariant.STAIRS);
    }

    public Block getButton() {
        return getBlock(HTWoodVariant.BUTTON);
    }

    public Block getSlab() {
        return getBlock(HTWoodVariant.SLAB);
    }

    public Block getPressurePlate() {
        return getBlock(HTWoodVariant.PRESSURE_PLATE);
    }

    public Item getBoatItem() {
        return getBoat(HTBoatVariant.NORMAL);
    }

    public Item getChestBoatItem() {
        return getBoat(HTBoatVariant.CHEST);
    }

    public HTBoatSetting getBoatSetting() {
        return boatSetting;
    }

    public WoodType getWoodType() {
        return woodType;
    }

    public BlockSetType getBlockSetType() {
        return blockSetType;
    }

    protected void defaultBlockSettingMap(){
        settingMap.clear();
        settingMap.put(HTWoodVariant.LOG, new HTBlockSetting(
                r -> StringHelper.suffix(r, "log"),
                Block.Properties.ofFullCopy(Blocks.OAK_LOG),
                RotatedPillarBlock::new
        ));
        settingMap.put(HTWoodVariant.STRIPPED_LOG, new HTBlockSetting(
                r -> StringHelper.expand(r, "stripped", "log"),
                Block.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG),
                RotatedPillarBlock::new
        ));
        settingMap.put(HTWoodVariant.WOOD, new HTBlockSetting(
                r -> StringHelper.suffix(r, "wood"),
                Block.Properties.ofFullCopy(Blocks.OAK_WOOD),
                RotatedPillarBlock::new
        ));
        settingMap.put(HTWoodVariant.STRIPPED_WOOD, new HTBlockSetting(
                r -> StringHelper.expand(r, "stripped", "wood"),
                Block.Properties.ofFullCopy(Blocks.STRIPPED_OAK_WOOD),
                RotatedPillarBlock::new
        ));
        settingMap.put(HTWoodVariant.PLANKS, new HTBlockSetting(
                r -> StringHelper.suffix(r, "planks"),
                Block.Properties.ofFullCopy(Blocks.OAK_PLANKS),
                Block::new
        ));
        settingMap.put(HTWoodVariant.DOOR, new HTBlockSetting(
                r -> StringHelper.suffix(r, "door"),
                Block.Properties.ofFullCopy(Blocks.OAK_DOOR),
                properties -> new HTDoorBlock(this.blockSetType, properties)
        ));
        settingMap.put(HTWoodVariant.TRAP_DOOR, new HTBlockSetting(
                r -> StringHelper.suffix(r, "trapdoor"),
                Block.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR),
                properties -> new HTTrapDoorBlock(this.blockSetType, properties)
        ));
        settingMap.put(HTWoodVariant.FENCE, new HTBlockSetting(
                r -> StringHelper.suffix(r, "fence"),
                Block.Properties.ofFullCopy(Blocks.OAK_FENCE),
                FenceBlock::new
        ));
        settingMap.put(HTWoodVariant.FENCE_GATE, new HTBlockSetting(
                r -> StringHelper.suffix(r, "fence_gate"),
                Block.Properties.ofFullCopy(Blocks.OAK_FENCE_GATE),
                properties -> new FenceGateBlock(this.woodType, properties)
        ));
        settingMap.put(HTWoodVariant.STANDING_SIGN, new HTBlockSetting(
                r -> StringHelper.suffix(r, "sign"),
                Block.Properties.ofFullCopy(Blocks.OAK_SIGN),
                p -> new HTStandingSignBlock(p, this.woodType),
                new Item.Properties().stacksTo(16),
                (block, po) -> new SignItem(po, block, this.blockMap.get(HTWoodVariant.WALL_SIGN))
        ));
        settingMap.put(HTWoodVariant.WALL_SIGN, new HTBlockSetting(
                r -> StringHelper.suffix(r, "wall_sign"),
                Block.Properties.ofFullCopy(Blocks.OAK_SIGN),
                p -> new HTWallSignBlock(p, this.woodType)
        ));
        settingMap.put(HTWoodVariant.HANGING_SIGN, new HTBlockSetting(
                r -> StringHelper.suffix(r, "hanging_sign"),
                Block.Properties.ofFullCopy(Blocks.OAK_HANGING_SIGN),
                p -> new HTHangingSignBlock(p, this.woodType),
                new Item.Properties().stacksTo(16),
                (block, po) -> new HangingSignItem(block, this.blockMap.get(HTWoodVariant.WALL_HANGING_SIGN), po)
        ));
        settingMap.put(HTWoodVariant.WALL_HANGING_SIGN, new HTBlockSetting(
                r -> StringHelper.suffix(r, "wall_hanging_sign"),
                Block.Properties.ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN),
                p -> new HTWallHangingSignBlock(p, this.woodType)
        ));
        settingMap.put(HTWoodVariant.STAIRS, new HTBlockSetting(
                r -> StringHelper.suffix(r, "stairs"),
                Block.Properties.ofFullCopy(Blocks.OAK_STAIRS),
                p -> new HTStairBlock(getBlock(HTWoodVariant.PLANKS).defaultBlockState(), p)
        ));
        settingMap.put(HTWoodVariant.BUTTON, new HTBlockSetting(
                r -> StringHelper.suffix(r, "button"),
                Block.Properties.ofFullCopy(Blocks.OAK_BUTTON),
                properties -> new HTButtonBlock(this.blockSetType, 30, properties)
        ));
        settingMap.put(HTWoodVariant.SLAB, new HTBlockSetting(
                r -> StringHelper.suffix(r, "slab"),
                Block.Properties.ofFullCopy(Blocks.OAK_SLAB),
                SlabBlock::new
        ));
        settingMap.put(HTWoodVariant.PRESSURE_PLATE, new HTBlockSetting(
                r -> StringHelper.suffix(r, "pressure_plate"),
                Block.Properties.ofFullCopy(Blocks.OAK_PRESSURE_PLATE),
                properties -> new HTPressurePlateBlock(this.blockSetType, properties)
        ));
    }

    protected HTBoatSetting defaultBoatSetting(){
        return new HTBoatSetting(new HTBoatType() {
            @Override
            public Block getPlanks() {
                return HTWoodSet.this.blockMap.getOrDefault(HTWoodVariant.PLANKS, Blocks.AIR);
            }

            @Override
            public Item getBoatItem() {
                return HTWoodSet.this.boatMap.getOrDefault(HTBoatVariant.NORMAL, ItemStack.EMPTY.getItem());
            }

            @Override
            public Item getChestBoatItem() {
                return HTWoodSet.this.boatMap.getOrDefault(HTBoatVariant.CHEST, ItemStack.EMPTY.getItem());
            }

            @Override
            public String name() {
                return HTWoodSet.this.name();
            }

            @Override
            public String getModID() {
                return HTWoodSet.this.getModID();
            }
        });
    }

    public enum HTWoodVariant {

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
        PRESSURE_PLATE(0.5F),
        ;

        private final float destroyTime;
        private final float explosionResistance;
        private final boolean hasItem;

        HTWoodVariant(float strength) {
            this(strength, strength);
        }

        HTWoodVariant(float strength, float explosionResistance) {
            this(strength, explosionResistance, true);
        }

        HTWoodVariant(float strength, float explosionResistance, boolean hasItem) {
            this.destroyTime = strength;
            this.explosionResistance = explosionResistance;
            this.hasItem = hasItem;
        }

        public boolean hasItem() {
            return this.hasItem;
        }
    }

    public enum HTBoatVariant {

        NORMAL,
        CHEST,
        ;
    }

    public static class WoodSuitBuilder {

        private final HTWoodSet suit;

        public WoodSuitBuilder(ResourceLocation registryLocation) {
            this.suit = new HTWoodSet(registryLocation);
        }

        /**
         * 取消一些木制品方块。
         * @param variants 木制品方块变体。
         */
        public WoodSuitBuilder banBlocks(HTWoodSet.HTWoodVariant... variants) {
            suit.banBlocks(variants);
            return this;
        }

        /**
         * 取消一些木制品方块对应的物品。
         * @param variants 木制品方块变体。
         */
        public WoodSuitBuilder banItems(HTWoodSet.HTWoodVariant... variants) {
            suit.banItems(variants);
            return this;
        }

        /**
         * 取消船的注册。
         */
        public WoodSuitBuilder banBoat() {
            suit.banBoat();
            return this;
        }

        /**
         * 更新所有方块的属性。
         */
        public WoodSuitBuilder updateBlocks(Consumer<BlockBehaviour.Properties> consumer) {
            for (HTWoodVariant variant : HTWoodVariant.values()) {
                updateBlock(variant, consumer);
            }
            return this;
        }

        public WoodSuitBuilder updateBlocksStrength(float multiplier) {
            return updateBlocksStrength(multiplier, multiplier);
        }

        /**
         * @param multiplier1 base on vanilla wood destroy time.
         * @param multiplier2 base on vanilla wood explosion resistance.
         */
        public WoodSuitBuilder updateBlocksStrength(float multiplier1, float multiplier2) {
            for (HTWoodVariant variant : HTWoodVariant.values()) {
                updateBlockStrength(variant, variant.destroyTime * multiplier1, variant.explosionResistance * multiplier2);
            }
            return this;
        }

        public WoodSuitBuilder updateBlockStrength(HTWoodVariant variant, float strength) {
            return updateBlock(variant, p -> p.strength(strength));
        }

        public WoodSuitBuilder updateBlockStrength(HTWoodVariant variant, float strength, float resistance) {
            return updateBlock(variant, p -> p.strength(strength, resistance));
        }

        public WoodSuitBuilder updateBlock(HTWoodVariant variant, Consumer<BlockBehaviour.Properties> consumer) {
            suit.updateBlock(variant, consumer);
            return this;
        }

        public WoodSuitBuilder updateWoodItems(Consumer<Item.Properties> consumer) {
            for (HTWoodVariant variant : HTWoodVariant.values()) {
                this.suit.updateItem(variant, consumer);
            }
            return this;
        }

        public WoodSuitBuilder updateWoodFunction(HTWoodVariant variant, Function<BlockBehaviour.Properties, Block> function) {
            suit.setBlockFunction(variant, function);
            return this;
        }

        public WoodSuitBuilder updateBoatItems(Consumer<Item.Properties> consumer) {
            suit.updateBoatItems(consumer);
            return this;
        }

        public HTWoodSet build() {
            return this.suit;
        }

    }
}
