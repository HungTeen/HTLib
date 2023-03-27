package hungteen.htlib.data.tag;

import hungteen.htlib.util.helper.registry.ItemHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
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

    protected void copy(TagKey<Block> blockTagKey, TagKey<Item> itemTagKey) {
        TagBuilder itemTagbuilder = this.getOrCreateRawBuilder(itemTagKey);
        TagBuilder blockTagbuilder = (TagBuilder)this.blockTags.apply(blockTagKey);
        List<TagEntry> list = blockTagbuilder.build();
        Objects.requireNonNull(itemTagbuilder);
        Objects.requireNonNull(itemTagbuilder);
        list.forEach(itemTagbuilder::add);
    }
}
