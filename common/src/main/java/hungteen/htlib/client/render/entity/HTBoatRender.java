package hungteen.htlib.client.render.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import hungteen.htlib.client.HTModelLayers;
import hungteen.htlib.util.HasHTBoatType;
import hungteen.htlib.common.impl.HTLibBoatTypes;
import hungteen.htlib.util.BoatType;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.WaterPatchModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.BoatRenderer;
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
public class HTBoatRender extends BoatRenderer {

    private final Map<BoatType, Pair<ResourceLocation, ListModel<Boat>>> boatResources;

    public HTBoatRender(EntityRendererProvider.Context context, boolean hasChest) {
        super(context, hasChest);
        this.boatResources = HTLibBoatTypes.getBoatTypes().stream().collect(ImmutableMap.toImmutableMap((type) -> {
            return type;
        }, (type) -> {
            return Pair.of(ResourceLocation.fromNamespaceAndPath(type.getModID(), getTextureLocation(type, hasChest)), this.createBoatModel(context, type, hasChest));
        }));
    }

    private static String getTextureLocation(BoatType type, boolean hasChest) {
        return "textures/entity/" + (hasChest ? "chest_" : "") + "boat/" + type.getName() + ".png";
    }

    private ListModel<Boat> createBoatModel(EntityRendererProvider.Context context, BoatType boatType, boolean hasChest) {
        final ModelLayerLocation modellayerlocation = hasChest ? HTModelLayers.createChestBoatModelName(boatType) : HTModelLayers.createBoatModelName(boatType);
        final ModelPart modelpart = context.bakeLayer(modellayerlocation);
        return hasChest ? new ChestBoatModel(modelpart) : new BoatModel(modelpart);
    }

    /**
     * Overwrite from super method.
     */
    @Override
    public void render(Boat boat, float $$1, float $$2, PoseStack stack, MultiBufferSource source, int $$5) {
        stack.pushPose();
        stack.translate(0.0F, 0.375F, 0.0F);
        stack.mulPose(Axis.YP.rotationDegrees(180.0F - $$1));
        float $$6 = (float)boat.getHurtTime() - $$2;
        float $$7 = boat.getDamage() - $$2;
        if ($$7 < 0.0F) {
            $$7 = 0.0F;
        }

        if ($$6 > 0.0F) {
            stack.mulPose(Axis.XP.rotationDegrees(Mth.sin($$6) * $$6 * $$7 / 10.0F * (float)boat.getHurtDir()));
        }

        float $$8 = boat.getBubbleAngle($$2);
        if (!Mth.equal($$8, 0.0F)) {
            stack.mulPose(new Quaternionf().setAngleAxis(boat.getBubbleAngle($$2) * (float) (Math.PI / 180.0), 1.0F, 0.0F, 1.0F));
        }

        Pair<ResourceLocation, ListModel<Boat>> $$9 = getModelWithLocation(boat);
        ResourceLocation $$10 = $$9.getFirst();
        ListModel<Boat> $$11 = $$9.getSecond();
        stack.scale(-1.0F, -1.0F, 1.0F);
        stack.mulPose(Axis.YP.rotationDegrees(90.0F));
        $$11.setupAnim(boat, $$2, 0.0F, -0.1F, 0.0F, 0.0F);
        VertexConsumer $$12 = source.getBuffer($$11.renderType($$10));
        $$11.renderToBuffer(stack, $$12, $$5, OverlayTexture.NO_OVERLAY);
        if (!boat.isUnderWater()) {
            VertexConsumer $$13 = source.getBuffer(RenderType.waterMask());
            if ($$11 instanceof WaterPatchModel $$14) {
                $$14.waterPatch().render(stack, $$13, $$5, OverlayTexture.NO_OVERLAY);
            }
        }

        stack.popPose();
        super.render(boat, $$1, $$2, stack, source, $$5);
    }

    @Deprecated // forge: override getModelWithLocation to change the texture / model
    @Override
    public ResourceLocation getTextureLocation(Boat boat) {
        return getModelWithLocation(boat).getFirst();
    }

    public Pair<ResourceLocation, ListModel<Boat>> getModelWithLocation(Boat boat) {
        if(boat instanceof HasHTBoatType htBoat) {
            return this.boatResources.get(htBoat.getHTBoatType());
        }
        throw new IllegalStateException("Boat entity must implement HasHTBoatType to use HTBoatRender");
    }

}
