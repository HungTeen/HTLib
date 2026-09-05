package hungteen.htlib.client.gui.widget.codec;

import hungteen.htlib.client.gui.screen.codec.node.EditorFormNode;
import hungteen.htlib.client.gui.screen.codec.node.OptionalNode;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/**
 * 可选控件：内层为标量时与外层字段名内联到同一行；内层为结构型时提供 [+]/[-] 折叠按钮。
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/5 22:50
 */
public final class OptionalWidget extends EditorWidget {

    private final OptionalNode node;
    private EditorWidget innerWidget;

    public OptionalWidget(EditorHost host, OptionalNode node) {
        super(host, node);
        this.node = node;
    }

    @Override
    protected void create() {
        if (node.structuralInner()) {
            createCollapseToggle();
        }
    }

    @Override
    protected boolean collapsible() {
        return node.structuralInner() && node.inner() != null;
    }

    @Override
    protected int doLayout() {
        if (node.inner() == null) {
            return ROW_HEIGHT;
        }
        if (node.structuralInner()) {
            placeCollapseToggle();
            if (isCollapsed()) {
                if (innerWidget != null) {
                    innerWidget.setHidden(true);
                }
                return ROW_HEIGHT;
            }
            return ROW_HEIGHT + inner().layout(x + INDENT, y + ROW_HEIGHT, width - INDENT);
        }
        return inner().layoutInline(x, y, width, node.label());
    }

    @Override
    protected void doRender(GuiGraphics graphics, Font font, int mouseX, int mouseY) {
        if (node.structuralInner()) {
            drawLabel(graphics, font, node.label(), labelColor());
            if (innerWidget != null) {
                innerWidget.render(graphics, font, mouseX, mouseY);
            }
        } else if (innerWidget != null) {
            innerWidget.render(graphics, font, mouseX, mouseY);
        }
    }

    @Override
    public boolean renderTooltip(GuiGraphics graphics, Font font, double mouseX, double mouseY) {
        if (node.structuralInner()) {
            if (renderSelfTooltip(graphics, font, mouseX, mouseY)) {
                return true;
            }
            return !isCollapsed() && innerWidget != null
                && innerWidget.renderTooltip(graphics, font, mouseX, mouseY);
        }
        return innerWidget != null && innerWidget.renderTooltip(graphics, font, mouseX, mouseY);
    }

    @Override
    public void refreshFromNode() {
        if (innerWidget != null) {
            innerWidget.refreshFromNode();
        }
    }

    @Override
    public void setHidden(boolean hidden) {
        super.setHidden(hidden);
        if (hidden && innerWidget != null) {
            innerWidget.setHidden(true);
        }
    }

    @Override
    protected void onDispose() {
        if (innerWidget != null) {
            innerWidget.dispose();
        }
    }

    private EditorWidget inner() {
        if (innerWidget == null && node.inner() != null) {
            innerWidget = node.inner().widget(host);
        }
        return innerWidget;
    }
}
