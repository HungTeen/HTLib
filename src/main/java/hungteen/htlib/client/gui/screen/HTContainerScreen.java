package hungteen.htlib.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import hungteen.htlib.client.RenderHelper;
import hungteen.htlib.client.gui.widget.DisplayField;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-09 10:30
 **/
public class HTContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    private static final ResourceLocation WIDGETS = RenderHelper.WIDGETS;
    protected final List<DisplayField> tips = new ArrayList<>();

    public HTContainerScreen(T screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, WIDGETS);
        stack.pushPose();
        this.tips.forEach(tip -> {
            blit(stack, this.leftPos + tip.getX(), this.topPos + tip.getY(), tip.getTexX(), tip.getTexY(), tip.getWidth(), tip.getHeight());
        });
        stack.popPose();
    }

    @Override
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY) {
    }

    @Override
    protected void renderTooltip(PoseStack stack, int mouseX, int mouseY) {
        super.renderTooltip(stack, mouseX, mouseY);
        this.tips.forEach(tip -> {
            if (tip.isInField(mouseX - this.leftPos, mouseY - this.topPos)) {
                this.minecraft.screen.renderComponentTooltip(stack, tip.getTexts(), mouseX, mouseY);
            }
        });
    }
}