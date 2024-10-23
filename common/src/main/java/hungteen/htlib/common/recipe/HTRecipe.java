package hungteen.htlib.common.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/4/13 14:10
 */
public abstract class HTRecipe<T extends RecipeInput> implements Recipe<T> {

    protected final ItemStack result;

    protected HTRecipe(ItemStack result) {
        this.result = result;
    }

    @Override
    public boolean matches(T container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(T container, HolderLookup.Provider provider) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return this.result;
    }

}
