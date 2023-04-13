package hungteen.htlib.data.recipe;

import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/4/13 13:56
 */
public abstract class HTFinishedRecipe implements FinishedRecipe {

    private final ResourceLocation id;
    private final Advancement.Builder advancement;
    private final ResourceLocation advancementId;

    public HTFinishedRecipe(ResourceLocation id, Advancement.Builder advancement, ResourceLocation advancementId) {
        this.id = id;
        this.advancement = advancement;
        this.advancementId = advancementId;
    }

    @Override
    public void serializeRecipeData(JsonObject jsonObject) {
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Nullable
    @Override
    public JsonObject serializeAdvancement() {
        return this.advancement.serializeToJson();
    }

    @Nullable
    @Override
    public ResourceLocation getAdvancementId() {
        return this.advancementId;
    }
}
