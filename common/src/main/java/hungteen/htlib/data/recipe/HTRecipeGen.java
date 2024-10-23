package hungteen.htlib.data.recipe;

import hungteen.htlib.common.impl.registry.suit.TreeSuits;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/4/21 15:53
 */
public class HTRecipeGen extends RecipeProvider {

    public HTRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {

    }

    /**
     * Gen wood-related at once.
     */
    protected void woodIntegration(RecipeOutput recipeOutput, TreeSuits.TreeSuit woodIntegration) {
        final Optional<Block> planksOpt = woodIntegration.getBlockOpt(TreeSuits.HTWoodTypes.PLANKS);
        planksOpt.ifPresent(planks -> {
            woodIntegration.getLogItemTag().ifPresent(logTag -> {
                planksFromLog(recipeOutput, planks, logTag, 4);
            });
        });
        woodIntegration.getBoatItem(TreeSuits.HTBoatStyles.NORMAL).ifPresent(boatItem -> {
            planksOpt.ifPresent(planks -> {
                woodenBoat(recipeOutput, boatItem, planks);
            });
            woodIntegration.getBoatItem(TreeSuits.HTBoatStyles.CHEST).ifPresent(chestBoat -> {
                chestBoat(recipeOutput, chestBoat, boatItem);
            });
        });
        woodIntegration.getBlockOpt(TreeSuits.HTWoodTypes.WOOD).ifPresent(wood -> {
            woodIntegration.getBlockOpt(TreeSuits.HTWoodTypes.LOG).ifPresent(log -> {
                woodFromLogs(recipeOutput, wood, log);
            });
        });
        woodIntegration.getBlockOpt(TreeSuits.HTWoodTypes.STRIPPED_WOOD).ifPresent(wood -> {
            woodIntegration.getBlockOpt(TreeSuits.HTWoodTypes.STRIPPED_LOG).ifPresent(log -> {
                woodFromLogs(recipeOutput, wood, log);
            });
        });
    }

}
