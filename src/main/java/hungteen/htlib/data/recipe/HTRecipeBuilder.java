package hungteen.htlib.data.recipe;

import hungteen.htlib.util.helper.StringHelper;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/4/13 13:42
 */
public abstract class HTRecipeBuilder implements RecipeBuilder {

    private final Advancement.Builder advancement = Advancement.Builder.advancement();
    private final Item result;
    @javax.annotation.Nullable
    private String group;

    public HTRecipeBuilder(Item result) {
        this.result = result;
    }

    @Override
    public RecipeBuilder unlockedBy(String name, CriterionTriggerInstance instance) {
        this.advancement.addCriterion(name, instance);
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String group) {
        this.group = group;
        return this;
    }

    @Override
    public Item getResult() {
        return this.result;
    }

    public String getGroup() {
        return this.group == null ? StringHelper.EMPTY_STRING : this.group;
    }

    public abstract FinishedRecipe createFinishedRecipe(ResourceLocation recipe);

    @Override
    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation recipe) {
        this.ensureValid(recipe);
        this.advancement.parent(ROOT_RECIPE_ADVANCEMENT)
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipe))
                .rewards(AdvancementRewards.Builder.recipe(recipe))
                .requirements(RequirementsStrategy.OR);
        consumer.accept(createFinishedRecipe(recipe));
    }

    protected void ensureValid(ResourceLocation recipe) {
        if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipe);
        }
    }
}
