package hungteen.htlib.client.util;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import hungteen.htlib.util.helper.MathHelper;
import hungteen.htlib.util.helper.StringHelper;
import hungteen.htlib.util.helper.impl.HTLibHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-06 23:16
 **/
public class RenderHelper {

    // WIDGETS.
    public static final ResourceLocation WIDGETS = HTLibHelper.get().guiTexture("widgets");
    public static final int ITEM_BAR_LEN = 13;

    public static void push(GuiGraphics gui){
        gui.pose().pushPose();
    }

    public static void pop(GuiGraphics gui){
        gui.pose().popPose();
    }

    /**
     * Render Entity on GUI.
     */
    public static void renderLivingOnGUI(LivingEntity entity, int posX, int posY, float scale, float mouseX, float mouseY, double xRot, double yRot, double zRot) {
        final float followRotX = (float) Math.atan((posX - mouseX) / 5.0F) * 1.5F;
        final float followRotY = - (float) Math.atan((posY - mouseY) / 40.0F);

        RenderSystem.applyModelViewMatrix();
        final PoseStack stack = new PoseStack();
        stack.translate(posX, posY, 120.0D);
        stack.scale(scale, scale, scale);

        final Quaternionf quaternion = Axis.ZP.rotationDegrees(180.0F);
        Quaternionf quaternion1 = Axis.XP.rotationDegrees(followRotY * 20.0F);
        quaternion.mul(quaternion1);
        stack.mulPose(quaternion);

        stack.mulPose(Axis.XP.rotationDegrees((float) xRot));
        stack.mulPose(Axis.YP.rotationDegrees((float) yRot));
        stack.mulPose(Axis.ZP.rotationDegrees((float) zRot));

        //storage rotation.
        final float f1 = entity.getYRot();
        final float f2 = entity.getXRot();
        final float f3 = entity.yBodyRot;
        final float f4 = entity.yBodyRotO;
        final float f5 = entity.yHeadRot;
        final float f6 = entity.yHeadRotO;

        final float yaw = - followRotX * 20.0F + (float) yRot;
        entity.setYRot(yaw);
        entity.setXRot(followRotY * 20.0F);
        entity.yBodyRot = yaw;
        entity.yBodyRotO = yaw;
        entity.yHeadRot = yaw;
        entity.yHeadRotO = yaw;

        quaternion1 = Axis.XP.rotationDegrees(- followRotY * 20.0F);
        quaternion.mul(quaternion1);

        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        quaternion1.conjugate();

        dispatcher.overrideCameraOrientation(quaternion1);
        dispatcher.setRenderShadow(false);
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> {
            dispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, stack, bufferSource, 15728880);
        });
        bufferSource.endBatch();
        dispatcher.setRenderShadow(false);
        dispatcher.setRenderHitBoxes(false);

        //restore rotation.
        entity.setYRot(f1);
        entity.setXRot(f2);
        entity.yBodyRot = f3;
        entity.yBodyRotO = f4;
        entity.yHeadRot = f5;
        entity.yHeadRotO = f6;

        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
    }

    public static void renderScrollBar(GuiGraphics gui, int leftPos, int topPos, int currentPos, int totalCount, int countPerPage, int barLen){
        push(gui);
        if(totalCount > countPerPage){
            final int len = MathHelper.getBarLen(currentPos, totalCount - countPerPage, barLen - 15);
            gui.blit(WIDGETS, leftPos, topPos + len, 15, 0, 12, 15);
        } else{
            gui.blit(WIDGETS, leftPos, topPos, 27, 0, 12, 15);
        }
        pop(gui);
    }

    public static void setTexture(ResourceLocation texture){
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture);
    }

    /* Text */

    public static void renderText(PoseStack stack, Font font, MultiBufferSource bufferSource, String text, float x, float y, int color, int outlineColor, TextRenderType renderType) {
        renderText(stack, font, bufferSource, StringHelper.format(text), x, y, color, outlineColor, renderType, LightTexture.FULL_BRIGHT);
    }

    public static void renderText(PoseStack stack, Font font, MultiBufferSource bufferSource, Component text, float x, float y, int color, int outlineColor, TextRenderType renderType) {
        renderText(stack, font, bufferSource, StringHelper.format(text), x, y, color, outlineColor, renderType, LightTexture.FULL_BRIGHT);
    }

    public static void renderText(PoseStack stack, Font font, MultiBufferSource bufferSource, FormattedCharSequence text, float x, float y, int color, int outlineColor, TextRenderType renderType, int packedLight) {
        renderType.render(font, stack.last().pose(), bufferSource, text, x, y, color, outlineColor, packedLight);
//        bufferSource.endLastBatch();
    }

    public static void renderScaledText(PoseStack stack, Font font, MultiBufferSource bufferSource, String text, float x, float y, float scale, int color, int outlineColor, TextRenderType renderType, int packedLight) {
        renderScaledText(stack, font, bufferSource, StringHelper.format(text), x, y, scale, color, outlineColor, renderType, packedLight);
    }

    public static void renderScaledText(PoseStack stack, Font font, MultiBufferSource bufferSource, Component text, float x, float y, float scale, int color, int outlineColor, TextRenderType renderType, int packedLight) {
        renderScaledText(stack, font, bufferSource, StringHelper.format(text), x, y, scale, color, outlineColor, renderType, packedLight);
    }

    public static void renderScaledText(PoseStack stack, Font font, MultiBufferSource bufferSource, FormattedCharSequence text, float x, float y, float scale, int color, int outlineColor, TextRenderType renderType, int packedLight) {
        stack.pushPose();
        stack.scale(scale, scale, 1F);
        renderText(stack, font, bufferSource, text, x / scale, y / scale, color, outlineColor, renderType, packedLight);
        stack.popPose();
    }

    public static void renderCenteredText(PoseStack stack, Font font, MultiBufferSource bufferSource, String text, float x, float y, int color, int outlineColor, TextRenderType renderType, int packedLight) {
        renderCenteredText(stack, font, bufferSource, StringHelper.format(text), x, y, color, outlineColor, renderType, packedLight);
    }

    public static void renderCenteredText(PoseStack stack, Font font, MultiBufferSource bufferSource, Component text, float x, float y, int color, int outlineColor, TextRenderType renderType, int packedLight) {
        renderCenteredText(stack, font, bufferSource, StringHelper.format(text), x, y, color, outlineColor, renderType, packedLight);
    }

    public static void renderCenteredText(PoseStack stack, Font font, MultiBufferSource bufferSource, FormattedCharSequence text, float x, float y, int color, int outlineColor, TextRenderType renderType, int packedLight) {
        renderText(stack, font, bufferSource, text, x - font.width(text) * 1F / 2, y, color, outlineColor, renderType, packedLight);
    }

    public static void renderCenterScaledText(PoseStack stack, Font font, MultiBufferSource bufferSource, String text, float x, float y, float scale, int color, int outlineColor, TextRenderType renderType, int packedLight) {
        renderCenterScaledText(stack, font, bufferSource, StringHelper.format(text), x, y, scale, color, outlineColor, renderType, packedLight);
    }

    public static void renderCenterScaledText(PoseStack stack, Font font, MultiBufferSource bufferSource, Component text, float x, float y, float scale, int color, int outlineColor, TextRenderType renderType, int packedLight) {
        renderCenterScaledText(stack, font, bufferSource, StringHelper.format(text), x, y, scale, color, outlineColor, renderType, packedLight);
    }

    public static void renderCenterScaledText(PoseStack stack, Font font, MultiBufferSource bufferSource, FormattedCharSequence text, float x, float y, float scale, int color, int outlineColor, TextRenderType renderType, int packedLight) {
        stack.pushPose();
        stack.scale(scale, scale, 1F);
        renderText(stack, font, bufferSource, text, (x - font.width(text) * scale / 2 ) / scale, y / scale, color, outlineColor, renderType, packedLight);
        stack.popPose();
    }

    public static ModelPart getModelRoot(ModelLayerLocation location){
        return ClientHelper.mc().getEntityModels().bakeLayer(location);
    }

}
