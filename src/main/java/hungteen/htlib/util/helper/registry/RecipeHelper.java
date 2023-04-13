package hungteen.htlib.util.helper.registry;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/4/13 11:18
 */
public class RecipeHelper {

    private static final RegistryHelper<RecipeType<?>> RECIPE_HELPER = new RegistryHelper<>() {
        @Override
        public Either<IForgeRegistry<RecipeType<?>>, Registry<RecipeType<?>>> getRegistry() {
            return Either.left(ForgeRegistries.RECIPE_TYPES);
        }

    };

    private static final RegistryHelper<RecipeSerializer<?>> SERIALIZER_HELPER = new RegistryHelper<>() {
        @Override
        public Either<IForgeRegistry<RecipeSerializer<?>>, Registry<RecipeSerializer<?>>> getRegistry() {
            return Either.left(ForgeRegistries.RECIPE_SERIALIZERS);
        }

    };

    public static ItemStack readResultItem(JsonObject jsonObject){
        return ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));
    }

    public static void writeResultItem(JsonObject jsonObject, Item result, int count){
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("item", ItemHelper.get().getKey(result).toString());
        if (count > 1) {
            jsonobject.addProperty("count", count);
        }
    }

    public static NonNullList<Ingredient> readIngredients(JsonArray jsonArray) {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        for(int i = 0; i < jsonArray.size(); ++i) {
            ingredients.add(Ingredient.fromJson(jsonArray.get(i)));
        }
        return ingredients;
    }

    public static void writeIngredients(JsonObject jsonObject, NonNullList<Ingredient> ingredients){
        JsonArray jsonarray = new JsonArray();
        for (Ingredient ingredient : ingredients) {
            jsonarray.add(ingredient.toJson());
        }
        jsonObject.add("ingredients", jsonarray);
    }

    public static NonNullList<Ingredient> readIngredients(FriendlyByteBuf buf){
        final int len = buf.readVarInt();
        NonNullList<Ingredient> ingredients = NonNullList.withSize(len, Ingredient.EMPTY);
        for(int j = 0; j < ingredients.size(); ++j) {
            ingredients.set(j, Ingredient.fromNetwork(buf));
        }
        return ingredients;
    }

    public static void writeIngredients(FriendlyByteBuf buf, NonNullList<Ingredient> ingredients){
        buf.writeVarInt(ingredients.size());
        for(Ingredient ingredient : ingredients) {
            ingredient.toNetwork(buf);
        }
    }

    public static RegistryHelper<RecipeType<?>> get(){
        return RECIPE_HELPER;
    }

    public static RegistryHelper<RecipeSerializer<?>> serializer(){
        return SERIALIZER_HELPER;
    }

}
