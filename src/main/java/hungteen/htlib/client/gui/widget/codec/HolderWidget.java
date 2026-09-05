package hungteen.htlib.client.gui.widget.codec;

import com.google.gson.JsonPrimitive;
import hungteen.htlib.client.gui.screen.codec.node.HolderNode;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;

/**
 * 注册表引用控件（HOLDER / HOLDER_SET）：优先用补全选择器枚举条目；
 * 枚举不到（如自定义注册表）时退回普通输入框，允许手填。
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/5 22:50
 */
public final class HolderWidget extends EditorWidget {

    private final HolderNode node;
    private TypeSelector selector;
    private EditBox box;

    public HolderWidget(EditorHost host, HolderNode node) {
        super(host, node);
        this.node = node;
    }

    @Override
    protected void create() {
        if (node.entries().isEmpty()) {
            box = createEditBox(node.valueText(), s -> {
                node.setValueText(s);
                applyErrorColor(box);
            });
            applyErrorColor(box);
        } else {
            selector = createSelector(controlWidth(), node.entries(), node.valueText(),
                name -> node.setValue(new JsonPrimitive(name)));
        }
    }

    @Override
    protected int doLayout() {
        if (box != null) {
            box.setWidth(controlWidth());
            place(box, controlX(), y);
        } else if (selector != null) {
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
        if (box != null) {
            box.setValue(node.valueText());
            applyErrorColor(box);
        } else if (selector != null) {
            selector.setValue(node.valueText());
        }
    }

    @Override
    protected void onDispose() {
        removeSelector(selector);
    }
}
