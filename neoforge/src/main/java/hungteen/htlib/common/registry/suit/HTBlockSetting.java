package hungteen.htlib.common.registry.suit;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 一个方块需要注册的所有设置，包括方块属性、物品属性、方块生成函数、物品生成函数等等。<br>
 * @author PangTeen
 * @program HTLib
 * @data 2023/7/4 15:25
 */
public class HTBlockSetting {

    private final Function<ResourceLocation, ResourceLocation> nameFunc;
    private final BlockBehaviour.Properties blockProperties;
    private final Item.Properties itemProperties;
    private Function<BlockBehaviour.Properties, Block> blockFunc;
    private BiFunction<Block, Item.Properties, BlockItem> itemFunc;
    private boolean hasItem = true;

    HTBlockSetting(Function<ResourceLocation, ResourceLocation> nameFunc, BlockBehaviour.Properties blockProperties, Function<BlockBehaviour.Properties, Block> blockFunc) {
        this(nameFunc, blockProperties, blockFunc, new Item.Properties(), ItemNameBlockItem::new);
    }

    /**
     * @param nameFunc 根据基本的名字生成变体注册名（石头->石头台阶）。
     * @param blockProperties 该方块的属性。
     * @param blockFunc 一般是方块的构造函数。
     * @param itemProperties 该物品的属性。
     * @param itemFunc 一般是物品的构造函数。
     */
    HTBlockSetting(Function<ResourceLocation, ResourceLocation> nameFunc, BlockBehaviour.Properties blockProperties, Function<BlockBehaviour.Properties, Block> blockFunc, Item.Properties itemProperties, BiFunction<Block, Item.Properties, BlockItem> itemFunc) {
        this.nameFunc = nameFunc;
        this.blockProperties = blockProperties;
        this.itemProperties = itemProperties;
        this.blockFunc = blockFunc;
        this.itemFunc = itemFunc;
    }



    /**
     * @param consumer 修改物品的属性。
     */
    public void itemProperties(Consumer<Item.Properties> consumer) {
        consumer.accept(itemProperties);
    }

    public Item.Properties getItemProperties() {
        return itemProperties;
    }

    /**
     * @param consumer 修改方块的属性。
     */
    public void blockProperties(Consumer<BlockBehaviour.Properties> consumer) {
        consumer.accept(blockProperties);
    }

    public BlockBehaviour.Properties getBlockProperties() {
        return blockProperties;
    }

    public ResourceLocation getName(ResourceLocation location) {
        return nameFunc.apply(location);
    }

    public void setBlockFunc(@NotNull Function<BlockBehaviour.Properties, Block> blockFunc) {
        this.blockFunc = blockFunc;
    }

    public Supplier<Block> getSupplier() {
        return () -> this.blockFunc.apply(this.getBlockProperties());
    }

    public Supplier<Item> getItemSupplier(Block block) {
        return () -> this.itemFunc.apply(block, this.getItemProperties());
    }

    public void setItemFunc(BiFunction<Block, Item.Properties, BlockItem> itemFunc) {
        this.itemFunc = itemFunc;
    }

    public boolean hasItem() {
        return hasItem;
    }

    /**
     * @param hasItem 设置该方块是否是否有对应物品。
     */
    public void setHasItem(boolean hasItem) {
        this.hasItem = hasItem;
    }
}
