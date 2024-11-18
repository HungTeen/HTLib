package hungteen.htlib.data;

import hungteen.htlib.api.HTLibAPI;
import hungteen.htlib.common.registry.suit.HTWoodSuit;
import hungteen.htlib.util.helper.StringHelper;
import hungteen.htlib.util.helper.impl.BlockHelper;
import hungteen.htlib.util.helper.impl.ItemHelper;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-07 12:25
 **/
public abstract class HTBlockModelGen extends BlockModelProvider {

    protected final Set<Block> addedBlocks = new HashSet<>();

    public HTBlockModelGen(PackOutput output, String modId, ExistingFileHelper existingFileHelper) {
        super(output, modId, existingFileHelper);
    }

    protected ResourceLocation key(Item item) {
        return ItemHelper.get().getKey(item);
    }

    protected String name(Item item) {
        return key(item).getPath();
    }

    protected ResourceLocation key(Block block) {
        return BlockHelper.get().getKey(block);
    }

    protected String name(Block block) {
        return key(block).getPath();
    }

    protected void fence(Block block){
        final ResourceLocation res = StringHelper.replace(BlockHelper.blockTexture(block), "fence", "planks");
        fenceInventory(name(block) + "_inventory", res);
    }

    protected void button(Block block){
        final ResourceLocation res = StringHelper.replace(BlockHelper.blockTexture(block), "button", "planks");
        buttonInventory(name(block) + "_inventory", res);
    }

    /**
     * Gen wood-related at once.
     */
    protected void woodSuitGen(HTWoodSuit suit) {
        suit.getBlockOpt(HTWoodSuit.HTWoodVariant.FENCE).ifPresent(b -> gen(b, this::fence));
        suit.getBlockOpt(HTWoodSuit.HTWoodVariant.BUTTON).ifPresent(b -> gen(b, this::button));
    }

    /**
     * Gen block model and add it to the gen set.
     */
    protected <T extends Block> void gen(T block, Consumer<T> consumer) {
        if (this.contains(block)) {
            HTLibAPI.logger().warn("Already gen {} before !", key(block));
            return;
        }
        consumer.accept(block);
        this.add(block);
    }

    protected void add(Block block){
        this.addedBlocks.add(block);
    }

    protected boolean contains(Block block) {
        return this.addedBlocks.contains(block);
    }

}
