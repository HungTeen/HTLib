package hungteen.htlib.client.gui.widget.codec;

import hungteen.htlib.client.gui.screen.codec.node.RecordNode;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

/**
 * 记录/对象控件：仅绘制标题行（可选），字段编辑全部由子控件承担。
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/5 22:50
 */
public final class RecordWidget extends EditorWidget {

    private final RecordNode node;
    private final List<EditorWidget> childWidgets = new ArrayList<>();

    public RecordWidget(EditorHost host, RecordNode node) {
        super(host, node);
        this.node = node;
    }

    @Override
    protected void create() {
        if (collapsible()) {
            createCollapseToggle();
        }
    }

    @Override
    protected boolean collapsible() {
        return !node.label().isEmpty();
    }

    @Override
    protected int doLayout() {
        placeCollapseToggle();
        if (isCollapsed()) {
            for (EditorWidget w : childWidgets) {
                w.setHidden(true);
            }
            return ROW_HEIGHT;
        }
        syncChildren(node.children(), childWidgets, host);
        int nextY = y;
        if (!node.label().isEmpty()) {
            nextY += ROW_HEIGHT;
        }
        for (EditorWidget w : childWidgets) {
            nextY += w.layout(x + INDENT, nextY, width - INDENT);
        }
        return nextY - y;
    }

    @Override
    protected void doRender(GuiGraphics graphics, Font font, int mouseX, int mouseY) {
        if (!node.label().isEmpty()) {
            drawLabel(graphics, font, node.label(), labelColor());
        }
        for (EditorWidget w : childWidgets) {
            w.render(graphics, font, mouseX, mouseY);
        }
    }

    @Override
    public boolean renderTooltip(GuiGraphics graphics, Font font, double mouseX, double mouseY) {
        for (EditorWidget w : childWidgets) {
            if (w.renderTooltip(graphics, font, mouseX, mouseY)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void refreshFromNode() {
        syncChildren(node.children(), childWidgets, host);
        for (EditorWidget w : childWidgets) {
            w.refreshFromNode();
        }
    }

    @Override
    public void setHidden(boolean hidden) {
        super.setHidden(hidden);
        if (hidden) {
            for (EditorWidget w : childWidgets) {
                w.setHidden(true);
            }
        }
    }

    @Override
    protected void onDispose() {
        for (EditorWidget w : childWidgets) {
            w.dispose();
        }
    }
}
