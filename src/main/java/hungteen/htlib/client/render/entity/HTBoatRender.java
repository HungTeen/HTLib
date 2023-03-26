package hungteen.htlib.client.render.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import hungteen.htlib.client.HTModelLayers;
import hungteen.htlib.common.WoodIntegrations;
import hungteen.htlib.common.entity.HTBoat;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.WaterPatchModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;
import org.joml.Quaternionf;

import java.util.Map;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-13 23:09
 **/
public class HTBoatRender extends EntityRenderer<HTBoat> {

    private final Map<WoodIntegrations.IBoatType, Pair<ResourceLocation, ListModel<Boat>>> boatResources;

    public HTBoatRender(EntityRendererProvider.Context context, boolean hasChest) {
        super(context);
        this.shadowRadius = 0.8F;
        this.boatResources = WoodIntegrations.getBoatTypes().stream().collect(ImmutableMap.toImmutableMap((type) -> {
            return type;
        }, (type) -> {
            return Pair.of(new ResourceLocation(type.getModID(), getTextureLocation(type, hasChest)), this.createBoatModel(context, type, hasChest));
        }));
    }

    private static String getTextureLocation(WoodIntegrations.IBoatType type, boolean hasChest) {
        return "textures/entity/" + (hasChest ? "chest_" : "") + "boat/" + type.getName() + ".png";
    }

    private ListModel<Boat> createBoatModel(EntityRendererProvider.Context context, WoodIntegrations.IBoatType boatType, boolean hasChest) {
        final ModelLayerLocation modellayerlocation = hasChest ? HTModelLayers.createChestBoatModelName(boatType) : HTModelLayers.createBoatModelName(boatType);
        final ModelPart modelpart = context.bakeLayer(modellayerlocation);
        return (ListModel<Boat>)(hasChest ? new ChestBoatModel(modelpart) : new BoatModel(modelpart));
    }

    /**
     * Copy from {@link BoatRenderer#render(Boat, float, float, PoseStack, MultiBufferSource, int)}.
     */
    public void render(HTBoat boat, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLightIn) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.375D, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entityYaw));
        float f = (float)boat.getHurtTime() - partialTicks;
        float f1 = boat.getDamage() - partialTicks;
        if (f1 < 0.0F) {
            f1 = 0.0F;
        }

        if (f > 0.0F) {
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.sin(f) * f * f1 / 10.0F * (float)boat.getHurtDir()));
        }

        float f2 = boat.getBubbleAngle(partialTicks);
        if (!Mth.equal(f2, 0.0F)) {
            poseStack.mulPose(new Quaternionf().setAngleAxis(boat.getBubbleAngle(partialTicks) * ((float)Math.PI / 180F), 1.0F, 0.0F, 1.0F));
        }

        Pair<ResourceLocation, ListModel<Boat>> pair = getModelWithLocation(boat);
        ResourceLocation resourcelocation = pair.getFirst();
        ListModel<Boat> listModel = pair.getSecond();
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        listModel.setupAnim(boat, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
        VertexConsumer vertexconsumer = bufferSource.getBuffer(listModel.renderType(resourcelocation));
        listModel.renderToBuffer(poseStack, vertexconsumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        if (!boat.isUnderWater()) {
            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.waterMask());
            if (listModel instanceof WaterPatchModel model) {
                model.waterPatch().render(poseStack, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY);
            }
        }

        poseStack.popPose();
        super.render(boat, entityYaw, partialTicks, poseStack, bufferSource, packedLightIn);
    }

    @Deprecated // forge: override getModelWithLocation to change the texture / model
    public ResourceLocation getTextureLocation(HTBoat boat) {
        return getModelWithLocation(boat).getFirst();
    }

    public Pair<ResourceLocation, ListModel<Boat>> getModelWithLocation(HTBoat boat) {
        return this.boatResources.get(boat.getHTBoatType());
    }
}
