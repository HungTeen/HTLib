package hungteen.htlib.common.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/4/13 14:10
 */
public abstract class HTRecipe<T extends Container> implements Recipe<T> {

    protected final ResourceLocation id;
    protected final ItemStack result;

    protected HTRecipe(ResourceLocation id, ItemStack result) {
        this.id = id;
        this.result = result;
    }

    @Override
    public boolean matches(T container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(T container) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return this.result;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

}
