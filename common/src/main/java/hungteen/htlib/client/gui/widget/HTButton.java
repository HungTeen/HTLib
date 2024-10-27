package hungteen.htlib.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-09 10:33
 **/
public abstract class HTButton extends AbstractButton {

    protected final ResourceLocation widgets;
    protected final OnPress onPress;

    public HTButton(ResourceLocation location, int x, int y, int width, int height, OnPress onPress) {
        this(location, x, y, width, height, "", onPress);
    }

    public HTButton(ResourceLocation location, int x, int y, int width, int height, String text, OnPress onPress) {
        super(x, y, width, height, Component.literal(text));
        this.onPress = onPress;
        this.widgets = location;
    }

    @Override
    public boolean isHovered(){
        return this.isHovered;
    }

    @Deprecated
    @Override
    public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partial) {
        if (this.visible) {
            this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, this.widgets);
            final Pair<Integer,Integer> xy = this.getButtonUV();
            final Pair<Integer,Integer> xyOffset = this.getButtonUVOffset();
            if(this.isHovered){
                gui.blit(this.widgets, this.getX(), this.getY(), xy.getFirst() + xyOffset.getFirst(), xy.getSecond() + xyOffset.getSecond(), width, height);
            } else{
                gui.blit(this.widgets, this.getX(), this.getY(), xy.getFirst(), xy.getSecond(), width, height);
            }
        }
    }

    @Override
    public void onPress() {
        this.onPress.onPress(this);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {

    }

    protected abstract Pair<Integer,Integer> getButtonUV();

    protected abstract Pair<Integer,Integer> getButtonUVOffset();

    public interface OnPress {
        void onPress(HTButton button);
    }
}
