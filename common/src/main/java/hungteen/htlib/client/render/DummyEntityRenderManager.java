package hungteen.htlib.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import hungteen.htlib.client.util.ClientHelper;
import hungteen.htlib.common.HTLibProxy;
import hungteen.htlib.common.world.entity.DummyEntity;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/27 10:29
 **/
public class DummyEntityRenderManager {

    private static final ResourceLocation FORCEFIELD_LOCATION = ResourceLocation.parse("textures/misc/forcefield.png");

    public static void renderFormations(Camera camera) {
        HTLibProxy.get().getDummyEntities().stream().filter(DummyEntity::renderBorder).forEach(entity -> {
            renderBorderFormation(entity, camera);
        });
    }

    /**
     * {@link net.minecraft.client.renderer.LevelRenderer}
     */
    public static void renderBorderFormation(DummyEntity entityIn, Camera camera) {
        final double validDistance = ClientHelper.option().getEffectiveRenderDistance() * 16;
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

            float d4 = ClientHelper.mc().gameRenderer.getDepthFar();
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderSystem.setShaderTexture(0, FORCEFIELD_LOCATION);
            RenderSystem.depthMask(Minecraft.useShaderTransparency());
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

            final double validMinZ = Math.max(Mth.floor(originZ - validDistance), minZ);
            final double validMaxZ = Math.min(Mth.ceil(originZ + validDistance), maxZ);
            final double validMinX = Math.max(Mth.floor(originX - validDistance), minX);
            final double validMaxX = Math.min(Mth.ceil(originX + validDistance), maxX);

            BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

            // Can see the most far positive x border.
            if (originX > maxX - validDistance) {
                float f7 = 0.0F;
                for(double currentZ = validMinZ; currentZ < validMaxZ; f7 += 0.5F) {
                    double d8 = Math.min(1.0D, validMaxZ - currentZ);
                    float f8 = (float)d8 * 0.5F;
                    bufferbuilder.addVertex((float)(maxX - originX), -d4, (float) (currentZ - originZ)).setUv(f3 - f7, f3 + f6);
                    bufferbuilder.addVertex((float) (maxX - originX), -d4, (float) (currentZ + d8 - originZ)).setUv(f3 - (f8 + f7), f3 + f6);
                    bufferbuilder.addVertex((float) (maxX - originX), d4, (float) (currentZ + d8 - originZ)).setUv(f3 - (f8 + f7), f3 + 0.0F);
                    bufferbuilder.addVertex((float) (maxX - originX), d4, (float) (currentZ - originZ)).setUv(f3 - f7, f3 + 0.0F);
                    ++currentZ;
                }
            }
            // Can see the most far negative x border.
            if (originX < minX + validDistance) {
                float f9 = 0.0F;
                for(double currentZ = validMinZ; currentZ < validMaxZ; f9 += 0.5F) {
                    double d12 = Math.min(1.0D, validMaxZ - currentZ);
                    float f12 = (float)d12 * 0.5F;
                    bufferbuilder.addVertex((float) (minX - originX), -d4, (float) (currentZ - originZ)).setUv(f3 + f9, f3 + f6);
                    bufferbuilder.addVertex((float) (minX - originX), -d4, (float) (currentZ + d12 - originZ)).setUv(f3 + f12 + f9, f3 + f6);
                    bufferbuilder.addVertex((float) (minX - originX), d4, (float) (currentZ + d12 - originZ)).setUv(f3 + f12 + f9, f3 + 0.0F);
                    bufferbuilder.addVertex((float) (minX - originX), d4, (float) (currentZ - originZ)).setUv(f3 + f9, f3 + 0.0F);
                    ++currentZ;
                }
            }
            // Can see the most far positive z border.
            if (originZ > maxZ - validDistance) {
                float f10 = 0.0F;
                for(double d10 = validMinX; d10 < validMaxX; f10 += 0.5F) {
                    double d13 = Math.min(1.0D, validMaxX - d10);
                    float f13 = (float)d13 * 0.5F;
                    bufferbuilder.addVertex((float) (d10 - originX), -d4, (float) (maxZ - originZ)).setUv(f3 + f10, f3 + f6);
                    bufferbuilder.addVertex((float) (d10 + d13 - originX), -d4, (float) (maxZ - originZ)).setUv(f3 + f13 + f10, f3 + f6);
                    bufferbuilder.addVertex((float) (d10 + d13 - originX), d4, (float) (maxZ - originZ)).setUv(f3 + f13 + f10, f3 + 0.0F);
                    bufferbuilder.addVertex((float) (d10 - originX), d4, (float) (maxZ - originZ)).setUv(f3 + f10, f3 + 0.0F);
                    ++d10;
                }
            }
            // Can see the most far negative z border.
            if (originZ < minZ + validDistance) {
                float f11 = 0.0F;
                for(double d11 = validMinX; d11 < validMaxX; f11 += 0.5F) {
                    double d14 = Math.min(1.0D, validMaxX - d11);
                    float f14 = (float)d14 * 0.5F;
                    bufferbuilder.addVertex((float) (d11 - originX), -d4, (float) (minZ - originZ)).setUv(f3 - f11, f3 + f6);
                    bufferbuilder.addVertex((float) (d11 + d14 - originX), -d4, (float) (minZ - originZ)).setUv(f3 - (f14 + f11), f3 + f6);
                    bufferbuilder.addVertex((float) (d11 + d14 - originX), d4, (float) (minZ - originZ)).setUv(f3 - (f14 + f11), f3 + 0.0F);
                    bufferbuilder.addVertex((float) (d11 - originX), d4, (float) (minZ - originZ)).setUv(f3 - f11, f3 + 0.0F);
                    ++d11;
                }
            }
            MeshData meshdata = bufferbuilder.build();
            if (meshdata != null) {
                BufferUploader.drawWithShader(meshdata);
            }
            RenderSystem.enableCull();
            RenderSystem.polygonOffset(0.0F, 0.0F);
            RenderSystem.disablePolygonOffset();
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.applyModelViewMatrix();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.depthMask(true);
        }
    }
}
