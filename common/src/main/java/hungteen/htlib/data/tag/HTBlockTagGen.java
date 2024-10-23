package hungteen.htlib.data.tag;

import hungteen.htlib.common.impl.registry.suit.TreeSuits;
import hungteen.htlib.util.helper.registry.BlockHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-07 08:46
 **/
public abstract class HTBlockTagGen extends HTHolderTagsProvider<Block> {
    public HTBlockTagGen(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, BlockHelper.get());
    }

    protected void woodIntegration(TreeSuits.TreeSuit woodIntegration) {
        woodIntegration.getWoodBlocks().forEach(pair -> {
            Block block = pair.getValue();
            switch (pair.getKey()) {
                case LOG, STRIPPED_LOG, WOOD, STRIPPED_WOOD -> this.tag(BlockTags.LOGS_THAT_BURN).add(block);
                case PLANKS -> this.tag(BlockTags.PLANKS).add(block);
                case DOOR -> this.tag(BlockTags.WOODEN_DOORS).add(block);
                case TRAP_DOOR -> this.tag(BlockTags.WOODEN_TRAPDOORS).add(block);
                case FENCE -> this.tag(BlockTags.FENCES).add(block);
                case FENCE_GATE -> this.tag(BlockTags.FENCE_GATES).add(block);
                case STANDING_SIGN -> this.tag(BlockTags.STANDING_SIGNS).add(block);
                case WALL_SIGN -> this.tag(BlockTags.WALL_SIGNS).add(block);
                case HANGING_SIGN -> this.tag(BlockTags.CEILING_HANGING_SIGNS).add(block);
                case WALL_HANGING_SIGN -> this.tag(BlockTags.WALL_HANGING_SIGNS).add(block);
                case STAIRS -> this.tag(BlockTags.BUTTONS).add(block);
                case BUTTON -> this.tag(BlockTags.WOODEN_STAIRS).add(block);
                case SLAB -> this.tag(BlockTags.WOODEN_SLABS).add(block);
                case PRESSURE_PLATE -> this.tag(BlockTags.WOODEN_PRESSURE_PLATES).add(block);
            }
        });
        woodIntegration.getLogBlockTag().ifPresent(blockTag -> {
            this.tag(BlockTags.LOGS_THAT_BURN).addTag(blockTag);
            woodIntegration.getBlockOpt(TreeSuits.HTWoodTypes.LOG).ifPresent(this.tag(blockTag)::add);
            woodIntegration.getBlockOpt(TreeSuits.HTWoodTypes.WOOD).ifPresent(this.tag(blockTag)::add);
            woodIntegration.getBlockOpt(TreeSuits.HTWoodTypes.STRIPPED_WOOD).ifPresent(this.tag(blockTag)::add);
            woodIntegration.getBlockOpt(TreeSuits.HTWoodTypes.STRIPPED_LOG).ifPresent(this.tag(blockTag)::add);
        });
    }
}
