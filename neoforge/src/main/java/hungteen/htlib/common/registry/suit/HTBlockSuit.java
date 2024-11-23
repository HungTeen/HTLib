package hungteen.htlib.common.registry.suit;

import hungteen.htlib.util.NeoHelper;
import hungteen.htlib.util.SuitRegistry;
import hungteen.htlib.util.helper.impl.BlockHelper;
import hungteen.htlib.util.helper.impl.ItemHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 注册方块的同时，注册物品。
 * @author PangTeen
 * @program HTLib
 * @create 2024/11/23 16:33
 **/
public class HTBlockSuit<T extends Block> implements SuitRegistry {

    private final ResourceLocation registryName;
    private Supplier<T> blockSupplier;
    private Function<T, BlockItem> itemFactory;
    private T block;

    public HTBlockSuit(ResourceLocation registryName, Supplier<T> blockSupplier){
        this(registryName, blockSupplier, block -> new BlockItem(block, new Item.Properties()));
    }

    public HTBlockSuit(ResourceLocation registryName, Supplier<T> blockSupplier, Function<T, BlockItem> itemFactory) {
        this.registryName = registryName;
        this.blockSupplier = blockSupplier;
        this.itemFactory = itemFactory;
    }

    @Override
    public void fillTabs(BuildCreativeModeTabContentsEvent event) {
        // No Use.
    }

    @Override
    public void register(RegisterEvent event) {
        if(blockSupplier != null){
            NeoHelper.register(event, BlockHelper.get(), registryName, () -> {
                return this.block = blockSupplier.get();
            });
        }
        if(this.block != null && itemFactory != null){
            NeoHelper.register(event, ItemHelper.get(), registryName, () -> {
                return itemFactory.apply(this.block);
            });
        }
    }

    public T get() {
        return Optional.ofNullable(block).orElseThrow(() -> new RuntimeException("Block not registered."));
    }

    public BlockItem getItem(){
        return ItemHelper.get().get(registryName).map(BlockItem.class::cast).orElseThrow(() -> new RuntimeException("Item not registered."));
    }

    @Override
    public void clear() {
        this.blockSupplier = null;
        this.itemFactory = null;
    }

    @Override
    public String name() {
        return registryName.getPath();
    }

    @Override
    public String getModID() {
        return registryName.getNamespace();
    }
}
