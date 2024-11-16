package hungteen.htlib.data.recipe;

import net.minecraft.data.recipes.RecipeBuilder;

/**
 * TODO 这个有用吗？
 * @author PangTeen
 * @program HTLib
 * @data 2023/4/13 13:42
 */
public abstract class HTRecipeBuilder implements RecipeBuilder {

//    private final Advancement.Builder advancement = Advancement.Builder.advancement();
//    private final Item result;
//    @javax.annotation.Nullable
//    private String group;
//
//    public HTRecipeBuilder(Item result) {
//        this.result = result;
//    }
//
//    @Override
//    public RecipeBuilder unlockedBy(String name, Criterion<?> instance) {
//        this.advancement.addCriterion(name, instance);
//        return this;
//    }
//
//    @Override
//    public RecipeBuilder group(@Nullable String group) {
//        this.group = group;
//        return this;
//    }
//
//    @Override
//    public Item getResult() {
//        return this.result;
//    }
//
//    public String getGroup() {
//        return this.group == null ? StringHelper.EMPTY_STRING : this.group;
//    }
//
//    public Advancement.Builder getAdvancement() {
//        return advancement;
//    }
//
//    public abstract FinishedRecipe createFinishedRecipe(ResourceLocation recipe);
//
//    @Override
//    public void save(RecipeOutput recipeOutput, ResourceLocation recipe) {
//        this.ensureValid(recipe);
//        this.advancement.parent(ROOT_RECIPE_ADVANCEMENT)
//                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipe))
//                .rewards(AdvancementRewards.Builder.recipe(recipe))
//                .requirements(RequirementsStrategy.OR);
//        recipeOutput.accept(createFinishedRecipe(recipe));
//    }
//
//    protected void ensureValid(ResourceLocation recipe) {
//        if (this.advancement.getCriteria().isEmpty()) {
//            throw new IllegalStateException("No way of obtaining recipe " + recipe);
//        }
//    }
}
