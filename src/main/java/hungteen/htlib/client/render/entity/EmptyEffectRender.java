package hungteen.htlib.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-07 22:42
 **/
public class EmptyEffectRender extends EntityRenderer<Entity> {

    public EmptyEffectRender(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(Entity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn,
                       MultiBufferSource bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    protected void renderNameTag(Entity entity, Component component, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
    }

    @Override
    public ResourceLocation getTextureLocation(Entity entity) {
        return null;
    }
}
