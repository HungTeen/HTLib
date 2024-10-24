package hungteen.htlib.client.gui.screen;

import hungteen.htlib.client.util.RenderHelper;
import hungteen.htlib.client.gui.widget.DisplayField;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-09 10:30
 **/
public class HTContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    private static final ResourceLocation WIDGETS = RenderHelper.WIDGETS;
    protected final List<DisplayField> helpTips = new ArrayList<>();

    public HTContainerScreen(T screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        super.render(gui, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTicks, int mouseX, int mouseY) {
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
    }

    @Override
    protected void renderTooltip(GuiGraphics gui, int mouseX, int mouseY) {
        super.renderTooltip(gui, mouseX, mouseY);
    }

    protected void renderHelpTipIcons(GuiGraphics gui){
        RenderHelper.push(gui);
        this.helpTips.forEach(tip -> {
            gui.blit(WIDGETS, this.leftPos + tip.getX(), this.topPos + tip.getY(), tip.getTexX(), tip.getTexY(), tip.getWidth(), tip.getHeight());
        });
        RenderHelper.pop(gui);
    }

    protected void renderHelpTips(GuiGraphics gui, int mouseX, int mouseY){
        this.helpTips.forEach(tip -> {
            if (tip.isInField(mouseX - this.leftPos, mouseY - this.topPos)) {
                gui.renderTooltip(this.font, tip.getTexts(), Optional.empty(), mouseX, mouseY);
            }
        });
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int key) {
        return super.mouseClicked(mouseX, mouseY, key);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double p_99129_, double delta) {
        return super.mouseScrolled(mouseX, mouseY, p_99129_, delta);
    }

    protected boolean canClickInventoryButton(int id){
        if(this.minecraft != null && this.minecraft.player != null){
            return this.menu.clickMenuButton(this.minecraft.player, id);
        }
        return false;
    }

    protected void sendInventoryButtonClickPacket(int id){
        if(this.minecraft != null && this.minecraft.gameMode != null){
            this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, id);
        }
    }

}