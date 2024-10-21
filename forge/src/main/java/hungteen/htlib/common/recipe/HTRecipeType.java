package hungteen.htlib.common.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/4/13 11:15
 */
public record HTRecipeType<T extends Recipe<?>>(ResourceLocation location) implements RecipeType<T> {

    @Override
    public String toString() {
        return location().toString();
    }

}
