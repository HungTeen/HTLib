package hungteen.htlib.client.util;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;


/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/7/10 9:58
 */
public interface TextRenderType {

    default void render(Font font, Matrix4f pose, MultiBufferSource bufferSource, String text, float x, float y, int color, int outlineColor, int packedLight){
        render(font, pose, bufferSource, Component.literal(text), x, y, color, outlineColor, packedLight);
    }

    default void render(Font font, Matrix4f pose, MultiBufferSource bufferSource, Component text, float x, float y, int color, int outlineColor, int packedLight){
        render(font, pose, bufferSource, text.getVisualOrderText(), x, y, color, outlineColor, packedLight);
    }

    void render(Font font, Matrix4f pose, MultiBufferSource bufferSource, FormattedCharSequence text, float x, float y, int color, int outlineColor, int packedLight);

    TextRenderType NORMAL = (font, pose, bufferSource, text, x, y, color, outlineColor, packedLight) -> {
        font.drawInBatch(text, x, y, color, false, pose, bufferSource, Font.DisplayMode.NORMAL, outlineColor, packedLight);
    };

}
