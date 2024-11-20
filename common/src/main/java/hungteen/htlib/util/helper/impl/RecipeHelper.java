package hungteen.htlib.util.helper.impl;

import hungteen.htlib.api.util.helper.HTVanillaRegistryHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;
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

    static NonNullList<Ingredient> readIngredients(RegistryFriendlyByteBuf buf) {
        final int len = buf.readVarInt();
        NonNullList<Ingredient> ingredients = NonNullList.withSize(len, Ingredient.EMPTY);
        ingredients.replaceAll((ingredient) -> Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
        return ingredients;
    }

    static void writeIngredients(RegistryFriendlyByteBuf buf, NonNullList<Ingredient> ingredients) {
        buf.writeVarInt(ingredients.size());
        for (Ingredient ingredient : ingredients) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient);
        }
    }

    static HTVanillaRegistryHelper<RecipeType<?>> get() {
        return RECIPE_HELPER;
    }

    static HTVanillaRegistryHelper<RecipeSerializer<?>> serializer() {
        return SERIALIZER_HELPER;
    }

}
