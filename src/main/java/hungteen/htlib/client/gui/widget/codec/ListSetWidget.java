package hungteen.htlib.client.gui.widget.codec;

import hungteen.htlib.client.gui.screen.codec.node.ListSetNode;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;

import java.util.ArrayList;
import java.util.List;

/**
 * 列表/集合控件：字段标签 + [＋][－] 增删按钮 + 元素子控件。
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/5 22:50
 */
public final class ListSetWidget extends EditorWidget {

    private final ListSetNode node;
    private final List<EditorWidget> itemWidgets = new ArrayList<>();
    private Button plus;
    private Button minus;

    public ListSetWidget(EditorHost host, ListSetNode node) {
        super(host, node);
        this.node = node;
    }

    @Override
    protected void create() {
        createCollapseToggle();
        plus = createButton("+", 12, b -> {
            node.addItem();
            expand();
            syncChildren(node.items(), itemWidgets, host);
            host.relayout();
        });
        minus = createButton("-", 12, b -> {
            node.removeLastItem();
            syncChildren(node.items(), itemWidgets, host);
            host.relayout();
        });
    }

    @Override
    protected boolean collapsible() {
        return true;
    }

    @Override
    protected int doLayout() {
        placeCollapseToggle();
        place(minus, rightEdge() - TOGGLE_WIDTH - TOGGLE_GAP - 12, y);
        place(plus, rightEdge() - TOGGLE_WIDTH - TOGGLE_GAP - 12 - 12 - 3, y);
        minus.visible = !hidden && rowVisible(y) && !node.items().isEmpty();
        if (isCollapsed()) {
            for (EditorWidget w : itemWidgets) {
                w.setHidden(true);
            }
            return ROW_HEIGHT;
        }
        syncChildren(node.items(), itemWidgets, host);
        int nextY = y + ROW_HEIGHT;
        for (EditorWidget w : itemWidgets) {
            nextY += w.layout(x + INDENT, nextY, width - INDENT);
        }
        return nextY - y;
    }

    @Override
    protected void doRender(GuiGraphics graphics, Font font, int mouseX, int mouseY) {
        drawLabel(graphics, font, displayLabel(), labelColor());
        for (EditorWidget w : itemWidgets) {
            w.render(graphics, font, mouseX, mouseY);
        }
    }

    @Override
    public boolean renderTooltip(GuiGraphics graphics, Font font, double mouseX, double mouseY) {
        if (renderSelfTooltip(graphics, font, mouseX, mouseY)) {
            return true;
        }
        for (EditorWidget w : itemWidgets) {
            if (w.renderTooltip(graphics, font, mouseX, mouseY)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void refreshFromNode() {
        node.validate();
        syncChildren(node.items(), itemWidgets, host);
        for (EditorWidget w : itemWidgets) {
            w.refreshFromNode();
        }
    }

    @Override
    public void setHidden(boolean hidden) {
        super.setHidden(hidden);
        if (hidden) {
            for (EditorWidget w : itemWidgets) {
                w.setHidden(true);
            }
        }
    }

    @Override
    protected void onDispose() {
        for (EditorWidget w : itemWidgets) {
            w.dispose();
        }
    }
}
