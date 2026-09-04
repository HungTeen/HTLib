package hungteen.htlib.client.gui.screen.codec;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

/**
 * 标签绘制队列：rebuild 阶段只记录位置与文本，render 时一次性绘制并清空。
 * 两个 Codec 界面共用。
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/4 22:50
 */
public final class LabelQueue {

    private final List<Label> labels = new ArrayList<>();

    public void add(int x, int y, String text) {
        add(x, y, text, ViewerStyle.COLOR_TEXT);
    }

    public void add(int x, int y, String text, int color) {
        labels.add(new Label(x, y, text, color));
    }

    public void clear() {
        labels.clear();
    }

    /**
     * 绘制全部标签（清空由 rebuild/refreshTree 在填充前负责，不能每帧清，
     * 否则标签只在重建后的第一帧出现）。
     */
    public void render(GuiGraphics graphics, Font font) {
        for (Label l : labels) {
            graphics.drawString(font, l.text, l.x, l.y, l.color);
        }
    }

    private record Label(int x, int y, String text, int color) {
    }
}