package hungteen.htlib.data;

import hungteen.htlib.HTLib;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Set;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-06 18:37
 **/
public abstract class HTItemModelGen extends ItemModelProvider {

    protected final Set<Item> addedItems = new HashSet<>();

    public HTItemModelGen(DataGenerator generator, String modId, ExistingFileHelper helper) {
        super(generator, modId, helper);
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

    /**
     * for items with the same texture.
     */
    protected void genSameModelsWithAdd(Item... items) {
        final Item first = items[0];
        for (Item item : items) {
            genNormal(name(item), HTLib.res(this.modid, "item/" + name(first)));
            this.addedItems.add(item);
        }
    }

    protected void genNormalModel(Item item) {
        genNormal(name(item), HTLib.res(this.modid, "item/" + name(item)));
    }

    protected ItemModelBuilder genNormal(String name, ResourceLocation... layers) {
        return gen(name, "item/generated", layers);
    }

    protected ItemModelBuilder genHeld(String name, ResourceLocation... layers) {
        return gen(name, "item/handheld", layers);
    }

    protected ItemModelBuilder gen(String name, String parent, ResourceLocation... layers) {
        ItemModelBuilder builder = withExistingParent(name, parent);
        for (int i = 0; i < layers.length; i++) {
            builder = builder.texture("layer" + i, layers[i]);
        }
        return builder;
    }

    protected void genBlockModel(Block block) {
        genBlockModel(block, name(block));
    }

    protected void genBlockModel(Block block, String path) {
        withExistingParent(name(block), HTLib.res(this.modid, "block/" + path));
        this.addedItems.add(block.asItem());
    }

    protected void genItemModelWithBlock(Item item) {
        genNormal(name(item), HTLib.res(this.modid, "block/" + name(item)));
        this.addedItems.add(item);
    }

    @Override
    public String getName() {
        return this.modid + " item models";
    }

}