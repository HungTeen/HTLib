package hungteen.htlib.client.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-09 10:32
 **/
public class RecipeRenderManager {

    private final List<RecipeIngredient> ingredients = Lists.newArrayList();
    private float time;

    public void render(Minecraft mc, GuiGraphics gui, int guiLeft, int guiTop, float partialTicks) {
        gui.pose().pushPose();
        if (! Screen.hasControlDown()) {
            this.time += partialTicks;
        }

        for (int i = 0; i < this.ingredients.size(); ++i) {
            RecipeIngredient ingredient = this.ingredients.get(i);
            int x = ingredient.getX() + guiLeft;
            int y = ingredient.getY() + guiTop;
            gui.fill(x, y, x + 16, y + 16, 822018048);
            ItemStack itemstack = ingredient.getItem();
            gui.renderItem(itemstack, x, y);
            RenderSystem.depthFunc(516);
            gui.fill(x, y, x + 16, y + 16, 822083583);
            RenderSystem.depthFunc(515);
            if (i == 0) {
                gui.renderItemDecorations(mc.font, itemstack, x, y);
            }
        }
        gui.pose().popPose();
    }

    public void renderGhostRecipeTooltip(Minecraft mc, GuiGraphics gui, Font font, int guiLeft, int guiTop, int mouseX, int mouseY) {
        ItemStack itemstack = null;
        for (int i = 0; i < this.size(); ++ i) {
            RecipeIngredient ingredient = this.get(i);
            int x = ingredient.getX() + guiLeft;
            int y = ingredient.getY() + guiTop;
            if (mouseX >= x && mouseY >= y && mouseX < x + 16 && mouseY < y + 16) {
                itemstack = ingredient.getItem();
            }
        }
        if (itemstack != null && mc.screen != null) {
            gui.renderComponentTooltip(font, Screen.getTooltipFromItem(mc, itemstack), mouseX, mouseY);
        }
    }

    public void clear() {
        this.ingredients.clear();
        this.time = 0.0F;
    }

    public void setRecipe(List<Pair<Ingredient, Slot>> list) {
        list.forEach((pair) -> {
            this.addIngredient(pair.getFirst(), pair.getSecond().x, pair.getSecond().y);
        });
    }

    public void addIngredient(Ingredient i, int x, int y) {
        this.ingredients.add(new RecipeIngredient(i, x, y));
    }

    public RecipeIngredient get(int pos) {
        return this.ingredients.get(pos);
    }

    public int size() {
        return this.ingredients.size();
    }

    @OnlyIn(Dist.CLIENT)
    public class RecipeIngredient {
        private final Ingredient ingredient;
        private final int x;
        private final int y;

        public RecipeIngredient(Ingredient p_i47604_2_, int x, int y) {
            this.ingredient = p_i47604_2_;
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public ItemStack getItem() {
            ItemStack[] aitemstack = this.ingredient.getItems();
            return aitemstack[Mth.floor(RecipeRenderManager.this.time / 30.0F) % aitemstack.length];
        }
    }

}
