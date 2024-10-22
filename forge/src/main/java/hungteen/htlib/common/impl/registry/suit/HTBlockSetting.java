package hungteen.htlib.common.impl.registry.suit;

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
 * @author PangTeen
 * @program HTLib
 * @data 2023/7/4 15:25
 */
public class HTBlockSetting {

    private final Function<ResourceLocation, ResourceLocation> getName;
    private final BlockBehaviour.Properties blockProperties;
    private final Item.Properties itemProperties;
    private Function<BlockBehaviour.Properties, Block> blockFunction;
    private BiFunction<Block, Item.Properties, BlockItem> itemFunction;
    private boolean hasItem = true;

    HTBlockSetting(Function<ResourceLocation, ResourceLocation> getName, BlockBehaviour.Properties blockProperties, Function<BlockBehaviour.Properties, Block> blockFunction) {
        this(getName, blockProperties, blockFunction, new Item.Properties(), ItemNameBlockItem::new);
    }

    HTBlockSetting(Function<ResourceLocation, ResourceLocation> getName, BlockBehaviour.Properties blockProperties, Function<BlockBehaviour.Properties, Block> blockFunction, Item.Properties itemProperties, BiFunction<Block, Item.Properties, BlockItem> itemFunction) {
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

    public boolean hasItem() {
        return hasItem;
    }

    public void setHasItem(boolean hasItem) {
        this.hasItem = hasItem;
    }
}
