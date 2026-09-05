package hungteen.htlib.client.gui.widget.codec;

import hungteen.htlib.client.gui.screen.codec.node.BooleanNode;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

/**
 * 布尔控件：字段标签 + true/false 切换按钮。
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/5 22:50
 */
public final class BooleanWidget extends EditorWidget {

    private final BooleanNode node;
    private Button button;

    public BooleanWidget(EditorHost host, BooleanNode node) {
        super(host, node);
        this.node = node;
    }

    @Override
    protected void create() {
        button = createButton("", controlWidth(), b -> {
            node.toggle();
            button.setMessage(Component.literal(String.valueOf(node.isCurrentTrue())));
        });
        button.setMessage(Component.literal(String.valueOf(node.isCurrentTrue())));
    }

    @Override
    protected int doLayout() {
        button.setWidth(controlWidth());
        place(button, controlX(), y);
        return ROW_HEIGHT;
    }

    @Override
    protected void doRender(GuiGraphics graphics, Font font, int mouseX, int mouseY) {
        drawLabel(graphics, font, displayLabel(), labelColor());
    }

    @Override
    public void refreshFromNode() {
        button.setMessage(Component.literal(String.valueOf(node.isCurrentTrue())));
    }
}
