package hungteen.htlib.data.recipe;

import hungteen.htlib.common.impl.registry.suit.TreeSuits;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/4/21 15:53
 */
public class HTRecipeGen extends RecipeProvider {

    public HTRecipeGen(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {

    }

    /**
     * Gen wood-related at once.
     */
    protected void woodIntegration(Consumer<FinishedRecipe> consumer, TreeSuits.TreeSuit woodIntegration) {
        final Optional<Block> planksOpt = woodIntegration.getBlockOpt(TreeSuits.HTWoodTypes.PLANKS);
        planksOpt.ifPresent(planks -> {
            woodIntegration.getLogItemTag().ifPresent(logTag -> {
                planksFromLog(consumer, planks, logTag, 4);
            });
        });
        woodIntegration.getBoatItem(TreeSuits.HTBoatStyles.NORMAL).ifPresent(boatItem -> {
            planksOpt.ifPresent(planks -> {
                woodenBoat(consumer, boatItem, planks);
            });
            woodIntegration.getBoatItem(TreeSuits.HTBoatStyles.CHEST).ifPresent(chestBoat -> {
                chestBoat(consumer, chestBoat, boatItem);
            });
        });
        woodIntegration.getBlockOpt(TreeSuits.HTWoodTypes.WOOD).ifPresent(wood -> {
            woodIntegration.getBlockOpt(TreeSuits.HTWoodTypes.LOG).ifPresent(log -> {
                woodFromLogs(consumer, wood, log);
            });
        });
        woodIntegration.getBlockOpt(TreeSuits.HTWoodTypes.STRIPPED_WOOD).ifPresent(wood -> {
            woodIntegration.getBlockOpt(TreeSuits.HTWoodTypes.STRIPPED_LOG).ifPresent(log -> {
                woodFromLogs(consumer, wood, log);
            });
        });
    }

}
