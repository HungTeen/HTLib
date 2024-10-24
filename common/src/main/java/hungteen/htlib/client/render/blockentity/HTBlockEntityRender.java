package hungteen.htlib.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import hungteen.htlib.common.blockentity.HTBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-09 09:51
 **/
public class HTBlockEntityRender<T extends HTBlockEntity> implements BlockEntityRenderer<T> {
    @Override
    public void render(T blockEntity, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

    }
}
