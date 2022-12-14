package hungteen.htlib.data;

import hungteen.htlib.util.helper.BlockHelper;
import hungteen.htlib.util.helper.StringHelper;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-07 12:25
 **/
public abstract class HTBlockModelGen extends BlockModelProvider {

    protected final Set<Block> addedBlocks = new HashSet<>();

    public HTBlockModelGen(DataGenerator generator, String modId, ExistingFileHelper existingFileHelper) {
        super(generator, modId, existingFileHelper);
    }

    protected ResourceLocation key(Item item) {
        return ForgeRegistries.ITEMS.getKey(item);
    }

    protected String name(Item item) {
        return key(item).getPath();
    }

    protected ResourceLocation key(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }

    protected String name(Block block) {
        return key(block).getPath();
    }

    protected void fence(Block block){
        final ResourceLocation res = StringHelper.replace(BlockHelper.blockTexture(block), "fence", "planks");
        fenceInventory(name(block) + "_inventory", res);
        this.addedBlocks.add(block);
    }

    protected void button(Block block){
        final ResourceLocation res = StringHelper.replace(BlockHelper.blockTexture(block), "button", "planks");
        buttonInventory(name(block) + "_inventory", res);
        this.addedBlocks.add(block);
    }

    @NotNull
    @Override
    public String getName() {
        return this.modid + " item models";
    }
}
