package hungteen.htlib.util.helper.impl;

import hungteen.htlib.api.util.helper.HTVanillaRegistryHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/4/13 11:18
 */
public interface RecipeHelper {

     HTVanillaRegistryHelper<RecipeType<?>> RECIPE_HELPER = () -> BuiltInRegistries.RECIPE_TYPE;

     HTVanillaRegistryHelper<RecipeSerializer<?>> SERIALIZER_HELPER = () -> BuiltInRegistries.RECIPE_SERIALIZER;

//    public static ItemStack readResultItem(JsonObject jsonObject){
//        return ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));
//    }
//
//    public static void writeResultItem(JsonObject jsonObject, Item result, int count) {
//        JsonObject obj = new JsonObject();
//        obj.addProperty("item", ItemHelper.get().getKey(result).toString());
//        if (count > 1) {
//            obj.addProperty("count", count);
//        }
//        jsonObject.add("result", obj);
//    }
//
//    public static void writeGroup(JsonObject jsonObject, String group){
//        if (!group.isEmpty()) {
//            jsonObject.addProperty("group", group);
//        }
//    }
//
//    public static String readGroup(JsonObject jsonObject){
//        return GsonHelper.getAsString(jsonObject, "group", "");
//    }
//
//    public static NonNullList<Ingredient> readIngredients(JsonObject jsonObject) {
//        return readIngredients(GsonHelper.getAsJsonArray(jsonObject, "ingredients"));
//    }
//
//    public static NonNullList<Ingredient> readIngredients(JsonArray jsonArray) {
//        NonNullList<Ingredient> ingredients = NonNullList.create();
//        for(int i = 0; i < jsonArray.size(); ++i) {
//            ingredients.add(Ingredient.fromJson(jsonArray.get(i)));
//        }
//        return ingredients;
//    }
//
//    public static void writeIngredients(JsonObject jsonObject, List<Ingredient> ingredients) {
//        JsonArray jsonarray = new JsonArray();
//
//        for (Ingredient ingredient : ingredients) {
//            jsonarray.add(ingredient.toJson());
//        }
//
//        jsonObject.add("ingredients", jsonarray);
//    }
//
//    public static NonNullList<Ingredient> readIngredients(FriendlyByteBuf buf){
//        final int len = buf.readVarInt();
//        NonNullList<Ingredient> ingredients = NonNullList.withSize(len, Ingredient.EMPTY);
//        for(int j = 0; j < ingredients.size(); ++j) {
//            ingredients.set(j, Ingredient.fromNetwork(buf));
//        }
//        return ingredients;
//    }
//
//    public static void writeIngredients(FriendlyByteBuf buf, NonNullList<Ingredient> ingredients){
//        buf.writeVarInt(ingredients.size());
//        for(Ingredient ingredient : ingredients) {
//            ingredient.toNetwork(buf);
//        }
//    }

    static HTVanillaRegistryHelper<RecipeType<?>> get(){
        return RECIPE_HELPER;
    }

    static HTVanillaRegistryHelper<RecipeSerializer<?>> serializer(){
        return SERIALIZER_HELPER;
    }

}
