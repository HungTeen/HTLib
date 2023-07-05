package hungteen.htlib.data.tag;

import hungteen.htlib.common.registry.suit.TreeSuits;
import hungteen.htlib.util.helper.registry.ItemHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-07 08:45
 **/
public abstract class HTItemTagGen extends HTHolderTagsProvider<Item> {
    private final Function<TagKey<Block>, TagBuilder> blockTags;

    public HTItemTagGen(PackOutput output, HTBlockTagGen blockTagsProvider, CompletableFuture<HolderLookup.Provider> provider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, ItemHelper.get(), modId, existingFileHelper);
        Objects.requireNonNull(blockTagsProvider);
        this.blockTags = blockTagsProvider::getOrCreateRawBuilder;
    }

    protected void woodIntegration(TreeSuits.TreeSuit woodIntegration) {
        this.copy(BlockTags.PLANKS, ItemTags.PLANKS);
        this.copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
        this.copy(BlockTags.BUTTONS, ItemTags.BUTTONS);
        this.copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
        this.copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
        this.copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
        this.copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
        this.copy(BlockTags.FENCE_GATES, ItemTags.FENCE_GATES);
        this.copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
        this.copy(BlockTags.DOORS, ItemTags.DOORS);
        this.copy(BlockTags.SLABS, ItemTags.SLABS);
        this.copy(BlockTags.STAIRS, ItemTags.STAIRS);
        this.copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
        this.copy(BlockTags.TRAPDOORS, ItemTags.TRAPDOORS);
        this.copy(BlockTags.FENCES, ItemTags.FENCES);
        this.copy(BlockTags.STANDING_SIGNS, ItemTags.SIGNS);
        this.copy(BlockTags.CEILING_HANGING_SIGNS, ItemTags.HANGING_SIGNS);
        woodIntegration.getLogBlockTag().ifPresent(blockTag -> {
            woodIntegration.getLogItemTag().ifPresent(itemTag -> {
                this.copy(blockTag, itemTag);
            });
        });
        woodIntegration.getBoatItem(TreeSuits.HTBoatStyles.NORMAL).ifPresent(item -> {
            this.tag(ItemTags.BOATS).add(item);
        });
        woodIntegration.getBoatItem(TreeSuits.HTBoatStyles.CHEST).ifPresent(item -> {
            this.tag(ItemTags.CHEST_BOATS).add(item);
        });
    }

    protected void copy(TagKey<Block> blockTagKey, TagKey<Item> itemTagKey) {
        final TagBuilder itemTagbuilder = this.getOrCreateRawBuilder(itemTagKey);
        final TagBuilder blockTagbuilder = this.blockTags.apply(blockTagKey);
        blockTagbuilder.build().forEach(itemTagbuilder::add);
    }
}
