package hungteen.htlib.client.gui.widget.codec;

import hungteen.htlib.client.gui.screen.codec.node.ScalarNode;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;

/**
 * 标量控件：字段标签 + 输入框，校验失败文字变红。
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/5 22:50
 */
public final class ScalarWidget extends EditorWidget {

    private final ScalarNode node;
    private EditBox box;

    public ScalarWidget(EditorHost host, ScalarNode node) {
        super(host, node);
        this.node = node;
    }

    @Override
    protected void create() {
        box = createEditBox(node.valueText(), s -> {
            node.setValueText(s);
            applyErrorColor(box);
        });
        applyErrorColor(box);
    }

    @Override
    protected int doLayout() {
        box.setWidth(controlWidth());
        place(box, controlX(), y);
        return ROW_HEIGHT;
    }

    @Override
    protected void doRender(GuiGraphics graphics, Font font, int mouseX, int mouseY) {
        drawLabel(graphics, font, displayLabel(), labelColor());
    }

    @Override
    public void refreshFromNode() {
        node.validate();
        if (box != null) {
            box.setValue(node.valueText());
            applyErrorColor(box);
        }
    }
}
