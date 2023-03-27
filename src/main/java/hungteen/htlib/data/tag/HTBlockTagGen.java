package hungteen.htlib.data.tag;

import hungteen.htlib.common.WoodIntegrations;
import hungteen.htlib.util.helper.registry.BlockHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-07 08:46
 **/
public abstract class HTBlockTagGen extends HTHolderTagsProvider<Block> {
    public HTBlockTagGen(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, BlockHelper.get(), modId, existingFileHelper);
    }

    protected void woodIntegration(WoodIntegrations.WoodIntegration woodIntegration) {
        woodIntegration.getWoodBlocks().forEach((pair) -> {
            Block block = (Block)pair.getValue();
            switch ((WoodIntegrations.WoodSuits)pair.getKey()) {
                case LOG:
                case STRIPPED_LOG:
                case WOOD:
                case STRIPPED_WOOD:
                    this.tag(BlockTags.LOGS_THAT_BURN).add(block);
                    break;
                case PLANKS:
                    this.tag(BlockTags.PLANKS).add(block);
                    break;
                case DOOR:
                    this.tag(BlockTags.WOODEN_DOORS).add(block);
                    break;
                case TRAP_DOOR:
                    this.tag(BlockTags.WOODEN_TRAPDOORS).add(block);
                    break;
                case FENCE:
                    this.tag(BlockTags.FENCES).add(block);
                    break;
                case FENCE_GATE:
                    this.tag(BlockTags.FENCE_GATES).add(block);
                    break;
                case STANDING_SIGN:
                    this.tag(BlockTags.STANDING_SIGNS).add(block);
                    break;
                case WALL_SIGN:
                    this.tag(BlockTags.WALL_SIGNS).add(block);
                    break;
                case STAIRS:
                    this.tag(BlockTags.BUTTONS).add(block);
                    break;
                case BUTTON:
                    this.tag(BlockTags.WOODEN_STAIRS).add(block);
                    break;
                case SLAB:
                    this.tag(BlockTags.WOODEN_SLABS).add(block);
                    break;
                case PRESSURE_PLATE:
                    this.tag(BlockTags.WOODEN_PRESSURE_PLATES).add(block);
            }

        });
    }
}
