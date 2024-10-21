package hungteen.htlib.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import hungteen.htlib.HTLib;
import hungteen.htlib.client.ClientProxy;
import hungteen.htlib.common.world.entity.DummyEntity;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-02 12:43
 **/
@Mod.EventBusSubscriber(modid = HTLib.MOD_ID, value = Dist.CLIENT)
public class LevelRenderHandler {

    private static final ResourceLocation FORCEFIELD_LOCATION = new ResourceLocation("textures/misc/forcefield.png");

    @SubscribeEvent
    public static void gatherComponents(RenderLevelStageEvent event){
        if(event.getStage() == RenderLevelStageEvent.Stage.AFTER_WEATHER){
            renderFormations(event);
        }
    }

    public static void renderFormations(RenderLevelStageEvent event){
        HTLib.PROXY.getDummyEntities().stream().filter(DummyEntity::renderBorder).forEach(entity -> {
                    renderBorderFormation(entity, event.getCamera());
                });
    }

    /**
     * {@link net.minecraft.client.renderer.LevelRenderer}
     */
    private static void renderBorderFormation(DummyEntity entityIn, Camera camera) {
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        final double validDistance = ClientProxy.MC.options.getEffectiveRenderDistance() * 16;
        final double maxX = entityIn.getMaxX();
        final double minX = entityIn.getMinX();
        final double maxZ = entityIn.getMaxZ();
        final double minZ = entityIn.getMinZ();
        final double originX = camera.getPosition().x;
        final double originZ = camera.getPosition().z;
        // Can see at least one side of border.
        if (originX > maxX - validDistance || originX < minX + validDistance || originZ > maxZ - validDistance || originZ > minZ + validDistance) {
//            double d1 = 1.0D - Mth.sqrt((float) entityIn.distanceToSqr(new Vec3(camera.getPosition().x, entityIn.getY(), camera.getPosition().z))) / validDistance;
            double d1 = 1;
            d1 = Math.pow(d1, 4.0D);
            d1 = Mth.clamp(d1, 0.0D, 1.0D);

            double d4 = ClientProxy.MC.gameRenderer.getDepthFar();
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderSystem.setShaderTexture(0, FORCEFIELD_LOCATION);
            RenderSystem.depthMask(Minecraft.useShaderTransparency());
            PoseStack posestack = RenderSystem.getModelViewStack();
            posestack.pushPose();
            RenderSystem.applyModelViewMatrix();
            final int color = entityIn.getBorderColor();
            final float red = (float)(color >> 16 & 255) / 255.0F;
            final float green = (float)(color >> 8 & 255) / 255.0F;
            final float blue = (float)(color & 255) / 255.0F;
            RenderSystem.setShaderColor(red, green, blue, (float)d1);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.polygonOffset(-3.0F, -3.0F);
            RenderSystem.enablePolygonOffset();
            RenderSystem.disableCull();
            float f3 = (float)(Util.getMillis() % 3000L) / 3000.0F;
            float f6 = (float)(d4 - Mth.frac(camera.getPosition().y));
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            final double validMinZ = Math.max(Mth.floor(originZ - validDistance), minZ);
            final double validMaxZ = Math.min(Mth.ceil(originZ + validDistance), maxZ);
            final double validMinX = Math.max(Mth.floor(originX - validDistance), minX);
            final double validMaxX = Math.min(Mth.ceil(originX + validDistance), maxX);
            // Can see the most far positive x border.
            if (originX > maxX - validDistance) {
                float f7 = 0.0F;
                for(double currentZ = validMinZ; currentZ < validMaxZ; f7 += 0.5F) {
                    double d8 = Math.min(1.0D, validMaxZ - currentZ);
                    float f8 = (float)d8 * 0.5F;
                    bufferbuilder.vertex(maxX - originX, -d4, currentZ - originZ).uv(f3 - f7, f3 + f6).endVertex();
                    bufferbuilder.vertex(maxX - originX, -d4, currentZ + d8 - originZ).uv(f3 - (f8 + f7), f3 + f6).endVertex();
                    bufferbuilder.vertex(maxX - originX, d4, currentZ + d8 - originZ).uv(f3 - (f8 + f7), f3 + 0.0F).endVertex();
                    bufferbuilder.vertex(maxX - originX, d4, currentZ - originZ).uv(f3 - f7, f3 + 0.0F).endVertex();
                    ++currentZ;
                }
            }
            // Can see the most far negative x border.
            if (originX < minX + validDistance) {
                float f9 = 0.0F;
                for(double currentZ = validMinZ; currentZ < validMaxZ; f9 += 0.5F) {
                    double d12 = Math.min(1.0D, validMaxZ - currentZ);
                    float f12 = (float)d12 * 0.5F;
                    bufferbuilder.vertex(minX - originX, -d4, currentZ - originZ).uv(f3 + f9, f3 + f6).endVertex();
                    bufferbuilder.vertex(minX - originX, -d4, currentZ + d12 - originZ).uv(f3 + f12 + f9, f3 + f6).endVertex();
                    bufferbuilder.vertex(minX - originX, d4, currentZ + d12 - originZ).uv(f3 + f12 + f9, f3 + 0.0F).endVertex();
                    bufferbuilder.vertex(minX - originX, d4, currentZ - originZ).uv(f3 + f9, f3 + 0.0F).endVertex();
                    ++currentZ;
                }
            }
            // Can see the most far positive z border.
            if (originZ > maxZ - validDistance) {
                float f10 = 0.0F;
                for(double d10 = validMinX; d10 < validMaxX; f10 += 0.5F) {
                    double d13 = Math.min(1.0D, validMaxX - d10);
                    float f13 = (float)d13 * 0.5F;
                    bufferbuilder.vertex(d10 - originX, -d4, maxZ - originZ).uv(f3 + f10, f3 + f6).endVertex();
                    bufferbuilder.vertex(d10 + d13 - originX, -d4, maxZ - originZ).uv(f3 + f13 + f10, f3 + f6).endVertex();
                    bufferbuilder.vertex(d10 + d13 - originX, d4, maxZ - originZ).uv(f3 + f13 + f10, f3 + 0.0F).endVertex();
                    bufferbuilder.vertex(d10 - originX, d4, maxZ - originZ).uv(f3 + f10, f3 + 0.0F).endVertex();
                    ++d10;
                }
            }
            // Can see the most far negative z border.
            if (originZ < minZ + validDistance) {
                float f11 = 0.0F;
                for(double d11 = validMinX; d11 < validMaxX; f11 += 0.5F) {
                    double d14 = Math.min(1.0D, validMaxX - d11);
                    float f14 = (float)d14 * 0.5F;
                    bufferbuilder.vertex(d11 - originX, -d4, minZ - originZ).uv(f3 - f11, f3 + f6).endVertex();
                    bufferbuilder.vertex(d11 + d14 - originX, -d4, minZ - originZ).uv(f3 - (f14 + f11), f3 + f6).endVertex();
                    bufferbuilder.vertex(d11 + d14 - originX, d4, minZ - originZ).uv(f3 - (f14 + f11), f3 + 0.0F).endVertex();
                    bufferbuilder.vertex(d11 - originX, d4, minZ - originZ).uv(f3 - f11, f3 + 0.0F).endVertex();
                    ++d11;
                }
            }
            BufferUploader.drawWithShader(bufferbuilder.end());
            RenderSystem.enableCull();
            RenderSystem.polygonOffset(0.0F, 0.0F);
            RenderSystem.disablePolygonOffset();
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
            posestack.popPose();
            RenderSystem.applyModelViewMatrix();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.depthMask(true);
        }
    }
}
