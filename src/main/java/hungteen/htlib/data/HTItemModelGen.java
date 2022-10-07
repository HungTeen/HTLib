package hungteen.htlib.data;

import hungteen.htlib.HTLib;
import net.minecraft.Util;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.HashSet;
import java.util.Set;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-06 18:37
 **/
public abstract class HTItemModelGen extends ItemModelProvider {

    protected final Set<Item> addedItems = new HashSet<>();

    public HTItemModelGen(DataGenerator generator, String modid, ExistingFileHelper helper) {
        super(generator, modid, helper);
    }

    /**
     * for items with the same texture.
     */
    protected void genSameModelsWithAdd(Item... items) {
        final Item first = items[0];
        for (Item i : items) {
            genNormal(i.getRegistryName().getPath(), HTLib.res(this.modid, "item/" + first.getRegistryName().getPath()));
            this.addedItems.add(i);
        }
    }

    protected void genNormalModel(Item i) {
        genNormal(i.getRegistryName().getPath(), HTLib.res(this.modid, "item/" + i.getRegistryName().getPath()));
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

    protected void genBlockModel(Block b) {
        genBlockModel(b, b.getRegistryName().getPath());
    }

    protected void genBlockModel(Block b, String path) {
        withExistingParent(b.getRegistryName().getPath(), HTLib.res(this.modid, "block/" + path));
        this.addedItems.add(b.asItem());
    }

    protected void genItemModelWithBlock(Item i) {
        genNormal(i.getRegistryName().getPath(), HTLib.res(this.modid, "block/" + i.getRegistryName().getPath()));
        this.addedItems.add(i);
    }

    @Override
    public String getName() {
        return this.modid + " item models";
    }

}