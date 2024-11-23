package hungteen.htlib.data.recipe;

import hungteen.htlib.common.registry.suit.HTWoodSet;
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
    public void buildRecipes(RecipeOutput recipeOutput) {

    }

    /**
     * Gen wood-related at once.
     */
    protected void woodIntegration(RecipeOutput recipeOutput, HTWoodSet woodIntegration) {
        final Optional<Block> planksOpt = woodIntegration.getBlockOpt(HTWoodSet.HTWoodVariant.PLANKS);
        planksOpt.ifPresent(planks -> {
            woodIntegration.getLogItemTag().ifPresent(logTag -> {
                planksFromLog(recipeOutput, planks, logTag, 4);
            });
        });
        woodIntegration.getBoatItem(HTWoodSet.HTBoatVariant.NORMAL).ifPresent(boatItem -> {
            planksOpt.ifPresent(planks -> {
                woodenBoat(recipeOutput, boatItem, planks);
            });
            woodIntegration.getBoatItem(HTWoodSet.HTBoatVariant.CHEST).ifPresent(chestBoat -> {
                chestBoat(recipeOutput, chestBoat, boatItem);
            });
        });
        woodIntegration.getBlockOpt(HTWoodSet.HTWoodVariant.WOOD).ifPresent(wood -> {
            woodIntegration.getBlockOpt(HTWoodSet.HTWoodVariant.LOG).ifPresent(log -> {
                woodFromLogs(recipeOutput, wood, log);
            });
        });
        woodIntegration.getBlockOpt(HTWoodSet.HTWoodVariant.STRIPPED_WOOD).ifPresent(wood -> {
            woodIntegration.getBlockOpt(HTWoodSet.HTWoodVariant.STRIPPED_LOG).ifPresent(log -> {
                woodFromLogs(recipeOutput, wood, log);
            });
        });
    }

}
