package hungteen.htlib.client;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import hungteen.htlib.util.helper.HTLibHelper;
import hungteen.htlib.util.helper.MathHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
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

    public static void renderScrollBar(Screen screen, PoseStack stack, int leftPos, int topPos, int currentPos, int totalCount, int countPerPage, int barLen){
        RenderSystem.setShaderTexture(0, WIDGETS);
        stack.pushPose();
        if(totalCount > countPerPage){
            final int len = MathHelper.getBarLen(currentPos, totalCount - countPerPage, barLen - 15);
            screen.blit(stack, leftPos, topPos + len, 15, 0, 12, 15);
        } else{
            screen.blit(stack, leftPos, topPos, 27, 0, 12, 15);
        }
        stack.popPose();
    }

    public static void setTexture(ResourceLocation texture){
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture);
    }

    public static void drawScaledString(PoseStack stack, Font render, String string, int x, int y, int color, float scale) {
        stack.pushPose();
        stack.scale(scale, scale, scale);
        render.draw(stack, string, x / scale, y / scale, color);
        stack.popPose();
    }

    public static void drawCenteredString(PoseStack stack, Font render, String string, int x, int y, int color) {
        final int width = render.width(string);
        render.draw(stack, string, x - width / 2, y, color);
    }

    public static void drawCenteredScaledString(PoseStack stack, Font render, String string, int x, int y, int color,
                                                float scale) {
        int width = render.width(string);
        stack.pushPose();
        stack.scale(scale, scale, scale);
        render.draw(stack, string, (x - width / 2 * scale) / scale, y / scale, color);
        stack.popPose();
    }

    public static ModelPart getModelRoot(ModelLayerLocation location){
        return ClientProxy.MC.getEntityModels().bakeLayer(location);
    }

}
