package hungteen.htlib.client.gui.widget.codec;

import hungteen.htlib.client.gui.screen.codec.EditorHost;
import hungteen.htlib.client.gui.screen.codec.node.EnumNode;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

/**
 * 枚举控件：字段标签 + 行内补全选择器。
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/5 22:50
 */
public final class EnumWidget extends EditorWidget {

    private final EnumNode node;
    private TypeSelector selector;

    public EnumWidget(EditorHost host, EnumNode node) {
        super(host, node);
        this.node = node;
    }

    @Override
    protected void create() {
        if (node.enumValues().isEmpty()) {
            return;
        }
        selector = createSelector(SELECTOR_WIDTH, node.enumValues(), currentValue(), node::setEnumValue);
    }

    @Override
    protected int doLayout() {
        if (selector != null) {
            selector.setPosition(controlX(), y);
            selector.setVisible(!hidden && rowVisible(y));
        }
        return ROW_HEIGHT;
    }

    @Override
    protected void doRender(GuiGraphics graphics, Font font, int mouseX, int mouseY) {
        drawLabel(graphics, font, displayLabel(), labelColor());
    }

    @Override
    public void refreshFromNode() {
        if (selector != null) {
            selector.setValue(currentValue());
        }
    }

    @Override
    protected void onDispose() {
        removeSelector(selector);
    }

    private String currentValue() {
        List<String> values = node.enumValues();
        int idx = node.enumIndex();
        return idx >= 0 && idx < values.size() ? values.get(idx) : "";
    }
}
