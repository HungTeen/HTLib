package hungteen.htlib.client.gui.widget.codec;

import hungteen.htlib.client.gui.screen.codec.node.MapNode;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;

import java.util.ArrayList;
import java.util.List;

/**
 * 映射控件：字段标签 + [＋][－] 增删按钮 + 键/值成对的子控件。
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/5 22:50
 */
public final class MapWidget extends EditorWidget {

    private final MapNode node;
    private final List<EditorWidget> keyWidgets = new ArrayList<>();
    private final List<EditorWidget> valueWidgets = new ArrayList<>();
    private Button plus;
    private Button minus;

    public MapWidget(EditorHost host, MapNode node) {
        super(host, node);
        this.node = node;
    }

    @Override
    protected void create() {
        createCollapseToggle();
        plus = createButton("+", 20, b -> {
            node.addEntry();
            expand();
            syncChildren(node.keys(), keyWidgets, host);
            syncChildren(node.values(), valueWidgets, host);
            host.relayout();
        });
        minus = createButton("-", 20, b -> {
            node.removeLastEntry();
            syncChildren(node.keys(), keyWidgets, host);
            syncChildren(node.values(), valueWidgets, host);
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
        place(minus, rightEdge() - TOGGLE_WIDTH - TOGGLE_GAP - 20, y);
        place(plus, rightEdge() - TOGGLE_WIDTH - TOGGLE_GAP - 20 - 20 - 3, y);
        if (isCollapsed()) {
            for (EditorWidget w : keyWidgets) {
                w.setHidden(true);
            }
            for (EditorWidget w : valueWidgets) {
                w.setHidden(true);
            }
            return ROW_HEIGHT;
        }
        syncChildren(node.keys(), keyWidgets, host);
        syncChildren(node.values(), valueWidgets, host);
        int nextY = y + ROW_HEIGHT;
        for (int i = 0; i < keyWidgets.size(); i++) {
            nextY += keyWidgets.get(i).layout(x + INDENT, nextY, width - INDENT);
            if (i < valueWidgets.size()) {
                nextY += valueWidgets.get(i).layout(x + INDENT, nextY, width - INDENT);
            }
        }
        return nextY - y;
    }

    @Override
    protected void doRender(GuiGraphics graphics, Font font, int mouseX, int mouseY) {
        drawLabel(graphics, font, displayLabel(), labelColor());
        for (EditorWidget w : keyWidgets) {
            w.render(graphics, font, mouseX, mouseY);
        }
        for (EditorWidget w : valueWidgets) {
            w.render(graphics, font, mouseX, mouseY);
        }
    }

    @Override
    public boolean renderTooltip(GuiGraphics graphics, Font font, double mouseX, double mouseY) {
        if (renderSelfTooltip(graphics, font, mouseX, mouseY)) {
            return true;
        }
        for (EditorWidget w : keyWidgets) {
            if (w.renderTooltip(graphics, font, mouseX, mouseY)) {
                return true;
            }
        }
        for (EditorWidget w : valueWidgets) {
            if (w.renderTooltip(graphics, font, mouseX, mouseY)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void refreshFromNode() {
        syncChildren(node.keys(), keyWidgets, host);
        syncChildren(node.values(), valueWidgets, host);
        for (EditorWidget w : keyWidgets) {
            w.refreshFromNode();
        }
        for (EditorWidget w : valueWidgets) {
            w.refreshFromNode();
        }
    }

    @Override
    public void setHidden(boolean hidden) {
        super.setHidden(hidden);
        if (hidden) {
            for (EditorWidget w : keyWidgets) {
                w.setHidden(true);
            }
            for (EditorWidget w : valueWidgets) {
                w.setHidden(true);
            }
        }
    }

    @Override
    protected void onDispose() {
        for (EditorWidget w : keyWidgets) {
            w.dispose();
        }
        for (EditorWidget w : valueWidgets) {
            w.dispose();
        }
    }
}
